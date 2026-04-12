package com.makesoft.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_rejectsWhenUserNotFound() throws Exception {
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identifier\":\"nobody@example.com\",\"password\":\"any\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }

    @Test
    void login_rejectsBlankIdentifier() throws Exception {
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identifier\":\"\",\"password\":\"x\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }

    @Test
    void registerThenLogin_returnsUserWithoutPassword() throws Exception {
        String email = "logintest@example.com";
        String body = "{\"name\":\"Login Test\",\"email\":\"" + email + "\",\"phoneNumber\":\"\",\"password\":\"mypass\",\"role\":\"customer\"}";
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identifier\":\"" + email + "\",\"password\":\"mypass\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"email\":\"logintest@example.com\",\"role\":\"customer\"}"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("mypass"))));
    }

    @Test
    void login_rejectsWrongPassword() throws Exception {
        String email = "wrongpass@example.com";
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"X\",\"email\":\"" + email + "\",\"phoneNumber\":\"\",\"password\":\"correct\",\"role\":\"customer\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identifier\":\"" + email + "\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }

    @Test
    void register_rejectsWhenEmailAlreadyExists() throws Exception {
        String email = "dup@example.com";
        String body = "{\"name\":\"Dup\",\"email\":\"" + email + "\",\"phoneNumber\":\"\",\"password\":\"mypass\",\"role\":\"customer\"}";
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict())
                .andExpect(content().string("user with that email already exists"));
    }

    @Test
    void register_rejectsWhenPhoneAlreadyExists() throws Exception {
        String phone = "514" + System.nanoTime();
        String email1 = "ph1" + System.nanoTime() + "@example.com";
        String email2 = "ph2" + System.nanoTime() + "@example.com";

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"P1\",\"email\":\"" + email1 + "\",\"phoneNumber\":\"" + phone + "\",\"password\":\"mypass\",\"role\":\"customer\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"P2\",\"email\":\"" + email2 + "\",\"phoneNumber\":\"" + phone + "\",\"password\":\"mypass\",\"role\":\"customer\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("user with that phone number already exists"));
    }

    @Test
    void register_rejectsWhenEmailMissing() throws Exception {
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"NoEmail\",\"password\":\"mypass\",\"role\":\"customer\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("email required"));
    }

    @Test
    void login_acceptsPhoneIdentifier() throws Exception {
        String phone = "438" + System.nanoTime();
        String email = "phone-login" + System.nanoTime() + "@example.com";
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Phone Login\",\"email\":\"" + email + "\",\"phoneNumber\":\"" + phone + "\",\"password\":\"mypass\",\"role\":\"customer\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identifier\":\"" + phone + "\",\"password\":\"mypass\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"email\":\"" + email + "\",\"role\":\"customer\"}"));
    }

    @Test
    void login_rejectsWhenPasswordMissing() throws Exception {
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identifier\":\"someone@example.com\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }
}
