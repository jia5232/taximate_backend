package com.backend.kiri.repository.university;

import com.backend.kiri.domain.university.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UniversityRepository extends JpaRepository<University, Long> {
    Boolean existsByEmailSuffix(String emailSuffix);
    Optional<University> findByEmailSuffix(String emailSuffix);
    Optional<University> findByName(String name);
    List<University> findByNameContainingIgnoreCase(String name);
}
