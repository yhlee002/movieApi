package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.service.CommentMovService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class CommentController {

    @Autowired
    CommentMovService commentMovService;

    @ResponseBody
    @RequestMapping("/writeComment")
    public Long writeCommentProc(String commentContent, Long memNo, Long movieNo, int rating) {
        Long id = commentMovService.saveComment(memNo, commentContent, movieNo, rating);
        log.info("저장된 뒤 반환된 comment_id : "+id);
        return id; // 저장된 comment의 id
    }

    @ResponseBody
    @RequestMapping("/getCommentListOrderByRegDt")
    public Map<String ,Object> getCommentList(@RequestParam(name = "p") int pageNum, Long movieCd) {
        Map<String, Object> map = commentMovService.getCommentListOrderByRegDate(pageNum, movieCd);
        log.info("가져온 데이터 map : "+map.toString());
        return map;
    }
}
