package com.makesoft.project.service;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.makesoft.project.model.Event;
import com.makesoft.project.model.Reservation;
import com.makesoft.project.model.User;
import com.makesoft.project.repository.EventRepository;
import com.makesoft.project.repository.ReservationRepository;
import com.makesoft.project.repository.UserRepository;

@SpringBootTest
class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

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

    @Test
    void cancelEvent_setsDeletedAndKeepsRow() {
        Event ev = new Event("To Cancel", "d", "loc", daysFromNow(5), "AVAILABLE", "10", "music");
        eventRepository.save(ev);
        Long id = ev.getId();

        eventService.cancelEvent(id);

        Event reloaded = eventRepository.findById(id).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo("DELETED");
    }

    @Test
    void cancelEvent_withReservation_keepsRowAndSetsDeleted() {
        Event ev = new Event("Booked", "d", "loc", daysFromNow(5), "AVAILABLE", "10", "music");
        eventRepository.save(ev);
        User u = new User("Cust", "c@x.com", "", "pw", "customer");
        userRepository.save(u);
        Reservation r = new Reservation(u, ev, 1, "CONFIRMED", new Date());
        reservationRepository.save(r);
        Long id = ev.getId();

        eventService.cancelEvent(id);

        Event reloaded = eventRepository.findById(id).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo("DELETED");
        assertThat(reservationRepository.findById(r.getReservation_id())).isPresent();
    }

    @Test
    void viewEvents_excludesDeletedByDefault() {
        Event active = new Event("Active", "d", "loc", daysFromNow(3), "AVAILABLE", "5", "music");
        eventRepository.save(active);
        Event deleted = new Event("Gone", "d", "loc", daysFromNow(3), "DELETED", "5", "music");
        eventRepository.save(deleted);

        assertThat(eventService.viewEvents(false).stream().map(Event::getId))
                .contains(active.getId())
                .doesNotContain(deleted.getId());
    }

    @Test
    void filterByLocation_returnsOnlyMatchingNonDeletedByDefault() {
        Event match = new Event("M1", "d", "Montreal", daysFromNow(4), "AVAILABLE", "5", "music");
        eventRepository.save(match);
        Event deleted = new Event("M2", "d", "Montreal", daysFromNow(4), "DELETED", "5", "music");
        eventRepository.save(deleted);
        Event other = new Event("T", "d", "Toronto", daysFromNow(4), "AVAILABLE", "5", "music");
        eventRepository.save(other);

        assertThat(eventService.filterByLocation("Montreal", false).stream().map(Event::getId))
                .contains(match.getId())
                .doesNotContain(deleted.getId(), other.getId());
    }

    @Test
    void filterByLocation_includeDeletedTrue_keepsDeleted() {
        Event deleted = new Event("Gone", "d", "Ottawa", daysFromNow(4), "DELETED", "5", "music");
        eventRepository.save(deleted);

        assertThat(eventService.filterByLocation("Ottawa", true).stream().map(Event::getId))
                .contains(deleted.getId());
    }

    @Test
    void filterByCategory_returnsOnlyMatchingNonDeletedByDefault() {
        Event match = new Event("C1", "d", "loc", daysFromNow(4), "AVAILABLE", "5", "sports");
        eventRepository.save(match);
        Event deleted = new Event("C2", "d", "loc", daysFromNow(4), "DELETED", "5", "sports");
        eventRepository.save(deleted);
        Event other = new Event("C3", "d", "loc", daysFromNow(4), "AVAILABLE", "5", "music");
        eventRepository.save(other);

        assertThat(eventService.filterByCategory("sports", false).stream().map(Event::getId))
                .contains(match.getId())
                .doesNotContain(deleted.getId(), other.getId());
    }

    @Test
    void filterByDate_returnsOnlyMatchingNonDeletedByDefault() {
        Date date = daysFromNow(9);
        Event match = new Event("D1", "d", "loc", date, "AVAILABLE", "5", "music");
        eventRepository.save(match);
        Event deleted = new Event("D2", "d", "loc", date, "DELETED", "5", "music");
        eventRepository.save(deleted);
        Event other = new Event("D3", "d", "loc", daysFromNow(10), "AVAILABLE", "5", "music");
        eventRepository.save(other);

        assertThat(eventService.filterByDate(date, false).stream().map(Event::getId))
                .contains(match.getId())
                .doesNotContain(deleted.getId(), other.getId());
    }

    @Test
    void viewAvailableEvents_returnsOnlyAvailableNonDeleted() {
        Event available = new Event("A", "d", "loc", daysFromNow(5), "AVAILABLE", "5", "music");
        eventRepository.save(available);
        Event deleted = new Event("Del", "d", "loc", daysFromNow(5), "DELETED", "5", "music");
        eventRepository.save(deleted);
        Event filled = new Event("Fill", "d", "loc", daysFromNow(5), "FILLED", "0", "music");
        eventRepository.save(filled);

        assertThat(eventService.viewAvailableEvents(false).stream().map(Event::getId))
                .contains(available.getId())
                .doesNotContain(deleted.getId(), filled.getId());
    }

    @Test
    void getPublicEventById_returnsEmptyWhenMissing() {
        assertThat(eventService.getPublicEventById(987654321L)).isEmpty();
    }
}
