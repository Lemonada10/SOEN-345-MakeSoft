package com.makesoft.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.makesoft.project.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
// This interface will automatically inherit methods for CRUD operations on User entities
// operations like save(), findAll(), findById(), deleteById()

    @Query("SELECT u FROM User u WHERE u.phone_number = :phoneNumber")
    Optional<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("DELETE FROM User u WHERE u.phone_number = :phoneNumber")
    void deleteByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    void deleteByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.phone_number = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

}
