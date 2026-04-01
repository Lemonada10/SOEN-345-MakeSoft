package com.makesoft.project.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.makesoft.project.model.Event;
import com.makesoft.project.repository.EventRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public boolean eventExists(Long id) {
        return eventRepository.existsById(id);
    }

    public List<Event> viewEvents() {
        return viewEvents(false);
    }

    public List<Event> viewEvents(boolean includeDeleted) {
        List<Event> events = eventRepository.findAll();
        markPastEvents(events);
        return includeDeleted ? events : excludeDeleted(events);
    }

    public List<Event> viewAvailableEvents(boolean includeDeleted) {
        List<Event> events = eventRepository.findByStatus("AVAILABLE");
        markPastEvents(events);
        return includeDeleted ? events : excludeDeleted(events);
    }

    public List<Event> filterByDate(Date date, boolean includeDeleted) {
        List<Event> events = eventRepository.findByStartDateTime(date);
        markPastEvents(events);
        return includeDeleted ? events : excludeDeleted(events);
    }

    public List<Event> filterByLocation(String location, boolean includeDeleted) {
        List<Event> events = eventRepository.findByLocation(location);
        markPastEvents(events);
        return includeDeleted ? events : excludeDeleted(events);
    }

    public List<Event> filterByCategory(String category, boolean includeDeleted) {
        List<Event> events = eventRepository.findByCategory(category);
        markPastEvents(events);
        return includeDeleted ? events : excludeDeleted(events);
    }

    private static List<Event> excludeDeleted(List<Event> events) {
        return events.stream()
                .filter(e -> !"DELETED".equals(e.getStatus()))
                .collect(Collectors.toList());
    }

    public Optional<Event> getPublicEventById(Long id) {
        Optional<Event> opt = eventRepository.findById(id);
        if (!opt.isPresent()) {
            return Optional.empty();
        }
        Event e = opt.get();
        markPastEvents(singletonListMutable(e));
        if ("DELETED".equals(e.getStatus())) {
            return Optional.empty();
        }
        return Optional.of(e);
    }

    private static List<Event> singletonListMutable(Event e) {
        List<Event> list = new ArrayList<>(1);
        list.add(e);
        return list;
    }

    private void markPastEvents(List<Event> events) {
        Date now = new Date();
        for (Event e : events) {
            if ("DELETED".equals(e.getStatus())) {
                continue;
            }
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

    public Event modifyEvent(Long event_id, Event incoming) {
        Optional<Event> old_event = eventRepository.findById(event_id);
        if (!old_event.isPresent()) {
            return null;
        }

        Event event = old_event.get();

        if (incoming.getName() != null) {
            event.setName(incoming.getName());
        }
        if (incoming.getDescription() != null) {
            event.setDescription(incoming.getDescription());
        }
        if (incoming.getLocation() != null) {
            event.setLocation(incoming.getLocation());
        }
        if (incoming.getStartDateTime() != null) {
            event.setStartDateTime(incoming.getStartDateTime());
        }
        if (incoming.getTicketRemaining() != null) {
            event.setTicketRemaining(incoming.getTicketRemaining());
        }
        if (incoming.getCategory() != null) {
            event.setCategory(incoming.getCategory());
        }

        if (!"DELETED".equals(event.getStatus())) {
            refreshStatusAfterAdminEdit(event);
        }

        return eventRepository.save(event);
    }

    private void refreshStatusAfterAdminEdit(Event event) {
        Date now = new Date();
        if (event.getStartDateTime() != null && event.getStartDateTime().before(now)) {
            event.setStatus("PASSED");
            return;
        }
        if (isZeroTickets(event.getTicketRemaining())) {
            event.setStatus("FILLED");
            return;
        }
        event.setStatus("AVAILABLE");
    }

    private static boolean isZeroTickets(String ticketRemaining) {
        if (ticketRemaining == null || ticketRemaining.isBlank()) return false;
        try {
            return Integer.parseInt(ticketRemaining.trim()) == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Admin cancel: always set status DELETED (no row delete — avoids FK errors and keeps reservations valid).
     */
    public void cancelEvent(Long event_id) {
        Optional<Event> opt = eventRepository.findById(event_id);
        if (!opt.isPresent()) {
            return;
        }
        Event event = opt.get();
        if ("DELETED".equals(event.getStatus())) {
            return;
        }
        event.setStatus("DELETED");
        eventRepository.save(event);
    }
}
