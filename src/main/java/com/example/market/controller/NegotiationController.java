package com.example.market.controller;


import com.example.market.dto.CommentDTO;
import com.example.market.dto.ItemDTO;
import com.example.market.dto.NegotiationDTO;
import com.example.market.dto.ResponseDTO;
import com.example.market.service.ItemService;
import com.example.market.service.NegotiationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController  // @ResponseBody 생략 가능
@RequestMapping("/items/{itemId}/proposals")
@RequiredArgsConstructor
public class NegotiationController {
    private final NegotiationService service;
    //등록된 물품에 대하여 구매 제안을 등록할 수 있다.
    //이때 반드시 포함되어야 하는 내용은 **대상 물품, 제안 가격, 작성자**이다.
    //또한 구매 제안을 등록할 때, 비밀번호 항목을 추가해서 등록한다.
    // 구매 제안이 등록될 때, 제안의 상태는 **제안** 상태가 된다.

    @PostMapping
    public ResponseDTO negocreate (@Valid @RequestBody NegotiationDTO dto ,
                                      @PathVariable("itemId") Long itemId) {
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setMessage("구매 제안이 등록되었습니다.");
        service.negocreate(dto,itemId);
        return responseDto;
    }

    @GetMapping
    public Page<NegotiationDTO> readMaster(@PathVariable("itemId")Long itemId,
            @RequestParam(value = "page", defaultValue = "0") Long page,
            @RequestParam(value = "limit", defaultValue = "5") Long limit,
            @RequestParam(value = "writer") String writer,
            @RequestParam(value = "password") String password

    ){
        return service.readMaster(itemId,page,limit,writer,password);
    }
    @GetMapping("/user")
    public Page<NegotiationDTO> readUser(
            @RequestParam(value = "page", defaultValue = "0") Long page,
            @RequestParam(value = "limit", defaultValue = "5") Long limit,
            @RequestParam(value = "writer") String writer,
            @RequestParam(value = "password") String password){
        return service.readUser(page,limit,writer,password);
    }

    //
    @PutMapping("/{proposalId}")
    public ResponseDTO updateProposal( @RequestBody NegotiationDTO dto,
                                       @PathVariable("proposalId") Long proposalId,
                                       @PathVariable("itemId") Long itemId){

        ResponseDTO responseDTO = new ResponseDTO();
        // status가 있으면 제안 금액 변경
        if(dto.getStatus().equals("수락") || dto.getStatus().equals("거절")){
            responseDTO.setMessage("제안의 상태가 변경되었습니다.");
            service.changeStatus(dto, proposalId, itemId);
        }
        else if(dto.getStatus().equals("확정")){
            responseDTO.setMessage("구매가 확정되었습니다.");
            service.acceptProposal(dto, proposalId, itemId);
        }
        // suggestedPrice가 있으면 상태 변경
        else if(dto.getSuggestedPrice() != null){
            responseDTO.setMessage("제안이 수정되었습니다.");
            service.proposal(dto,proposalId);
        }

        return responseDTO;
    }







    //등록된 제안은 삭제가 가능하다.
    //이때, 제안이 등록될때 추가한 **작성자와 비밀번호**를 첨부해야 한다.
    @DeleteMapping("/{proposalId}")
    public ResponseDTO delete(@RequestBody NegotiationDTO dto,
                              @PathVariable("proposalId") Long proposalId){
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("제안을 삭제했습니다.");
        service.deleteProposal(dto,proposalId);
        return responseDTO;
    }


}
