package com.example.market.dto;
import com.example.market.entity.CommentEntity;
import com.example.market.entity.ItemEntity;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id; // 댓글 id

    private Long itemId; // 등록된 물품 id

    private String writer;

    private String password;

    private String content;

    private String reply;

    public static CommentDTO fromEntity(CommentEntity entity){
        CommentDTO dto = new CommentDTO();
        dto.setId(entity.getId());
        dto.setItemId(entity.getItemId());
        dto.setWriter(entity.getWriter());
        dto.setPassword(entity.getPassword());
        dto.setContent(entity.getContent());
        dto.setReply(entity.getReply());
        return dto;
    }
}