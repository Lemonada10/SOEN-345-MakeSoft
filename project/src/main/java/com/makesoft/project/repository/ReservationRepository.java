package com.makesoft.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.makesoft.project.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
// This interface will automatically inherit methods for CRUD operations on Reservation entities
// operations like save(), findAll(), findById(), deleteById()
}