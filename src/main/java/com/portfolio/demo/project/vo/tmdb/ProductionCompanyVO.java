package com.portfolio.demo.project.vo.tmdb;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductionCompanyVO {
    private int id;
    private String logoPath; // logo_path
    private String name;
    private String originCountry; // origin_country
}
