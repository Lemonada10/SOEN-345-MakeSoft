package com.makesoft.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.makesoft.project.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
// This interface will automatically inherit methods for CRUD operations on Event entities
// operations like save(), findAll(), findById(), deleteById()
}