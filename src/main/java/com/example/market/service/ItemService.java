package com.example.market.service;
import com.example.market.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.market.dto.ItemDTO;
import com.example.market.entity.ItemEntity;
import com.example.market.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository repository;

    public ItemDTO create(ItemDTO dto) {
        ItemEntity newItem = new ItemEntity();
        newItem.setTitle(dto.getTitle());
        newItem.setDescription(dto.getDescription());
        newItem.setMin_price_wanted(dto.getMin_price_wanted());
        newItem.setStatus("판매중");
        newItem.setWriter(dto.getWriter());
        newItem.setPassword(dto.getPassword());
        return ItemDTO.fromEntity(repository.save(newItem));
    }



    public ItemDTO readItem(Long id) {
        Optional<ItemEntity> optionalEntity
                = repository.findById(id);
        if (optionalEntity.isPresent())
            return ItemDTO.fromEntity(optionalEntity.get());
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }


    public ItemDTO update(Long id,ItemDTO dto) {
        Optional<ItemEntity> optionalItem = repository.findById(id);
        if (optionalItem.isPresent()) {
            ItemEntity upItem = optionalItem.get();
            if (upItem.getPassword().equals(dto.getPassword())) {
                upItem.setTitle(dto.getTitle());
                upItem.setDescription(dto.getDescription());
                upItem.setMin_price_wanted(dto.getMin_price_wanted());
                upItem.setWriter(dto.getWriter());
                upItem.setPassword(dto.getPassword());
                repository.save(upItem);
                return ItemDTO.fromEntity(upItem);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }



    public Page<ItemDTO> readItemAll(Long page, Long limit) {
        Pageable pageable = PageRequest.of(Math.toIntExact(page), Math.toIntExact(limit));
        Page<ItemEntity> ItemEntityPage = repository.findAll(pageable);
        Page<ItemDTO> ItemDtoPage = ItemEntityPage.map(ItemDTO:: fromEntity);
        return ItemDtoPage;
    }

    public ItemDTO updateImage(Long id,String password, MultipartFile Image){
        Optional<ItemEntity> optionalItem
                = repository.findById(id);
        if (optionalItem.isEmpty())
           throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        ItemEntity itemEntity = optionalItem.get();
        if(!itemEntity.getPassword().equals(password))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);


        String profileDir = String.format("media/%d/", id);
        try {
            Files.createDirectories(Path.of(profileDir));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }


        String originalFilename = Image.getOriginalFilename();
        String[] fileNameSplit = originalFilename.split("\\.");
        String extension = fileNameSplit[fileNameSplit.length - 1];
        String profileFilename = "Item." + extension;

        String profilePath = profileDir + profileFilename;

        try {
            Image.transferTo(Path.of(profilePath));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        itemEntity.setImage_url(String.format("/static/%d/%s", id, profileFilename));
        return ItemDTO.fromEntity(repository.save(itemEntity));
    }



    public ResponseDTO delete(long id, ItemDTO dto) {
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setMessage("물품을 삭제했습니다");
        Optional<ItemEntity> optionalItem = repository.findById(id);

        if (optionalItem.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 아이템을 찾을 수 없습니다.");
        }
        ItemEntity item = optionalItem.get();

        if (dto.getWriter().equals(item.getWriter())&&dto.getPassword().equals(item.getPassword())) {
            repository.deleteById(id);
            return responseDto;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호입니다.");
    }

}


