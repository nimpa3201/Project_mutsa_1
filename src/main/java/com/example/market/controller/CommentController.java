package com.example.market.controller;


import com.example.market.dto.CommentDTO;
import com.example.market.dto.ResponseDTO;
import com.example.market.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

@RestController  // @ResponseBody 생략 가능
@RequestMapping("/items/{itemId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;
    private final UserDetailsManager manager;

    @PostMapping
    public ResponseDTO createComment (@Valid @RequestBody CommentDTO dto ,
                                      @PathVariable ("itemId") Long itemId,
                                      Authentication authentication) {
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setMessage("댓글이 등록되었습니다.");
        service.commentcreate(dto,itemId,authentication);
        return responseDto;
    }

    @GetMapping
    public Page<CommentDTO> readPage(
            @PathVariable("itemId") Long itemId,
            @RequestParam(value = "page", defaultValue = "0") Long page,
            @RequestParam(value = "limit", defaultValue = "10") Long limit
    ){
        return service.readCommentAll(itemId, page, limit);
    }
    @PutMapping("/{commentId}")
    public ResponseDTO updateComment(@PathVariable("commentId") Long commentId,
                                     @RequestBody CommentDTO dto,
                                        Authentication authentication){
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("댓글이 수정되었습니다.");
        service.update(commentId,dto,authentication);
        return responseDTO;
    }



    @DeleteMapping("/{commentId}")
    public ResponseDTO deleteComment(@PathVariable("commentId") Long commentId,
                                     @RequestBody CommentDTO dto ,
                                     Authentication authentication){
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("댓글이 삭제되었습니다");
        service.delete(commentId,dto,authentication);
        return responseDTO;
    }

    @PutMapping("/{commentId}/reply")
    public  ResponseDTO replay(@PathVariable("commentId") Long commentId,
                               @RequestBody CommentDTO dto,
                               Authentication authentication){
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("댓글에 답변이 추가되었습니다.");
        service.userReply(commentId,dto,authentication);
        return responseDTO;

    }
}
