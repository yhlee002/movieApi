package com.portfolio.demo.project.dto.member.request;

import com.portfolio.demo.project.entity.member.MemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MultiUpdateRoleRequest {
    @NotNull
    private List<Long> memNoList;
    @NotNull
    private MemberRole role;
}
