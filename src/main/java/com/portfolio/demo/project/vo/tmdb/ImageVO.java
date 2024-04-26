package com.portfolio.demo.project.vo.tmdb;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ImageVO {
    private double aspectRatio; // aspect_ratio
    private double height;
    private String iso_639_1;
    private String filePath; // file_path
    private double voteAverage; // vote_average
    private int voteCount; // vote_count
    private double width;
}
