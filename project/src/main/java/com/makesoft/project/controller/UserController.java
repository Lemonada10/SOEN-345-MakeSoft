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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import com.makesoft.project.model.User;
import com.makesoft.project.service.NotificationService;
import com.makesoft.project.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = {"http://localhost:3000", "http://127.0.0.1:3000", "https://*.vercel.app"})
public class UserController {

    private static final String INVALID_CREDENTIALS = "Invalid email or password";

    private final UserService userService;
    private final NotificationService notificationService;

    public UserController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    // DTO for register request
    public static class RegisterRequest {
        public String name;
        public String email;
        public String phoneNumber;
        public String password;
        public String role; // e.g. "customer" or "instructor"
    }

    // DTO for login request (identifier = email or phone)
    public static class LoginRequest {
        public String identifier;
        public String password;
    }

    // Response DTO without password
    public static class LoginResponse {
        public Long id;
        public String name;
        public String email;
        public String phone_number;
        public String role;

        public LoginResponse(User u) {
            this.id = u.getId();
            this.name = u.getName();
            this.email = u.getEmail();
            this.phone_number = u.getPhone_number();
            this.role = u.getRole();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req.email == null || req.email.isBlank()) {
            return ResponseEntity.badRequest().body("email required");
        }
        if (userService.existsByEmail(req.email)) {
            return ResponseEntity.status(409).body("user with that email already exists");
        }
        if (req.phoneNumber != null && !req.phoneNumber.isBlank() && userService.existsByPhoneNumber(req.phoneNumber)) {
            return ResponseEntity.status(409).body("user with that phone number already exists");
        }

        userService.registerUser(req.name, req.email, req.phoneNumber, req.password, req.role);

        Optional<User> created = userService.findByEmail(req.email);
        if (created.isPresent()) {
            User u = created.get();
            notificationService.sendSignUpConfirmation(u, "email");
            URI location = URI.create("/api/users/" + u.getId());
            return ResponseEntity.created(location).body(u);
        }

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (req.identifier == null || req.identifier.isBlank() || req.password == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_CREDENTIALS);
        }
        Optional<User> userOpt = userService.findByEmail(req.identifier.trim());
        if (!userOpt.isPresent()) {
            userOpt = userService.findByPhoneNumber(req.identifier.trim());
        }
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_CREDENTIALS);
        }
        User user = userOpt.get();
        String stored = user.getPassword();
        if (stored == null || !stored.equals(req.password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_CREDENTIALS);
        }
        return ResponseEntity.ok(new LoginResponse(user));
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
