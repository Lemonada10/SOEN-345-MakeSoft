package com.makesoft.project.service;
import org.springframework.stereotype.Service;

import com.makesoft.project.repository.EventRepository;

@Service
public class EventService {
    
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

}
