package com.portfolio.demo.project.dto.tmdb;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GenreVO {
    private String id;
    private String name;
}
