package com.makesoft.project.controller;

import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.makesoft.project.model.Event;
import com.makesoft.project.model.Reservation;
import com.makesoft.project.repository.EventRepository;
import com.makesoft.project.repository.ReservationRepository;

@SpringBootTest(webEnvironment = org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Pattern JSON_ID = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

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

    @Test
    void registerLoginReserve_fullCustomerFlow() {
        String base = "http://localhost:" + port + "/api";
        Date future = new Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000);
        Event ev = new Event("Integration Reserve Event", "d", "Venue", future, "AVAILABLE", "3", "integration");
        eventRepository.save(ev);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String email = "flow" + System.nanoTime() + "@test.com";
        String registerBody = "{\"name\":\"Flow User\",\"email\":\"" + email + "\",\"phoneNumber\":\"\",\"password\":\"pass\",\"role\":\"customer\"}";
        ResponseEntity<String> reg = restTemplate.postForEntity(base + "/users/register", new HttpEntity<>(registerBody, headers), String.class);
        assertThat(reg.getStatusCode()).isIn(HttpStatus.CREATED, HttpStatus.valueOf(201));

        String loginBody = "{\"identifier\":\"" + email + "\",\"password\":\"pass\"}";
        ResponseEntity<String> login = restTemplate.postForEntity(base + "/users/login", new HttpEntity<>(loginBody, headers), String.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);

        Matcher idMatcher = JSON_ID.matcher(login.getBody());
        assertThat(idMatcher.find()).isTrue();
        long userId = Long.parseLong(idMatcher.group(1));

        String resBody = "{\"userId\":" + userId + ",\"eventId\":" + ev.getId() + ",\"quantity\":1}";
        ResponseEntity<String> reservation = restTemplate.postForEntity(base + "/reservations", new HttpEntity<>(resBody, headers), String.class);
        assertThat(reservation.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Event updated = eventRepository.findById(ev.getId()).orElseThrow();
        assertThat(updated.getTicketRemaining()).isEqualTo("2");
    }

    @Test
    void register_rejectsDuplicateEmail() {
        String base = "http://localhost:" + port + "/api";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String email = "dup-int" + System.nanoTime() + "@test.com";
        String body = "{\"name\":\"Dup Int\",\"email\":\"" + email + "\",\"phoneNumber\":\"\",\"password\":\"pass\",\"role\":\"customer\"}";

        ResponseEntity<String> first = restTemplate.postForEntity(base + "/users/register", new HttpEntity<>(body, headers), String.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        try {
            restTemplate.postForEntity(base + "/users/register", new HttpEntity<>(body, headers), String.class);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(ex.getResponseBodyAsString()).contains("user with that email already exists");
            return;
        }

        throw new AssertionError("Expected duplicate email registration to return 409 CONFLICT");
    }

    @Test
    void registerLoginReserveCancel_fullFlow() {
        String base = "http://localhost:" + port + "/api";
        Date future = new Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000);
        Event ev = new Event("Integration Cancel Event", "d", "Venue", future, "AVAILABLE", "4", "integration");
        eventRepository.save(ev);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String email = "cancel-flow" + System.nanoTime() + "@test.com";
        String registerBody = "{\"name\":\"Flow User\",\"email\":\"" + email + "\",\"phoneNumber\":\"\",\"password\":\"pass\",\"role\":\"customer\"}";
        ResponseEntity<String> reg = restTemplate.postForEntity(base + "/users/register", new HttpEntity<>(registerBody, headers), String.class);
        assertThat(reg.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String loginBody = "{\"identifier\":\"" + email + "\",\"password\":\"pass\"}";
        ResponseEntity<String> login = restTemplate.postForEntity(base + "/users/login", new HttpEntity<>(loginBody, headers), String.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);

        Matcher idMatcher = JSON_ID.matcher(login.getBody());
        assertThat(idMatcher.find()).isTrue();
        long userId = Long.parseLong(idMatcher.group(1));

        String resBody = "{\"userId\":" + userId + ",\"eventId\":" + ev.getId() + ",\"quantity\":1}";
        ResponseEntity<String> reservation = restTemplate.postForEntity(base + "/reservations", new HttpEntity<>(resBody, headers), String.class);
        assertThat(reservation.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Optional<Reservation> savedReservation = reservationRepository.findAll().stream()
                .filter(r -> r.getUser() != null && r.getEvent() != null)
                .filter(r -> r.getUser().getId().equals(userId) && r.getEvent().getId().equals(ev.getId()))
                .findFirst();
        assertThat(savedReservation).isPresent();

        HttpHeaders deleteHeaders = new HttpHeaders();
        ResponseEntity<Void> cancel = restTemplate.exchange(
                base + "/reservations/" + savedReservation.get().getReservation_id(),
                org.springframework.http.HttpMethod.DELETE,
                new HttpEntity<>(deleteHeaders),
                Void.class);
        assertThat(cancel.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(reservationRepository.findById(savedReservation.get().getReservation_id())).isEmpty();
    }
}
