package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.service.BoardImpService;
import com.portfolio.demo.project.service.BoardNoticeService;
import com.portfolio.demo.project.vo.ImpressionPagenationVO;
import com.portfolio.demo.project.vo.NoticePagenationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Slf4j
@Controller
public class BoardController {

    @Autowired
    BoardNoticeService boardNoticeService;

    @Autowired
    BoardImpService boardImpService;

    @RequestMapping("/notice")
    public String noticeBoard(Model model, @RequestParam(name = "p", required = false, defaultValue = "1") int pageNum) {
        NoticePagenationVO pagenationVO = boardNoticeService.getNoticeListView(pageNum);
        model.addAttribute("pagenation", pagenationVO);

        return "board_notice/list";
    }

    @RequestMapping("/notice/write")
    public String noticeBoardWriteForm() {

        return "board_notice/writeForm";
    }

    @RequestMapping("/notice/{boardNo}")
    public String noticeDetail(@PathVariable Long boardNo, Model model) {
        Map<String, BoardNotice> boards = boardNoticeService.selectBoardByBoardId(boardNo);
        model.addAttribute("board", boards.get("board"));
        model.addAttribute("prevBoard", boards.get("prevBoard"));
        model.addAttribute("nextBoard", boards.get("nextBoard"));

        return "board_notice/detail";
    }

    @RequestMapping("/imp")
    public String impBoard(Model model, @RequestParam(name = "p", required = false, defaultValue = "1") int pageNum) {
        ImpressionPagenationVO pagenationVO = boardImpService.getImpListView(pageNum);
        model.addAttribute("pagenation", pagenationVO);

        return "board_impression/list";
    }

    @RequestMapping("/imp/write")
    public String impBoardWriteForm() {

        return "board_impression/writeForm";
    }

    @RequestMapping("/imp/{boardNo}")
    public String impDetail(@PathVariable Long boardNo, Model model) {
        Map<String, BoardImp> boards = boardImpService.selectBoardByBoardId(boardNo);
        model.addAttribute("board", boards.get("board"));
        model.addAttribute("prevBoard", boards.get("prevBoard"));
        model.addAttribute("nextBoard", boards.get("nextBoard"));

        return "board_impression/detail";
    }

}
