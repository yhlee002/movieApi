package com.portfolio.demo.project.service;

import com.portfolio.demo.project.dto.recommended.RecommendedBoardParam;
import com.portfolio.demo.project.entity.recommended.RecommendedBoard;
import com.portfolio.demo.project.repository.RecommendedBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendedBoardService {

    private final RecommendedBoardRepository recommendedBoardRepository;

    public RecommendedBoardParam findById(Long id) {
        RecommendedBoard recommendedBoard = recommendedBoardRepository.findById(id).orElse(null);

        if (recommendedBoard != null) {
            return RecommendedBoardParam.create(recommendedBoard);
        } else {
            log.info("해당 아이디의 추천 정보가 존재하지 않습니다.");
        }

        return null;
    }

    public Boolean isRecommendedByLoginUser(Long boardId, Long memNo) {
        return recommendedBoardRepository.existsByBoardIdAndMemNo(boardId, memNo);
    }

    public Long saveBoardRecommended(Long boardId, Long memNo) {
        RecommendedBoard recommended = RecommendedBoard.builder()
                .boardId(boardId)
                .memNo(memNo)
                .build();
        recommendedBoardRepository.save(recommended);

        return recommended.getId();
    }

    public void deleteBoardRecommended(Long boardId, Long memNo) {
        RecommendedBoard recommended = recommendedBoardRepository.findByBoardIdAndMemNo(boardId, memNo);
        recommendedBoardRepository.delete(recommended);
    }
}
