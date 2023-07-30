package com.example.market.service;


import com.example.market.dto.CommentDTO;
import com.example.market.dto.ItemDTO;
import com.example.market.entity.CommentEntity;
import com.example.market.entity.ItemEntity;
import com.example.market.repository.CommentRepository;
import com.example.market.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final ItemRepository itemrepository;
    private final CommentRepository commentrepository;

    public CommentDTO commentcreate(CommentDTO dto, Long id) {
        Optional<ItemEntity> optionalEntity
                = itemrepository.findById(id);
        if (!optionalEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        CommentEntity newComment = new CommentEntity();
        newComment.setWriter(dto.getWriter());
        newComment.setPassword(dto.getPassword());
        newComment.setContent(dto.getContent());
        newComment.setItemId(id);
        return CommentDTO.fromEntity(commentrepository.save(newComment));
    }


    public CommentDTO update(Long commentId, CommentDTO dto) {
        Optional<CommentEntity> optionalComment
                = commentrepository.findById(commentId);
        if (!optionalComment.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        CommentEntity upComment = optionalComment.get();
        if (upComment.getPassword().equals(dto.getPassword())) {
            upComment.setWriter(dto.getWriter());
            upComment.setPassword(dto.getPassword());
            upComment.setContent(dto.getContent());
            commentrepository.save(upComment);
            return CommentDTO.fromEntity(upComment);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

    }

    public Page<CommentDTO> readCommentAll(Long itemId, Long page, Long limit) {
        Pageable pageable = PageRequest.of(page.intValue(), limit.intValue());

        Page<CommentEntity> commentPage = commentrepository.findByItemId(itemId, pageable);
        List<CommentDTO> commentDtoList = commentPage.getContent().stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(commentDtoList, pageable, commentPage.getTotalElements());
    }

    public void delete(Long commentId, CommentDTO dto) {
        Optional<CommentEntity> optionalComment
                = commentrepository.findById(commentId);
        if (!optionalComment.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        CommentEntity upComment = optionalComment.get();
        if (!upComment.getPassword().equals(dto.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        commentrepository.deleteById(commentId);
        }
//    public CommentDTO userReply(Long commentId, CommentDTO dto)  {
//
//    }
    }





