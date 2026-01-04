package com.sebas.booking.repository;

import com.sebas.booking.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> { }
