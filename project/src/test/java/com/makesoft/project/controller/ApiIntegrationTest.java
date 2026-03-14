package com.makesoft.project.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void login_andListEvents() {
        String base = "http://localhost:" + port + "/api";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String registerBody = "{\"name\":\"Integration User\",\"email\":\"int@test.com\",\"phoneNumber\":\"\",\"password\":\"pass\",\"role\":\"customer\"}";
        ResponseEntity<String> reg = restTemplate.postForEntity(base + "/users/register", new HttpEntity<>(registerBody, headers), String.class);
        assertThat(reg.getStatusCode()).isIn(HttpStatus.CREATED, HttpStatus.valueOf(201));

        String loginBody = "{\"identifier\":\"int@test.com\",\"password\":\"pass\"}";
        ResponseEntity<String> login = restTemplate.postForEntity(base + "/users/login", new HttpEntity<>(loginBody, headers), String.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(login.getBody()).contains("int@test.com").contains("customer");

        ResponseEntity<String> events = restTemplate.getForEntity(base + "/events", String.class);
        assertThat(events.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
