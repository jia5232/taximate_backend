package com.backend.kiri.repository.university;

import com.backend.kiri.domain.university.University;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityRepository extends JpaRepository<University, Long> {
    Boolean existsByEmailSuffix(String emailSuffix);
    String findByEmailSuffix(String emailSuffix);
}
