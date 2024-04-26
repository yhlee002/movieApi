package com.portfolio.demo.project.vo.tmdb;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductionContryVO {
    private String iso_3166_1; // Ex.'US', ...
    private String name;
}
