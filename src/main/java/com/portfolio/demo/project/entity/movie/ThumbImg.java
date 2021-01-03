package com.portfolio.demo.project.entity.movie;

import lombok.*;

import javax.persistence.*;

@Table(name = "movieimg")
@Setter
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ThumbImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "movie_cd")
    private String movieCd;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder
    public ThumbImg(Long no, String movieCd, String imageUrl) {
        this.no = no;
        this.movieCd = movieCd;
        this.imageUrl = imageUrl;
    }
}
