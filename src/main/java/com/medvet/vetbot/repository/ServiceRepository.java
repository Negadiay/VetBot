package com.medvet.vetbot.repository;

import com.medvet.vetbot.domain.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}
