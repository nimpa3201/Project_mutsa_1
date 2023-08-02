package com.example.market.dto;

import com.example.market.entity.NegotiationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class NegotiationDTO {
    private Long id;
    private Long suggestedPrice;
    private String status;


    public static NegotiationDTO fromEntity(NegotiationEntity entity) {
        return NegotiationDTO.builder()
                .id(entity.getId())
                .suggestedPrice(entity.getSuggestedPrice())
                .status(entity.getStatus())
                .build();
    }
}