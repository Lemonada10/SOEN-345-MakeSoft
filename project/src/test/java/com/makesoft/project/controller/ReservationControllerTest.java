package com.makesoft.project.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import com.makesoft.project.model.Event;
import com.makesoft.project.model.User;
import com.makesoft.project.repository.EventRepository;
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
}
