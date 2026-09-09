package com.railway.InRailway.service;

import com.railway.InRailway.dto.BookingDtos;
import com.railway.InRailway.exception.ApiException;
import com.railway.InRailway.model.*;
import com.railway.InRailway.repository.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepository bookings;
    private final UserRepository users;
    private final TrainService trains;
    private final StationService stations;
    private final PNRRepository pnrs;

    public BookingService(BookingRepository bookings, UserRepository users, TrainService trains, StationService stations, PNRRepository pnrs) {
        this.bookings = bookings;
        this.users = users;
        this.trains = trains;
        this.stations = stations;
        this.pnrs = pnrs;
    }

    @Transactional
    public BookingDtos.Response book(BookingDtos.Request r, String username) {
        Booking b = new Booking();
        String effectiveUsername = username != null && !username.isBlank() ? username : "guest";
        User user = users.findByUsername(effectiveUsername)
                .or(() -> users.findAll().stream().findFirst())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(effectiveUsername);
                    newUser.setEmail(effectiveUsername.contains("@") ? effectiveUsername : effectiveUsername + "@railsetu.in");
                    newUser.setPassword("guest123");
                    newUser.setRole(Role.USER);
                    return users.save(newUser);
                });
        b.setUser(user);

        Train train = trains.find(r.trainId());
        b.setTrain(train);

        Station sourceStation = (r.sourceStationCode() != null && !r.sourceStationCode().isBlank())
                ? stations.find(r.sourceStationCode())
                : train.getSourceStation();
        Station destStation = (r.destinationStationCode() != null && !r.destinationStationCode().isBlank())
                ? stations.find(r.destinationStationCode())
                : train.getDestinationStation();

        b.setSource(sourceStation);
        b.setDestination(destStation);
        b.setJourneyDate(r.journeyDate() != null ? r.journeyDate() : LocalDate.now().plusDays(1));
        b.setBookingTime(LocalDateTime.now());
        b.setBookingStatus(BookingStatus.CONFIRMED);
        b.setPnrNumber("IR" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());

        List<Passenger> passengerList = new ArrayList<>();
        if (r.passengers() != null && !r.passengers().isEmpty()) {
            for (var p : r.passengers()) {
                Passenger passenger = new Passenger();
                passenger.setBooking(b);
                passenger.setName(p.name() != null && !p.name().isBlank() ? p.name() : "Passenger");
                passenger.setAge(p.age() != null && p.age() > 0 ? p.age() : 25);
                passenger.setGender(p.gender() != null && !p.gender().isBlank() ? p.gender() : "M");
                passenger.setBerthPreference(p.berthPreference() != null ? p.berthPreference() : "LOWER");
                passengerList.add(passenger);
            }
        } else {
            Passenger passenger = new Passenger();
            passenger.setBooking(b);
            passenger.setName(r.passengerName() != null && !r.passengerName().isBlank() ? r.passengerName() : (effectiveUsername.contains("@") ? effectiveUsername.split("@")[0] : effectiveUsername));
            passenger.setAge(r.passengerAge() != null && r.passengerAge() > 0 ? r.passengerAge() : 28);
            passenger.setGender("M");
            passenger.setBerthPreference("LOWER");
            passengerList.add(passenger);
        }
        b.setPassengers(passengerList);

        Booking saved = bookings.save(b);
        PNR pnr = new PNR();
        pnr.setPnrNumber(saved.getPnrNumber());
        pnr.setBooking(saved);
        pnr.setStatus(saved.getBookingStatus());
        pnrs.save(pnr);
        return response(saved);
    }

    @Transactional
    public BookingDtos.Response cancel(Long id, String username) {
        Booking b = bookings.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (username != null && !username.isBlank() && b.getUser() != null && !b.getUser().getUsername().equalsIgnoreCase(username)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Booking belongs to another user");
        }
        b.setBookingStatus(BookingStatus.CANCELLED);
        pnrs.findByPnrNumber(b.getPnrNumber()).ifPresent(p -> {
            p.setStatus(BookingStatus.CANCELLED);
            pnrs.save(p);
        });
        return response(bookings.save(b));
    }

    @Transactional(readOnly = true)
    public Page<BookingDtos.Response> history(String username, Pageable pageable) {
        if (username == null || username.isBlank()) {
            return bookings.findAll(pageable).map(this::response);
        }
        Page<Booking> page = bookings.findByUserUsernameOrderByBookingTimeDesc(username, pageable);
        if (page.isEmpty()) {
            return bookings.findAll(pageable).map(this::response);
        }
        return page.map(this::response);
    }

    @Transactional(readOnly = true)
    public BookingDtos.Response pnr(String number) {
        return response(pnrs.findByPnrNumber(number).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PNR not found")).getBooking());
    }

    public BookingDtos.Response response(Booking b) {
        String firstPassengerName = b.getPassengers() != null && !b.getPassengers().isEmpty()
                ? b.getPassengers().get(0).getName()
                : (b.getUser() != null ? b.getUser().getUsername() : "Passenger");
        Integer firstPassengerAge = b.getPassengers() != null && !b.getPassengers().isEmpty()
                ? b.getPassengers().get(0).getAge()
                : 25;

        List<BookingDtos.PassengerRequest> passengerDtos = b.getPassengers() != null
                ? b.getPassengers().stream().map(p -> new BookingDtos.PassengerRequest(p.getName(), p.getAge(), p.getGender(), p.getBerthPreference())).toList()
                : List.of();

        String trainNumber = b.getTrain() != null ? b.getTrain().getTrainNumber() : "";
        String trainName = b.getTrain() != null ? b.getTrain().getTrainName() : "";
        String srcCode = b.getSource() != null ? b.getSource().getStationCode() : (b.getTrain() != null && b.getTrain().getSourceStation() != null ? b.getTrain().getSourceStation().getStationCode() : "");
        String dstCode = b.getDestination() != null ? b.getDestination().getStationCode() : (b.getTrain() != null && b.getTrain().getDestinationStation() != null ? b.getTrain().getDestinationStation().getStationCode() : "");

        return new BookingDtos.Response(
                b.getId(),
                b.getPnrNumber(),
                trainNumber,
                trainName,
                srcCode,
                dstCode,
                b.getJourneyDate(),
                b.getBookingStatus() != null ? b.getBookingStatus().name() : "CONFIRMED",
                b.getBookingTime(),
                firstPassengerName,
                firstPassengerAge,
                passengerDtos
        );
    }
}
