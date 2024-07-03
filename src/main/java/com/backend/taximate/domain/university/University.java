package com.backend.taximate.domain.university;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class University {
    @Id
    @GeneratedValue
    @Column(name = "university_id")
    private Long id;
    private String name;
    private String emailSuffix;

    public University() {}

    public University(String name, String emailSuffix) {
        this.name = name;
        this.emailSuffix = emailSuffix;
    }
}
