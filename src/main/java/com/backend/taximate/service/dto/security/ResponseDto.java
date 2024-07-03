package com.backend.taximate.service.dto.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ResponseDto {
    private String status;
    private String message;
}
