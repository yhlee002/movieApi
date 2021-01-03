package com.portfolio.demo.project.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BoardListVO {
    private int currentPageNo; // 현재 페이지
    private final static int recordsPerPage = 15; // 페이지당 출력할 데이터 개수
    private final static int pageSize = 5; // 화면 하단에 표시할 페이지 개수

    public BoardListVO() {
        this.currentPageNo = 1;
    }

    public int getStartPage(){
        return (currentPageNo - 1) * recordsPerPage;
    }
}
