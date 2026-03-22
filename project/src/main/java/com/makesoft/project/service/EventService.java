package com.makesoft.project.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.makesoft.project.model.Event;
import com.makesoft.project.repository.EventRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> viewEvents() {
        List<Event> events = eventRepository.findAll();
        markPastEvents(events);
        return events;
    }

    public List<Event> viewAvailableEvents() {
        List<Event> events = eventRepository.findByStatus("AVAILABLE");
        markPastEvents(events);
        return events;
    }

    public List<Event> filterByDate(Date date) {
        List<Event> events = eventRepository.findByStartDateTime(date);
        markPastEvents(events);
        return events;
    }

    public List<Event> filterByLocation(String location) {
        List<Event> events = eventRepository.findByLocation(location);
        markPastEvents(events);
        return events;
    }

    public List<Event> filterByCategory(String category) {
        List<Event> events = eventRepository.findByCategory(category);
        markPastEvents(events);
        return events;
    }

    private void markPastEvents(List<Event> events) {
        Date now = new Date();
        for (Event e : events) {
            if (e.getStartDateTime() != null && e.getStartDateTime().before(now)
                    && !"PASSED".equals(e.getStatus())) {
                e.setStatus("PASSED");
                eventRepository.save(e);
            }
        }
    }

    public void addEvent(Event event) {
        if (isZeroTickets(event.getTicketRemaining())) event.setStatus("FILLED");
        eventRepository.save(event);
    }

    public void modifyEvent(Long event_id, Event new_event) {
        Optional<Event> old_event = eventRepository.findById(event_id);
        if (!old_event.isPresent()) {
            return;
        }

        Event event = old_event.get();

        event.setName(new_event.getName());
        event.setDescription(new_event.getDescription());
        event.setLocation(new_event.getLocation());
        event.setStartDateTime(new_event.getStartDateTime());
        event.setTicketRemaining(new_event.getTicketRemaining());
        event.setCategory(new_event.getCategory());
        if (isZeroTickets(event.getTicketRemaining())) event.setStatus("FILLED");
        else event.setStatus(new_event.getStatus());
        eventRepository.save(event);

    }

    private static boolean isZeroTickets(String ticketRemaining) {
        if (ticketRemaining == null || ticketRemaining.isBlank()) return false;
        try {
            return Integer.parseInt(ticketRemaining.trim()) == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void cancelEvent(Long event_id) {
        Optional<Event> event = eventRepository.findById(event_id);
        if (!event.isPresent()) {
            return;
        }

        eventRepository.delete(event.get());
    }
}
