package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.movie.ThumbImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieImgRepository extends JpaRepository<ThumbImg, Long> {

    ThumbImg findByMovieCd(String movieCd);
}
