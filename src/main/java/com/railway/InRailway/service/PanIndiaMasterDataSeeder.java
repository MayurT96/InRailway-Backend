package com.railway.InRailway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.railway.InRailway.model.Route;
import com.railway.InRailway.model.RouteStation;
import com.railway.InRailway.model.Station;
import com.railway.InRailway.model.Train;
import com.railway.InRailway.model.TrainType;
import com.railway.InRailway.repository.RouteRepository;
import com.railway.InRailway.repository.StationRepository;
import com.railway.InRailway.repository.TrainRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PanIndiaMasterDataSeeder implements CommandLineRunner {
    private static final String STATIONS_URL = "https://raw.githubusercontent.com/datameet/railways/master/stations.json";
    private static final String TRAINS_URL = "https://raw.githubusercontent.com/datameet/railways/master/trains.json";
    private static final String SCHEDULES_URL = "https://raw.githubusercontent.com/datameet/railways/master/schedules.json";

    private final ObjectMapper mapper;
    private final StationRepository stations;
    private final TrainRepository trains;
    private final RouteRepository routes;
    private final com.railway.InRailway.repository.RouteStationRepository routeStations;
    private final boolean enabled;
    private final boolean schedulesEnabled;
    private final Path dataDirectory;
    private final HttpClient client = HttpClient.newHttpClient();

    public PanIndiaMasterDataSeeder(ObjectMapper mapper, StationRepository stations, TrainRepository trains,
                                    RouteRepository routes, com.railway.InRailway.repository.RouteStationRepository routeStations,
                                    @Value("${app.seed.pan-india.enabled:false}") boolean enabled,
                                    @Value("${app.seed.pan-india.schedules:false}") boolean schedulesEnabled,
                                    @Value("${app.seed.pan-india.data-dir:}") String dataDirectory) {
        this.mapper = mapper;
        this.stations = stations;
        this.trains = trains;
        this.routes = routes;
        this.routeStations = routeStations;
        this.enabled = enabled;
        this.schedulesEnabled = schedulesEnabled;
        this.dataDirectory = dataDirectory == null || dataDirectory.isBlank() ? null : Path.of(dataDirectory);
    }

    @Override
    public void run(String... args) {
        if (!enabled) return;
        try {
            int stationCount = importStations();
            int trainCount = importTrains();
            ensureTrainRoutes();
            int timetableCount = schedulesEnabled ? importSchedules() : 0;
            System.out.printf("Pan-India seed complete: %d stations, %d trains, %d timetable stops%n",
                    stationCount, trainCount, timetableCount);
        } catch (Exception e) {
            System.err.println("Warning: Pan-India dataset download/seeding skipped due to network or format error: " + e.getMessage());
        }
    }

    @Transactional
    int importStations() throws IOException, InterruptedException {
        int imported = 0;
        for (JsonNode feature : dataset(STATIONS_URL, "stations.json").path("features")) {
            JsonNode properties = feature.path("properties");
            String code = text(properties, "code");
            String name = text(properties, "name");
            String state = text(properties, "state");
            String zone = text(properties, "zone");
            if (code == null || name == null || state == null || zone == null || code.length() > 10) continue;
            Station station = stations.findByStationCodeIgnoreCase(code).orElseGet(Station::new);
            station.setStationCode(code.toUpperCase(Locale.ROOT));
            station.setStationName(name);
            station.setState(state);
            station.setZone(zone);
            JsonNode coordinates = feature.path("geometry").path("coordinates");
            if (coordinates.isArray() && coordinates.size() >= 2) {
                station.setLongitude(decimal(coordinates.get(0)));
                station.setLatitude(decimal(coordinates.get(1)));
            }
            station.setJunction(isJunction(name));
            station.setActive(true);
            stations.save(station);
            imported++;
        }
        return imported;
    }

    @Transactional
    int importTrains() throws IOException, InterruptedException {
        int imported = 0;
        for (JsonNode feature : dataset(TRAINS_URL, "trains.json").path("features")) {
            JsonNode properties = feature.path("properties");
            String number = firstText(properties, "number", "train_number");
            String name = firstText(properties, "name", "train_name");
            String source = firstText(properties, "from_station_code", "source_station_code");
            String destination = firstText(properties, "to_station_code", "destination_station_code");
            if (number == null || name == null || source == null || destination == null) continue;
            Station sourceStation = stations.findByStationCodeIgnoreCase(source).orElse(null);
            Station destinationStation = stations.findByStationCodeIgnoreCase(destination).orElse(null);
            if (sourceStation == null || destinationStation == null || trains.findByTrainNumber(number).isPresent()) continue;
            Train train = new Train();
            train.setTrainNumber(number);
            train.setTrainName(name);
            train.setTrainType(type(firstText(properties, "type", "train_type")));
            train.setSourceStation(sourceStation);
            train.setDestinationStation(destinationStation);
            train.setRunningDays("MON,TUE,WED,THU,FRI,SAT,SUN");
            train.setActive(true);
            trains.save(train);
            imported++;
        }
        return imported;
    }

    @Transactional
    int importSchedules() throws IOException, InterruptedException {
        Map<String, Route> routeByTrain = new HashMap<>();
        int imported = 0;
        for (JsonNode schedule : schedules()) {
            String number = firstText(schedule, "train_number", "number");
            String stationCode = firstText(schedule, "station_code", "code");
            if (number == null || stationCode == null) continue;
            Train train = trains.findByTrainNumber(number).orElse(null);
            Station station = stations.findByStationCodeIgnoreCase(stationCode).orElse(null);
            if (train == null || station == null) continue;
            Route route = routeByTrain.computeIfAbsent(number, key -> routes.findByTrainTrainNumber(key).orElseGet(() -> newRoute(train)));
            route = routes.save(route);
            routeByTrain.put(number, route);
            Route savedRoute = route;
            RouteStation stop = routeStations.findByRouteIdAndStationId(savedRoute.getId(), station.getId())
                    .orElseGet(() -> routeStations.findByRouteIdAndStopNumber(savedRoute.getId(), nextStopNumber(savedRoute.getId()))
                            .orElseGet(RouteStation::new));
            stop.setRoute(route);
            stop.setStation(station);
            if (stop.getStopNumber() == null) stop.setStopNumber(nextStopNumber(savedRoute.getId()));
            stop.setArrivalTime(time(firstText(schedule, "arrival", "arrivalTime")));
            stop.setDepartureTime(time(firstText(schedule, "departure", "departureTime")));
            routeStations.save(stop);
            imported++;
        }
        return imported;
    }

    @Transactional
    void ensureTrainRoutes() {
        for (Train train : trains.findAll()) {
            Route route = routes.findByTrainTrainNumber(train.getTrainNumber()).orElseGet(() -> newRoute(train));
            route = routes.save(route);
            addEndpoint(route, train.getSourceStation(), 1);
            addEndpoint(route, train.getDestinationStation(), 2);
        }
    }

    private void addEndpoint(Route route, Station station, int stopNumber) {
        if (routeStations.findByRouteIdAndStationId(route.getId(), station.getId()).isPresent()) return;
        if (routeStations.findByRouteIdAndStopNumber(route.getId(), stopNumber).isPresent()) return;
        RouteStation stop = new RouteStation();
        stop.setRoute(route);
        stop.setStation(station);
        stop.setStopNumber(stopNumber);
        stop.setHaltDurationMinutes(0);
        stop.setDistanceFromSource(BigDecimal.ZERO);
        routeStations.save(stop);
    }

    private JsonNode download(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofMinutes(2)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Railway dataset download failed: HTTP " + response.statusCode());
        return mapper.readTree(response.body());
    }

    private JsonNode dataset(String url, String fileName) throws IOException, InterruptedException {
        if (dataDirectory != null) {
            Path localFile = dataDirectory.resolve(fileName);
            if (Files.isRegularFile(localFile)) return mapper.readTree(localFile.toFile());
        }
        return download(url);
    }

    private ArrayList<JsonNode> schedules() throws IOException, InterruptedException {
        JsonNode root = dataset(SCHEDULES_URL, "schedules.json");
        ArrayList<JsonNode> schedules = new ArrayList<>();
        if (root.isArray()) root.forEach(schedules::add);
        else root.path("features").forEach(feature -> schedules.add(feature.path("properties")));
        schedules.sort(Comparator.comparing(item -> firstText(item, "train_number", "number") + item.path("day").asText()));
        return schedules;
    }

    private Route newRoute(Train train) {
        Route route = new Route();
        route.setTrain(train);
        route.setSource(train.getSourceStation());
        route.setDestination(train.getDestinationStation());
        route.setDistance(BigDecimal.ZERO);
        route.setDuration(Duration.ZERO);
        return route;
    }

    private static TrainType type(String value) {
        String type = value == null ? "EXPRESS" : value.toUpperCase(Locale.ROOT);
        if (type.contains("SUPERFAST")) return TrainType.SUPERFAST;
        if (type.contains("PASSENGER") || type.contains("MEMU") || type.contains("DEMU") || type.contains("LOCAL")) return TrainType.PASSENGER;
        if (type.contains("RAJDHANI") || type.contains("SHATABDI") || type.contains("VANDE") || type.contains("DURONTO") || type.contains("GARIB")) return TrainType.PREMIUM;
        return TrainType.EXPRESS;
    }

    private static boolean isJunction(String name) {
        String value = name.toUpperCase(Locale.ROOT);
        return value.contains("JUNCTION") || value.contains(" JN") || value.endsWith("JN") || value.contains("CENTRAL") || value.contains("TERMINUS");
    }

    private static String firstText(JsonNode node, String... names) { for (String name : names) { String value = text(node, name); if (value != null) return value; } return null; }
    private static String text(JsonNode node, String name) { JsonNode value = node.get(name); return value == null || value.isNull() || value.asText().isBlank() ? null : value.asText().trim(); }
    private static BigDecimal decimal(JsonNode node) { return node == null || node.isNull() || node.asText().isBlank() ? null : new BigDecimal(node.asText()); }
    private static LocalTime time(String value) { return value == null || value.equalsIgnoreCase("none") ? null : LocalTime.parse(value); }
    private int nextStopNumber(Long routeId) { return (int) routeStations.countByRouteId(routeId) + 1; }
}
