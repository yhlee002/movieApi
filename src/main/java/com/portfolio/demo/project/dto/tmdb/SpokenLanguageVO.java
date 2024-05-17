package com.portfolio.demo.project.dto.tmdb;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SpokenLanguageVO {
    private String englishName; // english_name
    private String iso_639_1; // 'en', 'ko', ...
    private String name;
}
