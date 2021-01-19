package com.portfolio.demo.project.vo.kmdb.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class RatingVO { // 전부 '||'를 기준으로 잘라야함
    List<RatingDetailVO> ratings;
}
