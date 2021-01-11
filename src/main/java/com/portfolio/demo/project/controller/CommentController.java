package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.service.CommentMovService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommentController {

    @Autowired
    CommentMovService commentMovService;

    @ResponseBody
    @RequestMapping("/writeComment")
    public Long writeCommentProc(String commentContent, Long memNo, Long movieNo) {

        return commentMovService.saveComment(memNo, commentContent, movieNo); // 저장된 comment의 id
    }
}
