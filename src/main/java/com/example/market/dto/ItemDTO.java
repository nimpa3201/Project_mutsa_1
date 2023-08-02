package com.example.market.dto;

import com.example.market.entity.ItemEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ItemDTO {
    private Integer id;
    private String title;
    private String description;
    private String image_url;
    private Integer min_price_wanted;
    private String status;


    public static ItemDTO fromEntity(ItemEntity entity) {
        return ItemDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .image_url(entity.getImage_url())
                .min_price_wanted(entity.getMin_price_wanted())
                .status(entity.getStatus())
                .build();
    }
}
