package com.makesoft.project.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    private String name;
    private String description;
    private String location;
    private Date startDateTime;
    private String status;
    private String ticketRemaining;
    private String category;

    public Event() {

    }

    public Event(String name, String description, String location, Date startDateTime, String status, String ticketRemaining, String category) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.startDateTime = startDateTime;
        this.status = status;
        this.ticketRemaining = ticketRemaining;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public String getStatus() {
        return status;
    }

    public String getTicketRemaining() {
        return ticketRemaining;
    }

    public String getCategory() {
        return category;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTicketRemaining(String ticketRemaining) {
        this.ticketRemaining = ticketRemaining;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
