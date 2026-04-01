package com.makesoft.project.controller;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.makesoft.project.model.Event;
import com.makesoft.project.service.EventService;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(originPatterns = {"http://localhost:3000", "http://127.0.0.1:3000", "https://*.vercel.app"})
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> listEvents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date date,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "false") boolean includeDeleted) {

        if (date != null) {
            return eventService.filterByDate(date, includeDeleted);
        }
        if (location != null && !location.isBlank()) {
            return eventService.filterByLocation(location, includeDeleted);
        }
        if (category != null && !category.isBlank()) {
            return eventService.filterByCategory(category, includeDeleted);
        }
        if (status != null && status.equalsIgnoreCase("AVAILABLE")) {
            return eventService.viewAvailableEvents(includeDeleted);
        }
        return eventService.viewEvents(includeDeleted);
    }

    @GetMapping("/available")
    public List<Event> listAvailable(
            @RequestParam(required = false, defaultValue = "false") boolean includeDeleted) {
        return eventService.viewAvailableEvents(includeDeleted);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        return eventService.getPublicEventById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        eventService.addEvent(event);
        URI location = URI.create("/api/events/" + (event.getId() != null ? event.getId() : ""));
        return ResponseEntity.created(location).body(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        if (!eventService.eventExists(id)) {
            return ResponseEntity.notFound().build();
        }

        Event updated = eventService.modifyEvent(id, event);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        if (!eventService.eventExists(id)) {
            return ResponseEntity.notFound().build();
        }

        eventService.cancelEvent(id);
        return ResponseEntity.noContent().build();
    }
}
