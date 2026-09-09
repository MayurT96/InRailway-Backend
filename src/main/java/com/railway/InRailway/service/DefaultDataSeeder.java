package com.railway.InRailway.service;

import com.railway.InRailway.model.*;
import com.railway.InRailway.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(1)
public class DefaultDataSeeder implements CommandLineRunner {

    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final RouteStationRepository routeStationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultDataSeeder(
            StationRepository stationRepository,
            TrainRepository trainRepository,
            RouteRepository routeRepository,
            RouteStationRepository routeStationRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.stationRepository = stationRepository;
        this.trainRepository = trainRepository;
        this.routeRepository = routeRepository;
        this.routeStationRepository = routeStationRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedStationsAndTrains();
    }

    private void seedUsers() {
        createUserIfAbsent("admin", "admin@inrailway.com", "Admin@123", Role.ADMIN);
        createUserIfAbsent("user", "user@inrailway.com", "User@123", Role.USER);
        createUserIfAbsent("mayur", "mayur@inrailway.com", "Mayur@123", Role.USER);
    }

    private void createUserIfAbsent(String username, String email, String rawPassword, Role role) {
        if (userRepository.findByUsername(username).isEmpty() && userRepository.findByEmail(email).isEmpty()) {
            User u = new User();
            u.setUsername(username);
            u.setEmail(email);
            u.setPassword(passwordEncoder.encode(rawPassword));
            u.setRole(role);
            userRepository.save(u);
        }
    }

    private void seedStationsAndTrains() {
        Map<String, Station> stations = new HashMap<>();

        // Major Northern Stations
        stations.put("NDLS", createStation("NDLS", "New Delhi", "Delhi", "NR", new BigDecimal("28.6143"), new BigDecimal("77.2197"), true));
        stations.put("DLI", createStation("DLI", "Old Delhi Junction", "Delhi", "NR", new BigDecimal("28.6606"), new BigDecimal("77.2274"), true));
        stations.put("NZM", createStation("NZM", "Hazrat Nizamuddin", "Delhi", "NR", new BigDecimal("28.5888"), new BigDecimal("77.2536"), true));
        stations.put("ANVT", createStation("ANVT", "Anand Vihar Terminal", "Delhi", "NR", new BigDecimal("28.6469"), new BigDecimal("77.3150"), true));
        stations.put("AGC", createStation("AGC", "Agra Cantt", "Uttar Pradesh", "NCR", new BigDecimal("27.1578"), new BigDecimal("78.0081"), true));
        stations.put("CNB", createStation("CNB", "Kanpur Central", "Uttar Pradesh", "NCR", new BigDecimal("26.4547"), new BigDecimal("80.3507"), true));
        stations.put("LKO", createStation("LKO", "Lucknow Charbagh", "Uttar Pradesh", "NR", new BigDecimal("26.8322"), new BigDecimal("80.9238"), true));
        stations.put("BSB", createStation("BSB", "Varanasi Junction", "Uttar Pradesh", "NR", new BigDecimal("25.3284"), new BigDecimal("82.9863"), true));
        stations.put("PRYJ", createStation("PRYJ", "Prayagraj Junction", "Uttar Pradesh", "NCR", new BigDecimal("25.4484"), new BigDecimal("81.8333"), true));
        stations.put("DDU", createStation("DDU", "Pt. Deen Dayal Upadhyaya Jn", "Uttar Pradesh", "ECR", new BigDecimal("25.2818"), new BigDecimal("83.1189"), true));
        stations.put("GKP", createStation("GKP", "Gorakhpur Junction", "Uttar Pradesh", "NER", new BigDecimal("26.7598"), new BigDecimal("83.3813"), true));

        // Western & Central Stations
        stations.put("MMCT", createStation("MMCT", "Mumbai Central", "Maharashtra", "WR", new BigDecimal("18.9696"), new BigDecimal("72.8193"), true));
        stations.put("CSMT", createStation("CSMT", "Chhatrapati Shivaji Maharaj Terminus", "Maharashtra", "CR", new BigDecimal("18.9400"), new BigDecimal("72.8353"), true));
        stations.put("BDTS", createStation("BDTS", "Bandra Terminus", "Maharashtra", "WR", new BigDecimal("19.0626"), new BigDecimal("72.8427"), true));
        stations.put("LTT", createStation("LTT", "Lokmanya Tilak Terminus", "Maharashtra", "CR", new BigDecimal("19.0699"), new BigDecimal("72.8913"), true));
        stations.put("PUNE", createStation("PUNE", "Pune Junction", "Maharashtra", "CR", new BigDecimal("18.5284"), new BigDecimal("73.8744"), true));
        stations.put("ST", createStation("ST", "Surat", "Gujarat", "WR", new BigDecimal("21.2049"), new BigDecimal("72.8406"), true));
        stations.put("BRC", createStation("BRC", "Vadodara Junction", "Gujarat", "WR", new BigDecimal("22.3107"), new BigDecimal("73.1812"), true));
        stations.put("ADI", createStation("ADI", "Ahmedabad Junction", "Gujarat", "WR", new BigDecimal("23.0225"), new BigDecimal("72.6022"), true));
        stations.put("GNC", createStation("GNC", "Gandhinagar Capital", "Gujarat", "WR", new BigDecimal("23.2355"), new BigDecimal("72.6369"), false));
        stations.put("KOTA", createStation("KOTA", "Kota Junction", "Rajasthan", "WCR", new BigDecimal("25.2138"), new BigDecimal("75.8648"), true));
        stations.put("RTM", createStation("RTM", "Ratlam Junction", "Madhya Pradesh", "WR", new BigDecimal("23.3441"), new BigDecimal("75.0371"), true));
        stations.put("JP", createStation("JP", "Jaipur Junction", "Rajasthan", "NWR", new BigDecimal("26.9200"), new BigDecimal("75.7878"), true));
        stations.put("GWL", createStation("GWL", "Gwalior Junction", "Madhya Pradesh", "NCR", new BigDecimal("26.2163"), new BigDecimal("78.1887"), true));
        stations.put("VGLJ", createStation("VGLJ", "VGL Jhansi Junction", "Uttar Pradesh", "NCR", new BigDecimal("25.4484"), new BigDecimal("78.5685"), true));
        stations.put("BPL", createStation("BPL", "Bhopal Junction", "Madhya Pradesh", "WCR", new BigDecimal("23.2667"), new BigDecimal("77.4167"), true));
        stations.put("RKMP", createStation("RKMP", "Rani Kamlapati", "Madhya Pradesh", "WCR", new BigDecimal("23.2089"), new BigDecimal("77.4395"), true));
        stations.put("NGP", createStation("NGP", "Nagpur Junction", "Maharashtra", "CR", new BigDecimal("21.1524"), new BigDecimal("79.0888"), true));

        // Eastern Stations
        stations.put("HWH", createStation("HWH", "Howrah Junction", "West Bengal", "ER", new BigDecimal("22.5839"), new BigDecimal("88.3426"), true));
        stations.put("SDAH", createStation("SDAH", "Sealdah", "West Bengal", "ER", new BigDecimal("22.5675"), new BigDecimal("88.3711"), true));
        stations.put("PNBE", createStation("PNBE", "Patna Junction", "Bihar", "ECR", new BigDecimal("25.6022"), new BigDecimal("85.1376"), true));
        stations.put("GHY", createStation("GHY", "Guwahati", "Assam", "NFR", new BigDecimal("26.1827"), new BigDecimal("91.7516"), true));
        stations.put("BBS", createStation("BBS", "Bhubaneswar", "Odisha", "ECoR", new BigDecimal("20.2668"), new BigDecimal("85.8436"), true));
        stations.put("PURI", createStation("PURI", "Puri", "Odisha", "ECoR", new BigDecimal("19.8135"), new BigDecimal("85.8312"), true));

        // Southern Stations
        stations.put("MAS", createStation("MAS", "MGR Chennai Central", "Tamil Nadu", "SR", new BigDecimal("13.0827"), new BigDecimal("80.2707"), true));
        stations.put("SBC", createStation("SBC", "KSR Bengaluru City Junction", "Karnataka", "SWR", new BigDecimal("12.9784"), new BigDecimal("77.5695"), true));
        stations.put("YPR", createStation("YPR", "Yesvantpur Junction", "Karnataka", "SWR", new BigDecimal("13.0238"), new BigDecimal("77.5501"), true));
        stations.put("SC", createStation("SC", "Secunderabad Junction", "Telangana", "SCR", new BigDecimal("17.4344"), new BigDecimal("78.5013"), true));
        stations.put("HYB", createStation("HYB", "Hyderabad Deccan", "Telangana", "SCR", new BigDecimal("17.3920"), new BigDecimal("78.4678"), true));
        stations.put("BZA", createStation("BZA", "Vijayawada Junction", "Andhra Pradesh", "SCR", new BigDecimal("16.5186"), new BigDecimal("80.6200"), true));
        stations.put("TVC", createStation("TVC", "Thiruvananthapuram Central", "Kerala", "SR", new BigDecimal("8.4875"), new BigDecimal("76.9526"), true));
        stations.put("ERS", createStation("ERS", "Ernakulam Junction", "Kerala", "SR", new BigDecimal("9.9688"), new BigDecimal("76.2942"), true));

        // Save all stations
        for (Map.Entry<String, Station> entry : stations.entrySet()) {
            Station s = stationRepository.findByStationCodeIgnoreCase(entry.getKey())
                    .orElseGet(() -> stationRepository.save(entry.getValue()));
            stations.put(entry.getKey(), s);
        }

        // Seed Trains with Complete Routes & Stops

        // 1. Mumbai Tejas Rajdhani Express (12952 / 12951)
        createTrainWithRoute("12952", "Mumbai Central Tejas Rajdhani", TrainType.PREMIUM,
                stations.get("NDLS"), stations.get("MMCT"), "DAILY",
                new BigDecimal("1386.00"), Duration.ofHours(15).plusMinutes(35),
                new StopDef[]{
                        new StopDef(stations.get("NDLS"), 1, null, LocalTime.of(16, 55)),
                        new StopDef(stations.get("KOTA"), 2, LocalTime.of(21, 30), LocalTime.of(21, 40)),
                        new StopDef(stations.get("RTM"), 3, LocalTime.of(0, 48), LocalTime.of(0, 50)),
                        new StopDef(stations.get("BRC"), 4, LocalTime.of(3, 50), LocalTime.of(4, 0)),
                        new StopDef(stations.get("ST"), 5, LocalTime.of(5, 13), LocalTime.of(5, 18)),
                        new StopDef(stations.get("MMCT"), 6, LocalTime.of(8, 30), null)
                });

        createTrainWithRoute("12951", "New Delhi Tejas Rajdhani", TrainType.PREMIUM,
                stations.get("MMCT"), stations.get("NDLS"), "DAILY",
                new BigDecimal("1386.00"), Duration.ofHours(15).plusMinutes(50),
                new StopDef[]{
                        new StopDef(stations.get("MMCT"), 1, null, LocalTime.of(17, 0)),
                        new StopDef(stations.get("ST"), 2, LocalTime.of(19, 43), LocalTime.of(19, 48)),
                        new StopDef(stations.get("BRC"), 3, LocalTime.of(21, 6), LocalTime.of(21, 16)),
                        new StopDef(stations.get("RTM"), 4, LocalTime.of(0, 25), LocalTime.of(0, 28)),
                        new StopDef(stations.get("KOTA"), 5, LocalTime.of(3, 15), LocalTime.of(3, 20)),
                        new StopDef(stations.get("NDLS"), 6, LocalTime.of(8, 50), null)
                });

        // 2. Vande Bharat Express (22436 / 22435: NDLS - BSB)
        createTrainWithRoute("22436", "Vande Bharat Express (NDLS-BSB)", TrainType.PREMIUM,
                stations.get("NDLS"), stations.get("BSB"), "MON,TUE,WED,FRI,SAT,SUN",
                new BigDecimal("759.00"), Duration.ofHours(8),
                new StopDef[]{
                        new StopDef(stations.get("NDLS"), 1, null, LocalTime.of(6, 0)),
                        new StopDef(stations.get("CNB"), 2, LocalTime.of(10, 8), LocalTime.of(10, 10)),
                        new StopDef(stations.get("PRYJ"), 3, LocalTime.of(12, 8), LocalTime.of(12, 10)),
                        new StopDef(stations.get("BSB"), 4, LocalTime.of(14, 0), null)
                });

        createTrainWithRoute("22435", "Vande Bharat Express (BSB-NDLS)", TrainType.PREMIUM,
                stations.get("BSB"), stations.get("NDLS"), "MON,TUE,WED,FRI,SAT,SUN",
                new BigDecimal("759.00"), Duration.ofHours(8),
                new StopDef[]{
                        new StopDef(stations.get("BSB"), 1, null, LocalTime.of(15, 0)),
                        new StopDef(stations.get("PRYJ"), 2, LocalTime.of(16, 30), LocalTime.of(16, 32)),
                        new StopDef(stations.get("CNB"), 3, LocalTime.of(18, 30), LocalTime.of(18, 32)),
                        new StopDef(stations.get("NDLS"), 4, LocalTime.of(23, 0), null)
                });

        // 3. Vande Bharat Express (20901 / 20902: MMCT - GNC)
        createTrainWithRoute("20901", "Vande Bharat Express (MMCT-GNC)", TrainType.PREMIUM,
                stations.get("MMCT"), stations.get("GNC"), "MON,TUE,WED,THU,FRI,SAT",
                new BigDecimal("522.00"), Duration.ofHours(6).plusMinutes(25),
                new StopDef[]{
                        new StopDef(stations.get("MMCT"), 1, null, LocalTime.of(6, 0)),
                        new StopDef(stations.get("ST"), 2, LocalTime.of(8, 50), LocalTime.of(8, 53)),
                        new StopDef(stations.get("BRC"), 3, LocalTime.of(10, 13), LocalTime.of(10, 16)),
                        new StopDef(stations.get("ADI"), 4, LocalTime.of(11, 25), LocalTime.of(11, 30)),
                        new StopDef(stations.get("GNC"), 5, LocalTime.of(12, 25), null)
                });

        // 4. Lucknow Swarna Shatabdi Express (12004 / 12003: NDLS - LKO)
        createTrainWithRoute("12004", "Lucknow Swarna Shatabdi", TrainType.PREMIUM,
                stations.get("NDLS"), stations.get("LKO"), "DAILY",
                new BigDecimal("512.00"), Duration.ofHours(6).plusMinutes(40),
                new StopDef[]{
                        new StopDef(stations.get("NDLS"), 1, null, LocalTime.of(6, 10)),
                        new StopDef(stations.get("CNB"), 2, LocalTime.of(11, 20), LocalTime.of(11, 25)),
                        new StopDef(stations.get("LKO"), 3, LocalTime.of(12, 50), null)
                });

        // 5. Bhopal Shatabdi Express (12002 / 12001: NDLS - RKMP)
        createTrainWithRoute("12002", "Bhopal Shatabdi Express", TrainType.PREMIUM,
                stations.get("NDLS"), stations.get("RKMP"), "DAILY",
                new BigDecimal("708.00"), Duration.ofHours(8).plusMinutes(25),
                new StopDef[]{
                        new StopDef(stations.get("NDLS"), 1, null, LocalTime.of(6, 0)),
                        new StopDef(stations.get("AGC"), 2, LocalTime.of(7, 50), LocalTime.of(7, 55)),
                        new StopDef(stations.get("GWL"), 3, LocalTime.of(9, 23), LocalTime.of(9, 28)),
                        new StopDef(stations.get("VGLJ"), 4, LocalTime.of(10, 45), LocalTime.of(10, 53)),
                        new StopDef(stations.get("BPL"), 5, LocalTime.of(14, 0), LocalTime.of(14, 5)),
                        new StopDef(stations.get("RKMP"), 6, LocalTime.of(14, 25), null)
                });

        // 6. Howrah Rajdhani Express (12302 / 12301: NDLS - HWH)
        createTrainWithRoute("12302", "Howrah Rajdhani Express", TrainType.PREMIUM,
                stations.get("NDLS"), stations.get("HWH"), "MON,TUE,WED,THU,SAT,SUN",
                new BigDecimal("1451.00"), Duration.ofHours(17).plusMinutes(15),
                new StopDef[]{
                        new StopDef(stations.get("NDLS"), 1, null, LocalTime.of(16, 50)),
                        new StopDef(stations.get("CNB"), 2, LocalTime.of(21, 32), LocalTime.of(21, 37)),
                        new StopDef(stations.get("PRYJ"), 3, LocalTime.of(23, 43), LocalTime.of(23, 45)),
                        new StopDef(stations.get("DDU"), 4, LocalTime.of(1, 42), LocalTime.of(1, 52)),
                        new StopDef(stations.get("PNBE"), 5, LocalTime.of(4, 40), LocalTime.of(4, 50)),
                        new StopDef(stations.get("HWH"), 6, LocalTime.of(10, 5), null)
                });

        // 7. Kerala Express (12626 / 12625: NDLS - TVC)
        createTrainWithRoute("12626", "Kerala Express", TrainType.SUPERFAST,
                stations.get("NDLS"), stations.get("TVC"), "DAILY",
                new BigDecimal("3031.00"), Duration.ofHours(48),
                new StopDef[]{
                        new StopDef(stations.get("NDLS"), 1, null, LocalTime.of(20, 10)),
                        new StopDef(stations.get("AGC"), 2, LocalTime.of(22, 20), LocalTime.of(22, 25)),
                        new StopDef(stations.get("GWL"), 3, LocalTime.of(23, 43), LocalTime.of(23, 45)),
                        new StopDef(stations.get("VGLJ"), 4, LocalTime.of(1, 30), LocalTime.of(1, 38)),
                        new StopDef(stations.get("BPL"), 5, LocalTime.of(5, 20), LocalTime.of(5, 25)),
                        new StopDef(stations.get("NGP"), 6, LocalTime.of(11, 45), LocalTime.of(11, 50)),
                        new StopDef(stations.get("BZA"), 7, LocalTime.of(19, 15), LocalTime.of(19, 25)),
                        new StopDef(stations.get("MAS"), 8, LocalTime.of(2, 40), LocalTime.of(3, 5)),
                        new StopDef(stations.get("ERS"), 9, LocalTime.of(14, 55), LocalTime.of(15, 0)),
                        new StopDef(stations.get("TVC"), 10, LocalTime.of(20, 10), null)
                });

        // 8. Gorakhdham Express (12556 / 12555: NDLS - GKP)
        createTrainWithRoute("12556", "Gorakhdham Superfast Express", TrainType.SUPERFAST,
                stations.get("NDLS"), stations.get("GKP"), "DAILY",
                new BigDecimal("788.00"), Duration.ofHours(14).plusMinutes(25),
                new StopDef[]{
                        new StopDef(stations.get("NDLS"), 1, null, LocalTime.of(21, 25)),
                        new StopDef(stations.get("CNB"), 2, LocalTime.of(3, 0), LocalTime.of(3, 5)),
                        new StopDef(stations.get("LKO"), 3, LocalTime.of(4, 55), LocalTime.of(5, 5)),
                        new StopDef(stations.get("GKP"), 4, LocalTime.of(9, 45), null)
                });

        // 9. Punjab Mail (12138: NDLS - CSMT)
        createTrainWithRoute("12138", "Punjab Mail", TrainType.SUPERFAST,
                stations.get("NDLS"), stations.get("CSMT"), "DAILY",
                new BigDecimal("1544.00"), Duration.ofHours(25).plusMinutes(15),
                new StopDef[]{
                        new StopDef(stations.get("NDLS"), 1, null, LocalTime.of(5, 15)),
                        new StopDef(stations.get("AGC"), 2, LocalTime.of(7, 30), LocalTime.of(7, 35)),
                        new StopDef(stations.get("GWL"), 3, LocalTime.of(9, 41), LocalTime.of(9, 43)),
                        new StopDef(stations.get("VGLJ"), 4, LocalTime.of(12, 5), LocalTime.of(12, 13)),
                        new StopDef(stations.get("BPL"), 5, LocalTime.of(16, 35), LocalTime.of(16, 40)),
                        new StopDef(stations.get("NGP"), 6, LocalTime.of(23, 5), LocalTime.of(23, 10)),
                        new StopDef(stations.get("CSMT"), 7, LocalTime.of(7, 35), null)
                });

        // 10. Karnataka Express (12628: NDLS - SBC)
        createTrainWithRoute("12628", "Karnataka Express", TrainType.SUPERFAST,
                stations.get("NDLS"), stations.get("SBC"), "DAILY",
                new BigDecimal("2410.00"), Duration.ofHours(38).plusMinutes(40),
                new StopDef[]{
                        new StopDef(stations.get("NDLS"), 1, null, LocalTime.of(20, 20)),
                        new StopDef(stations.get("AGC"), 2, LocalTime.of(22, 48), LocalTime.of(22, 50)),
                        new StopDef(stations.get("GWL"), 3, LocalTime.of(0, 16), LocalTime.of(0, 18)),
                        new StopDef(stations.get("VGLJ"), 4, LocalTime.of(2, 2), LocalTime.of(2, 10)),
                        new StopDef(stations.get("BPL"), 5, LocalTime.of(6, 25), LocalTime.of(6, 30)),
                        new StopDef(stations.get("NGP"), 6, LocalTime.of(12, 50), LocalTime.of(12, 55)),
                        new StopDef(stations.get("SC"), 7, LocalTime.of(21, 30), LocalTime.of(21, 40)),
                        new StopDef(stations.get("SBC"), 8, LocalTime.of(12, 0), null)
                });
    }

    private Station createStation(String code, String name, String state, String zone, BigDecimal lat, BigDecimal lon, boolean junction) {
        Station s = new Station();
        s.setStationCode(code);
        s.setStationName(name);
        s.setState(state);
        s.setZone(zone);
        s.setLatitude(lat);
        s.setLongitude(lon);
        s.setJunction(junction);
        s.setActive(true);
        return s;
    }

    private record StopDef(Station station, int stopNumber, LocalTime arrival, LocalTime departure) {}

    private void createTrainWithRoute(
            String number, String name, TrainType type,
            Station source, Station dest, String runningDays,
            BigDecimal distance, Duration duration,
            StopDef[] stops) {

        Train train = trainRepository.findByTrainNumber(number).orElseGet(() -> {
            Train t = new Train();
            t.setTrainNumber(number);
            t.setTrainName(name);
            t.setTrainType(type);
            t.setSourceStation(source);
            t.setDestinationStation(dest);
            t.setRunningDays(runningDays);
            t.setActive(true);
            return trainRepository.save(t);
        });

        Route route = routeRepository.findByTrainTrainNumber(number).orElseGet(() -> {
            Route r = new Route();
            r.setTrain(train);
            r.setSource(source);
            r.setDestination(dest);
            r.setDistance(distance);
            r.setDuration(duration);
            return routeRepository.save(r);
        });

        for (StopDef s : stops) {
            if (routeStationRepository.findByRouteIdAndStationId(route.getId(), s.station.getId()).isEmpty()) {
                RouteStation rs = new RouteStation();
                rs.setRoute(route);
                rs.setStation(s.station);
                rs.setStopNumber(s.stopNumber);
                rs.setArrivalTime(s.arrival);
                rs.setDepartureTime(s.departure);
                rs.setHaltDurationMinutes(2);
                rs.setDistanceFromSource(BigDecimal.ZERO);
                routeStationRepository.save(rs);
            }
        }
    }
}
