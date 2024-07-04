package com.portfolio.demo.project.dto.comment.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MultiDeleteRequest {
    @NotNull
    private List<Long> ids;
}
