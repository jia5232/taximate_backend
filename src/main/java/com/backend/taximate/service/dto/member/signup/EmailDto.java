package com.backend.taximate.service.dto.member.signup;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailDto {
    @NotBlank
    private String email;
}
