package com.makesoft.project.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.makesoft.project.model.User;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void registerAndFindByEmail() {
        userService.registerUser("Test User", "test@example.com", "5551234", "secret", "customer");
        Optional<User> found = userService.findByEmail("test@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test User");
        assertThat(found.get().getPassword()).isEqualTo("secret");
        assertThat(found.get().getRole()).isEqualTo("customer");
    }

    @Test
    void findByEmail_emptyWhenNotExists() {
        Optional<User> found = userService.findByEmail("nonexistent@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_trueAfterRegister() {
        userService.registerUser("A", "exists@test.com", null, "p", "customer");
        assertThat(userService.existsByEmail("exists@test.com")).isTrue();
        assertThat(userService.existsByEmail("other@test.com")).isFalse();
    }
}
