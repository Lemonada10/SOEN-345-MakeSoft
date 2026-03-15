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

    // GET /api/events?date=...&location=...&category=...&status=...
    @GetMapping
    public List<Event> listEvents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date date,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {

        if (date != null) {
            return eventService.filterByDate(date);
        }
        if (location != null && !location.isBlank()) {
            return eventService.filterByLocation(location);
        }
        if (category != null && !category.isBlank()) {
            return eventService.filterByCategory(category);
        }
        if (status != null && status.equalsIgnoreCase("AVAILABLE")) {
            return eventService.viewAvailableEvents();
        }
        return eventService.viewEvents();
    }

    @GetMapping("/available")
    public List<Event> listAvailable() {
        return eventService.viewAvailableEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        return eventService.viewEvents().stream()
                .filter(e -> e.getId() != null && e.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        // delegate creation to service; service saves the entity
        eventService.addEvent(event);
        // return 201 Created with Location header (event id should be populated by JPA save)
        URI location = URI.create("/api/events/" + (event.getId() != null ? event.getId() : ""));
        return ResponseEntity.created(location).body(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        boolean exists = eventService.viewEvents().stream()
                .anyMatch(e -> e.getId() != null && e.getId().equals(id));
        if (!exists) {
            return ResponseEntity.notFound().build();
        }

        eventService.modifyEvent(id, event);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        boolean exists = eventService.viewEvents().stream()
                .anyMatch(e -> e.getId() != null && e.getId().equals(id));
        if (!exists) {
            return ResponseEntity.notFound().build();
        }

        eventService.cancelEvent(id);
        return ResponseEntity.noContent().build();
    }
}
