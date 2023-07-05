package com.example.market.controller;

import com.example.market.dto.ItemDTO;
import com.example.market.dto.ResponseDTO;
import com.example.market.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;



@RestController  // @ResponseBody 생략 가능
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    // 1. 누구든지 중고 거래를 목적으로 물품에 대한 정보를 등록할 수 있다.
    //    1. 이때 반드시 포함되어야 하는 내용은 제목, 설명, 최소 가격, 작성자
    //    2. 또한 사용자가 물품을 등록할 때, 비밀번호 항목을 추가해서 등록한다.
    //    3. 최초로 물품이 등록될 때, 중고 물품의 상태는 **판매중** 상태가 된다.
    @PostMapping
    // RESTful한 API는 행동의 결과로 반영된 자원의 상태를 반환함이 옳다
    public ResponseDTO create(@Valid @RequestBody ItemDTO dto) {
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setMessage("동록이 완료되었습니다");
        service.create(dto);
        return responseDto;
    }
    @GetMapping
    public Page<ItemDTO> readPage(
            @RequestParam(value = "page", defaultValue = "0") Long page,
            @RequestParam(value = "limit", defaultValue = "5") Long limit
    ){
        return service.readItemAll(page, limit);
    }

    @GetMapping("/{id}") //딘일 조회
    public ItemDTO read(@PathVariable("id") Long id) {
        return service.readItem(id);
    }



     @PutMapping("/{id}") //업데이트 하는데 비번을 첨부해야함  성공으로 "message": "물품이 수정되었습니다."
    public ResponseDTO update(
             @PathVariable("id") Long id,
             @RequestBody ItemDTO dto)   // HTTP Request Body
     { ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("물품이 수정되었습니다.");
      service.update(id,dto);
      return responseDTO;
    }


    @DeleteMapping("/{id}")
    public ResponseDTO delete(
            @PathVariable("id") Long id,
            @RequestBody ItemDTO dto)
     {
    return service.delete(id, dto);
    }

    @PutMapping(
            value ="/{id2}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseDTO addImage(
            @PathVariable("id2") Long id,
            @RequestParam("image") MultipartFile Image,
            @RequestParam ("password") String password){

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("이미지가 등록되었습니다.");
        service.updateImage(id,password,Image);
        return responseDTO;

    }

}



