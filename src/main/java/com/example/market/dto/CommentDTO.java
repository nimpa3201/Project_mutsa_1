package com.example.market.dto;

import com.example.market.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data

public class CommentDTO {
    private Long id; // 댓글 id
    private String content;
    private String reply;

    public static CommentDTO fromEntity(CommentEntity entity) {
        return CommentDTO.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .reply(entity.getReply())
                .build();
    }
}










