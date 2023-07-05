package com.example.market.dto;

import com.example.market.entity.ItemEntity;
import lombok.Data;

@Data
public class ItemDTO {
    private Integer id;
    private String title;
    private String description;
    private String image_url;
    private Integer min_price_wanted;
    private String status;
    private String writer;
    private String  password;



 public static ItemDTO fromEntity (ItemEntity entity) {
        ItemDTO dto = new ItemDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setMin_price_wanted(entity.getMin_price_wanted());
        dto.setStatus(entity.getStatus());
        dto.setWriter(entity.getWriter());
        dto.setPassword(entity.getPassword());
        return dto;
    }

}
