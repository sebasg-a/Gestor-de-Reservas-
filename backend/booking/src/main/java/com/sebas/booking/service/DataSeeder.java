package com.sebas.booking.service;

import com.sebas.booking.domain.Resource;
import com.sebas.booking.domain.User;
import com.sebas.booking.repository.ResourceRepository;
import com.sebas.booking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    public DataSeeder(UserRepository userRepository, ResourceRepository resourceRepository) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .name("Sebas")
                    .email("sebas@example.com")
                    .build());
        }

        if (resourceRepository.count() == 0) {
            resourceRepository.save(Resource.builder()
                    .name("Sala 1")
                    .type("ROOM")
                    .location("Piso 2")
                    .active(true)
                    .build());
        }
    }
}
