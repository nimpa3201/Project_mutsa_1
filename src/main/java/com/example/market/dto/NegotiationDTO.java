package com.example.market.dto;

import com.example.market.entity.NegotiationEntity;
import lombok.Data;

@Data
public class NegotiationDTO {
    private Long id;
    private Long itemId;
    private Long suggestedPrice;
    private String status;
    private String writer;
    private String password;

    public static NegotiationDTO fromEntity(NegotiationEntity entity) {
        NegotiationDTO dto = new NegotiationDTO();
        dto.setId(entity.getId());
        dto.setItemId(entity.getItemId());
        dto.setSuggestedPrice(entity.getSuggestedPrice());
        dto.setStatus(entity.getStatus());
        dto.setWriter(entity.getWriter());
        dto.setPassword(entity.getPassword());
        return dto;
    }
}
