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
        return eventRepository.findAll();

    }

    public List<Event> viewAvailableEvents() {
        return eventRepository.findByStatus("AVAILABLE");
    }

    public List<Event> filterByDate(Date date) {
        return eventRepository.findByStartDateTime(date);
    }

    public List<Event> filterByLocation(String location) {
        return eventRepository.findByLocation(location);
    }

    public List<Event> filterByCategory(String category) {
        return eventRepository.findByCategory(category);
    }

    public void addEvent(Event event) {
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
        event.setStatus(new_event.getStatus());
        event.setTicketRemaining(new_event.getTicketRemaining());
        event.setCategory(new_event.getCategory());
        eventRepository.save(event);

    }

    public void cancelEvent(Long event_id) {
        Optional<Event> event = eventRepository.findById(event_id);
        if (!event.isPresent()) {
            return;
        }

        eventRepository.delete(event.get());
    }
}
