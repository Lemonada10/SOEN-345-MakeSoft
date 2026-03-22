package com.makesoft.project.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.makesoft.project.model.Event;
import com.makesoft.project.repository.EventRepository;

@SpringBootTest
class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    private static Date daysFromNow(int days) {
        return new Date(System.currentTimeMillis() + days * 24L * 60 * 60 * 1000);
    }

    private static Date daysAgo(int days) {
        return new Date(System.currentTimeMillis() - days * 24L * 60 * 60 * 1000);
    }

    @Test
    void viewEvents_marksPastEventAsPassed() {
        Event past = new Event("Past Gig", "d", "loc", daysAgo(2), "AVAILABLE", "5", "music");
        eventRepository.save(past);
        Long id = past.getId();

        eventService.viewEvents();

        Event reloaded = eventRepository.findById(id).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo("PASSED");
    }

    @Test
    void viewEvents_leavesFutureEventStatusUnchanged() {
        Event future = new Event("Future Gig", "d", "loc", daysFromNow(10), "AVAILABLE", "5", "music");
        eventRepository.save(future);
        Long id = future.getId();

        eventService.viewEvents();

        Event reloaded = eventRepository.findById(id).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    void addEvent_setsFilledWhenZeroTickets() {
        Event e = new Event("Full", "d", "loc", daysFromNow(1), "AVAILABLE", "0", "sports");
        eventService.addEvent(e);
        assertThat(e.getId()).isNotNull();
        Event saved = eventRepository.findById(e.getId()).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo("FILLED");
    }
}
