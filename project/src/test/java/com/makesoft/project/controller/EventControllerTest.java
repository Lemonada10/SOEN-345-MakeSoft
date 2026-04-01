package com.makesoft.project.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.makesoft.project.model.Event;
import com.makesoft.project.repository.EventRepository;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Test
    void listEvents_returnsOk() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getEvent_returns404WhenMissing() throws Exception {
        mockMvc.perform(get("/api/events/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEvent_returnsOkWhenExists() throws Exception {
        Date future = new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000);
        Event ev = new Event("Listed Show", "desc", "Hall", future, "AVAILABLE", "10", "concert");
        eventRepository.save(ev);

        mockMvc.perform(get("/api/events/" + ev.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Listed Show"));
    }

    @Test
    void getEvent_returns404WhenSoftDeleted() throws Exception {
        Date future = new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000);
        Event ev = new Event("Cancelled Show", "desc", "Hall", future, "DELETED", "10", "concert");
        eventRepository.save(ev);

        mockMvc.perform(get("/api/events/" + ev.getId()))
                .andExpect(status().isNotFound());
    }
}
