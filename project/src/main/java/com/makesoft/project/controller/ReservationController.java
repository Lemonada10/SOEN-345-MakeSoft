package com.makesoft.project.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.makesoft.project.model.Event;
import com.makesoft.project.model.Reservation;
import com.makesoft.project.model.User;
import com.makesoft.project.repository.EventRepository;
import com.makesoft.project.repository.UserRepository;
import com.makesoft.project.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(originPatterns = {"http://localhost:3000", "http://127.0.0.1:3000", "https://*.vercel.app"})
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public ReservationController(ReservationService reservationService,
                                 UserRepository userRepository,
                                 EventRepository eventRepository) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public static class ReservationRequest {
        public Long userId;
        public Long eventId;
        public Integer quantity;
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest req) {
        if (req == null || req.userId == null || req.eventId == null || req.quantity == null || req.quantity <= 0) {
            return ResponseEntity.badRequest().body("userId, eventId and positive quantity are required");
        }

        Optional<User> userOpt = userRepository.findById(req.userId);
        if (!userOpt.isPresent()) return ResponseEntity.status(404).body("User not found");

        Optional<Event> eventOpt = eventRepository.findById(req.eventId);
        if (!eventOpt.isPresent()) return ResponseEntity.status(404).body("Event not found");

        User user = userOpt.get();
        Event event = eventOpt.get();

        if (event.getStartDateTime() != null && event.getStartDateTime().before(new Date())) {
            return ResponseEntity.status(409).body("Cannot reserve a past event");
        }

        // Try to check ticket remaining if it's numeric. If not numeric or null, allow reservation.
        String ticketRemStr = event.getTicketRemaining();
        if (ticketRemStr != null) {
            try {
                int remaining = Integer.parseInt(ticketRemStr);
                if (remaining <= 0) {
                    return ResponseEntity.status(409).body("There is not even tickets for the quantity u chose.");
                }
                if (req.quantity > remaining) {
                    return ResponseEntity.status(409).body("Not enough tickets available");
                }
                // decrement and persist event remaining count
                int newRemaining = remaining - req.quantity;
                event.setTicketRemaining(String.valueOf(newRemaining));
                if (newRemaining == 0) event.setStatus("FILLED");
                eventRepository.save(event);
            } catch (NumberFormatException ex) {
                // ticketRemaining not numeric; skip availability enforcement
            }
        }

        // create reservation with default status and current date/time
        reservationService.addReservation(user, event, req.quantity, "CONFIRMED", new Date());

        return ResponseEntity.status(201).body("Reservation created");
    }

    @GetMapping
    public List<Reservation> listReservations() {
        return reservationService.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        // Optional: verify exists before deleting
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
