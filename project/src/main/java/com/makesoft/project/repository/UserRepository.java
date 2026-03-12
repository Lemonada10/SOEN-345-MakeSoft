package com.makesoft.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.makesoft.project.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
// This interface will automatically inherit methods for CRUD operations on User entities
// operations like save(), findAll(), findById(), deleteById()

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    void deleteByPhoneNumber(String phoneNumber);

    void deleteByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

}
