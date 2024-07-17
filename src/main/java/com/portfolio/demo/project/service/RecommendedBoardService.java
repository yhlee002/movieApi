package com.portfolio.demo.project.service;

import com.portfolio.demo.project.repository.RecommendedBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendedBoardService {

    private final RecommendedBoardRepository recommendedBoardRepository;

    public Boolean isRecommendedByLoginUser(Long boardId, Long memNo) {
        return recommendedBoardRepository.existsByBoardIdAndMemNo(boardId, memNo);
    }
}
