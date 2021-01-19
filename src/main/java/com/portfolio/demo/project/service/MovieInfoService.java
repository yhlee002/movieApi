//package com.portfolio.demo.project.service;
//
//import com.portfolio.demo.project.util.KmdbUtil;
//import com.portfolio.demo.project.vo.movieInfo.KmdbMovieDetailVO;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//public class MovieInfoService {
//
//    @Autowired
//    private KmdbUtil kmdbUtil;
//
//
//    public KmdbMovieDetailVO getOneMovieDetail(String title, String director) { // 영화 상세 정보
//        log.info("들어온 title : " + title + ", director : " + director);
//        return kmdbUtil.getMovieDetail(title, director);
//    }
//
////    public List<MovieD etailVO> getMovieDetailList() {// 박스오피스 영화 api에서 영화 정보를 가져와서 이 api를 통해 해당 영화의 정보 노출
////
////    }
//
//}
