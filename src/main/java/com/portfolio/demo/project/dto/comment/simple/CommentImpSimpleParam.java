package com.portfolio.demo.project.dto.comment.simple;

import com.portfolio.demo.project.entity.DeleteFlag;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CommentImpSimpleParam {

    private Long id;
    private Long boardId;
    private Long writerId;
    private String writerName;
    private String writerProfileImage;
    private String content;
    private LocalDateTime regDate;
    private DeleteFlag delYn;

    public CommentImpSimpleParam(Long id, Long boardId, Long writerId, String writerName, String writerProfileImage, String content, LocalDateTime regDate, DeleteFlag delYn) {
        this.id = id;
        this.boardId = boardId;
        this.writerId = writerId;
        this.writerName = writerName;
        this.writerProfileImage = writerProfileImage;
        this.content = content;
        this.regDate = regDate;
        this.delYn = delYn;
    }

}
