package com.portfolio.demo.project.dto.member.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SigninRequest {
    @NotEmpty
    private String identifier;
    private String password;
}
