package com.sebas.booking.api;

import com.sebas.booking.domain.Resource;
import com.sebas.booking.domain.User;
import com.sebas.booking.repository.ResourceRepository;
import com.sebas.booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ResourceRepository resourceRepository;

    private Long userId;
    private Long resourceId;

    private RestClient client;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        resourceRepository.deleteAll();

        User u = userRepository.save(User.builder().name("Test").email("test@example.com").build());
        Resource r = resourceRepository.save(Resource.builder().name("Sala").type("ROOM").location("X").active(true).build());

        userId = u.getId();
        resourceId = r.getId();

        client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldReturn201Then409WhenOverlap() {
        Map<String, Object> first = Map.of(
                "userId", userId,
                "resourceId", resourceId,
                "startTime", Instant.parse("2026-01-05T13:00:00Z").toString(),
                "endTime", Instant.parse("2026-01-05T14:00:00Z").toString(),
                "notes", "one"
        );

        Map<String, Object> overlap = Map.of(
                "userId", userId,
                "resourceId", resourceId,
                "startTime", Instant.parse("2026-01-05T13:30:00Z").toString(),
                "endTime", Instant.parse("2026-01-05T14:30:00Z").toString(),
                "notes", "two"
        );

        // 201
        String r1 = client.post()
                .uri("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(first)
                .retrieve()
                .body(String.class);

        assertNotNull(r1);

        // 409
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                client.post()
                        .uri("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(overlap)
                        .retrieve()
                        .body(String.class)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(ex.getResponseBodyAsString().contains("CONFLICT"));
    }
}
