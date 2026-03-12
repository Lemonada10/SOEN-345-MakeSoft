package com.makesoft.project.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservation_id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private Integer quantity;
    private String status;
    private Date reservationDateTime;

    public Reservation() {
    }

    public Reservation(User user, Event event, Integer quantity, String status, Date reservationDateTime) {
        this.user = user;
        this.event = event;
        this.quantity = quantity;
        this.status = status;
        this.reservationDateTime = reservationDateTime;
    }

    public Long getReservation_id() {
        return reservation_id;
    }

    public User getUser() {
        return user;
    }

    public Event getEvent() {
        return event;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public Date getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationId(Long reservation_id) {
        this.reservation_id = reservation_id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReservationDateTime(Date reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

}
