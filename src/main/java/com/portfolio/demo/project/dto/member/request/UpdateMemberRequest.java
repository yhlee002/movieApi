package com.portfolio.demo.project.dto.member.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMemberRequest {
    @NotNull
    private Long memNo;
    private String password;
    private String name;
    private String phone;
    private String provider;
    private String profileImage;
}
