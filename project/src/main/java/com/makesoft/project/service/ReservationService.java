package com.makesoft.project.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.makesoft.project.model.Event;
import com.makesoft.project.model.Reservation;
import com.makesoft.project.model.User;
import com.makesoft.project.repository.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void addReservation(User user, Event event, Integer quantity, String status, Date reservationDateTime) {
        Reservation reservation = new Reservation(user, event, quantity, status, reservationDateTime);
        reservationRepository.save(reservation);
    }

    public void cancelReservation(Long reservation_id) {
        reservationRepository.deleteById(reservation_id);
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }
}
