package com.example.market.service;


import com.example.market.authentication.UserRepository;
import com.example.market.dto.CommentDTO;
import com.example.market.dto.ItemDTO;
import com.example.market.entity.CommentEntity;
import com.example.market.entity.ItemEntity;
import com.example.market.entity.UserEntity;
import com.example.market.repository.CommentRepository;
import com.example.market.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
    private final UserRepository userrepository;

    public CommentDTO commentcreate(CommentDTO dto, Long id ,Authentication authentication) {
        Optional<ItemEntity> itemOptionalEntity
                = itemrepository.findById(id);
        Optional<UserEntity> userOptionalEntity
                = userrepository.findByUsername(authentication.getName());

        if (!itemOptionalEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        ItemEntity newItem = itemOptionalEntity.get();
        UserEntity newUser = userOptionalEntity.get();// 외래키를 설정하기위해 기존의 pk 가져옴

        CommentEntity newComment = new CommentEntity();
        newComment.setContent(dto.getContent());
        newComment.setSalesItem(newItem);
        newComment.setUsers(newUser);
        return CommentDTO.fromEntity(commentrepository.save(newComment));
    }


    public CommentDTO update(Long commentId, CommentDTO dto,Authentication authentication) {
        Optional<UserEntity> userOptionalEntity
                = userrepository.findByUsername(authentication.getName());
        Optional<CommentEntity> optionalComment
                = commentrepository.findById(commentId);
        if (!optionalComment.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        UserEntity newUser = userOptionalEntity.get();
        CommentEntity upComment = optionalComment.get();
        if (upComment.getUsers().getUsername().equals(authentication.getName())) {
            upComment.setContent(dto.getContent());
            commentrepository.save(upComment);
            return CommentDTO.fromEntity(upComment);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

    }

    public Page<CommentDTO> readCommentAll(Long itemId, Long page, Long limit) {
        Pageable pageable = PageRequest.of(page.intValue(), limit.intValue());

        Page<CommentEntity> commentPage = commentrepository.findBySalesItem_Id(itemId, pageable);
        List<CommentDTO> commentDtoList = commentPage.getContent().stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(commentDtoList, pageable, commentPage.getTotalElements());
    }

    public void delete(Long commentId, CommentDTO dto,Authentication authentication) {
        Optional<CommentEntity> optionalComment
                = commentrepository.findById(commentId);

        if (!optionalComment.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        CommentEntity upComment = optionalComment.get();

        if (!upComment.getUsers().getUsername().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        commentrepository.deleteById(commentId);

        }
    public CommentDTO userReply(Long commentId, CommentDTO dto,Authentication authentication)  {
        Optional<CommentEntity> optionalComment
                = commentrepository.findById(commentId);
        if(!optionalComment.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        CommentEntity upComment = optionalComment.get();
        if(upComment.getSalesItem().getUsers().getUsername().equals(authentication.getName())){
           upComment.setReply(dto.getReply());
            commentrepository.save(upComment);
            return CommentDTO.fromEntity(upComment);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
     }
    }





