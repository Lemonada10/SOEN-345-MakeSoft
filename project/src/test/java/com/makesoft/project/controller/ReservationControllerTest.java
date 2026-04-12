package com.makesoft.project.controller;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.makesoft.project.model.Event;
import com.makesoft.project.model.Reservation;
import com.makesoft.project.model.User;
import com.makesoft.project.repository.EventRepository;
import com.makesoft.project.repository.ReservationRepository;
import com.makesoft.project.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    /** Future start time so the past-event check does not run before the zero-tickets check. */
    private static Date futureStartDate() {
        return new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000);
    }

    @Test
    void createReservation_rejectsWhenZeroTickets() throws Exception {
        User user = new User("U", "reszero@test.com", null, "p", "customer");
        userRepository.save(user);
        Event event = new Event("Sold Out", "desc", "loc", futureStartDate(), "AVAILABLE", "0", "sports");
        eventRepository.save(event);

        String body = "{\"userId\":" + user.getId() + ",\"eventId\":" + event.getId() + ",\"quantity\":1}";
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict())
                .andExpect(content().string("There is not even tickets for the quantity u chose."));
    }

    @Test
    void createReservation_rejectsPastEvent() throws Exception {
        User user = new User("U", "respast@test.com", null, "p", "customer");
        userRepository.save(user);
        Date past = new Date(System.currentTimeMillis() - 24L * 60 * 60 * 1000);
        Event event = new Event("Old Show", "desc", "loc", past, "AVAILABLE", "10", "sports");
        eventRepository.save(event);

        String body = "{\"userId\":" + user.getId() + ",\"eventId\":" + event.getId() + ",\"quantity\":1}";
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict())
                .andExpect(content().string("Cannot reserve a past event"));
    }

    @Test
    void createReservation_successDecrementsTickets() throws Exception {
        User user = new User("U", "ressuccess@test.com", null, "p", "customer");
        userRepository.save(user);
        Event event = new Event("Has Tix", "d", "loc", futureStartDate(), "AVAILABLE", "5", "sports");
        eventRepository.save(event);

        String body = "{\"userId\":" + user.getId() + ",\"eventId\":" + event.getId() + ",\"quantity\":1}";
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        Event reloaded = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(reloaded.getTicketRemaining()).isEqualTo("4");
        assertThat(reloaded.getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    void createReservation_rejectsBadRequest() throws Exception {
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":1,\"eventId\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReservation_rejectsWhenQuantityExceedsRemaining() throws Exception {
        User user = new User("U", "resexceed@test.com", null, "p", "customer");
        userRepository.save(user);
        Event event = new Event("Low Stock", "desc", "loc", futureStartDate(), "AVAILABLE", "2", "sports");
        eventRepository.save(event);

        String body = "{\"userId\":" + user.getId() + ",\"eventId\":" + event.getId() + ",\"quantity\":3}";
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict())
                .andExpect(content().string("Not enough tickets available"));
    }

    @Test
    void createReservation_rejectsDeletedEvent() throws Exception {
        User user = new User("U", "resdeleted@test.com", null, "p", "customer");
        userRepository.save(user);
        Event event = new Event("Cancelled", "desc", "loc", futureStartDate(), "DELETED", "8", "sports");
        eventRepository.save(event);

        String body = "{\"userId\":" + user.getId() + ",\"eventId\":" + event.getId() + ",\"quantity\":1}";
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict())
                .andExpect(content().string("This event has been cancelled by the organizer"));
    }

    @Test
    void createReservation_rejectsWhenUserMissing() throws Exception {
        Event event = new Event("Has Tix", "d", "loc", futureStartDate(), "AVAILABLE", "5", "sports");
        eventRepository.save(event);

        String body = "{\"userId\":9999999,\"eventId\":" + event.getId() + ",\"quantity\":1}";
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    void createReservation_rejectsWhenEventMissing() throws Exception {
        User user = new User("U", "reseventmissing@test.com", null, "p", "customer");
        userRepository.save(user);

        String body = "{\"userId\":" + user.getId() + ",\"eventId\":9999999,\"quantity\":1}";
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Event not found"));
    }

    @Test
    void createReservation_whenQuantityEqualsRemaining_setsFilled() throws Exception {
        User user = new User("U", "resequal@test.com", null, "p", "customer");
        userRepository.save(user);
        Event event = new Event("Exact", "d", "loc", futureStartDate(), "AVAILABLE", "2", "sports");
        eventRepository.save(event);

        String body = "{\"userId\":" + user.getId() + ",\"eventId\":" + event.getId() + ",\"quantity\":2}";
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        Event reloaded = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(reloaded.getTicketRemaining()).isEqualTo("0");
        assertThat(reloaded.getStatus()).isEqualTo("FILLED");
    }

    @Test
    void createReservation_allowsWhenTicketRemainingNotNumeric() throws Exception {
        User user = new User("U", "resnonnumeric@test.com", null, "p", "customer");
        userRepository.save(user);
        Event event = new Event("Open", "d", "loc", futureStartDate(), "AVAILABLE", "N/A", "sports");
        eventRepository.save(event);

        String body = "{\"userId\":" + user.getId() + ",\"eventId\":" + event.getId() + ",\"quantity\":1}";
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        Event reloaded = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(reloaded.getTicketRemaining()).isEqualTo("N/A");
    }

    @Test
    void listReservations_returnsArray() throws Exception {
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void cancelReservation_deletesExistingReservation() throws Exception {
        User user = new User("U", "rescancel@test.com", null, "p", "customer");
        userRepository.save(user);
        Event event = new Event("Cancelable", "d", "loc", futureStartDate(), "AVAILABLE", "6", "sports");
        eventRepository.save(event);
        Reservation reservation = reservationRepository.save(new Reservation(user, event, 1, "CONFIRMED", new Date()));

        mockMvc.perform(delete("/api/reservations/" + reservation.getReservation_id()))
                .andExpect(status().isNoContent());

        assertThat(reservationRepository.findById(reservation.getReservation_id())).isEmpty();
    }
}
