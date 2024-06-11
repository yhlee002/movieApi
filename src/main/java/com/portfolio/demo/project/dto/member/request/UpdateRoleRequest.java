package com.portfolio.demo.project.dto.member.request;

import com.portfolio.demo.project.entity.member.MemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @NotNull
    private Long memNo;
    @NotNull
    private MemberRole role;
}
