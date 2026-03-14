package com.makesoft.project.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.makesoft.project.model.User;
import com.makesoft.project.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // DTO for register request
    public static class RegisterRequest {
        public String name;
        public String email;
        public String phoneNumber;
        public String password;
        public String role; // e.g. "customer" or "instructor"
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req.email == null || req.email.isBlank()) {
            return ResponseEntity.badRequest().body("email required");
        }
        if (userService.existsByEmail(req.email)) {
            return ResponseEntity.status(409).body("user with that email already exists");
        }

        userService.registerUser(req.name, req.email, req.phoneNumber, req.password, req.role);

        Optional<User> created = userService.findByEmail(req.email);
        if (created.isPresent()) {
            User u = created.get();
            URI location = URI.create("/api/users/" + u.getId());
            return ResponseEntity.created(location).body(u);
        }

        return ResponseEntity.status(201).build();
    }

    @GetMapping
    public List<User> listUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> u = userService.findById(id);
        return u.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // find by email via query param: /api/users/by-email?email=...
    @GetMapping("/by-email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        Optional<User> u = userService.findByEmail(email);
        return u.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> deleteByEmail(@PathVariable String email) {
        if (!userService.existsByEmail(email)) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/phone/{phoneNumber}")
    public ResponseEntity<Void> deleteByPhone(@PathVariable String phoneNumber) {
        if (!userService.existsByPhoneNumber(phoneNumber)) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteByPhoneNumber(phoneNumber);
        return ResponseEntity.noContent().build();
    }
}
