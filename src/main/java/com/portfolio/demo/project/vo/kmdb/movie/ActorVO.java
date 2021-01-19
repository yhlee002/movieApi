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
public class ActorVO {
    private List<ActorDetailVO> actor;
}
