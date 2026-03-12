package com.makesoft.project.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.makesoft.project.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
// This interface will automatically inherit methods for CRUD operations on Event entities
// operations like save(), findAll(), findById(), deleteById()

    List<Event> findByName(String name);

    List<Event> findByLocation(String location);

    List<Event> findByStartDateTime(Date startDateTime);

    List<Event> findByCategory(String category);

    List<Event> findByStatus(String status);
}
