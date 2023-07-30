package com.example.market.service;


import com.example.market.dto.CommentDTO;
import com.example.market.dto.ItemDTO;
import com.example.market.dto.NegotiationDTO;
import com.example.market.entity.CommentEntity;
import com.example.market.entity.ItemEntity;
import com.example.market.entity.NegotiationEntity;
import com.example.market.repository.CommentRepository;
import com.example.market.repository.ItemRepository;
import com.example.market.repository.NegotiationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NegotiationService {
    private final NegotiationRepository negotiationrepository;
    private final ItemRepository itemrepository;

    public NegotiationDTO negocreate(NegotiationDTO dto, Long id) {
        Optional<ItemEntity> optionalEntity
                = itemrepository.findById(id);
        if (!optionalEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        //등록된 물품에 대하여 구매 제안을 등록할 수 있다.
        //이때 반드시 포함되어야 하는 내용은 **대상 물품, 제안 가격, 작성자**이다.
        //또한 구매 제안을 등록할 때, 비밀번호 항목을 추가해서 등록한다.
        // 구매 제안이 등록될 때, 제안의 상태는 **제안** 상태가 된다.

        NegotiationEntity newNego = new NegotiationEntity();
        newNego.setWriter(dto.getWriter());
        newNego.setPassword(dto.getPassword());
        newNego.setSuggestedPrice(dto.getSuggestedPrice());
        newNego.setItemId(id);
        newNego.setStatus("제안");

        return NegotiationDTO.fromEntity(negotiationrepository.save(newNego));
    }

    public Page<NegotiationDTO> readMaster(Long itemId, Long page, Long limit, String writer, String password) {
        Optional<ItemEntity> optionalEntity
                = itemrepository.findById(itemId);
        if (!optionalEntity.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        ItemEntity itemEntity = optionalEntity.get();
        if (!itemEntity.getPassword().equals(password) && itemEntity.getWriter().equals(writer)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        List<NegotiationEntity> allNegoList = negotiationrepository.findAll();
        List<NegotiationEntity> negolist = new ArrayList<>();
        for (NegotiationEntity elem : allNegoList) {
            if (elem.getItemId() == itemId) {
                negolist.add(elem);
            }
        }
        Pageable pageable = PageRequest.of(Math.toIntExact(page), Math.toIntExact(limit));
        int startIndex = Math.toIntExact(pageable.getOffset());
        int endIndex = Math.min(startIndex + Math.toIntExact(pageable.getPageSize()), negolist.size());
        List<NegotiationEntity> pagedEntityList = negolist.subList(startIndex, endIndex);


        List<NegotiationDTO> NegotiationDtoList = new ArrayList<>();
        for (NegotiationEntity target : pagedEntityList) {
            NegotiationDtoList.add(NegotiationDTO.fromEntity(target));
        }
        return new PageImpl<>(NegotiationDtoList, pageable, negolist.size());


    }

    public Page<NegotiationDTO> readUser(Long page, Long limit, String writer, String password) {
        List<NegotiationEntity> allNegoList
                = negotiationrepository.findAll();
        List<NegotiationEntity> buyerNegoList = new ArrayList<>();
        for (NegotiationEntity elem : allNegoList) {
            if (elem.getWriter().equals(writer) && elem.getPassword().equals(password)) {
                buyerNegoList.add(elem);
            }
        }
        if (buyerNegoList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Pageable pageable = PageRequest.of(Math.toIntExact(page), Math.toIntExact(limit));
        int startIndex = Math.toIntExact(pageable.getOffset());
        int endIndex = Math.min(startIndex + Math.toIntExact(pageable.getPageSize()), buyerNegoList.size());
        List<NegotiationEntity> pagedEntityList = buyerNegoList.subList(startIndex, endIndex);


        List<NegotiationDTO> NegotiationDtoList = new ArrayList<>();
        for (NegotiationEntity target : pagedEntityList) {
            NegotiationDtoList.add(NegotiationDTO.fromEntity(target));
        }
        return new PageImpl<>(NegotiationDtoList, pageable, buyerNegoList.size());


    }

    public NegotiationDTO  proposal(NegotiationDTO dto, Long id) {
        // proposalId가 있는지 체크
        Optional<NegotiationEntity> optionalNegoEntity
                = negotiationrepository.findById(id);
        // 없으면 예외 던지기
        if (!optionalNegoEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        NegotiationEntity upNego = optionalNegoEntity.get();
        // 작성자의 아이디 비밀번호 체크 이후
        if (upNego.getPassword().equals(dto.getPassword()) && upNego.getWriter().equals(dto.getWriter())) {
            // upNego의 suggestedPrice 변경
            upNego.setSuggestedPrice(dto.getSuggestedPrice());
            negotiationrepository.save(upNego);
            return NegotiationDTO.fromEntity(upNego);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public NegotiationDTO changeStatus(NegotiationDTO dto, Long proposalId, Long itemId) {
        // proposalId가 있는지 체크
        Optional<NegotiationEntity> optionalNegoEntity
                = negotiationrepository.findById(proposalId);
        // 없으면 예외 던지기
        if (!optionalNegoEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // item의 writer 체크를 위한 itemEntity
        Optional<ItemEntity> optionalItemEntity = itemrepository.findById(itemId);
        if(!optionalItemEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        NegotiationEntity upNego = optionalNegoEntity.get();
        ItemEntity upItem = optionalItemEntity.get();

        // dto와 upNego의 id/pw 체크 이후 dto와 upItem의 id/pw 체크
        if (upItem.getPassword().equals(dto.getPassword())
                && upItem.getWriter().equals(dto.getWriter())
                && upNego.getStatus().equals("제안")) {
            // upNego의 status 변경
            upNego.setStatus(dto.getStatus());
            negotiationrepository.save(upNego);
            return NegotiationDTO.fromEntity(upNego);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public NegotiationDTO acceptProposal(NegotiationDTO dto, Long proposalId, Long itemId) {
        // proposalId가 있는지 체크
        Optional<NegotiationEntity> optionalNegoEntity
                = negotiationrepository.findById(proposalId);
        if (!optionalNegoEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // 상태를 바꾸기 위한 itemEntity
        Optional<ItemEntity> optionalItemEntity = itemrepository.findById(itemId);
        if(!optionalItemEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // negotiation list 가져오기
        List<NegotiationEntity> negoList = negotiationrepository.findByItemId(itemId);

        NegotiationEntity upNego = optionalNegoEntity.get();
        ItemEntity upItem = optionalItemEntity.get();

        // 작성자의 아이디 비밀번호 체크 이후 status가 제안일 경우
        if (upNego.getPassword().equals(dto.getPassword())
                && upNego.getWriter().equals(dto.getWriter())
                && upNego.getStatus().equals("수락")) {
            // upNego의 status를 확정으로 변경
            upNego.setStatus(dto.getStatus());
            // 대상 물품의 상태를 판매 완료로 변경
            upItem.setStatus("판매완료");
            // 다른 제안을 모두 거절로 변경
            for(NegotiationEntity nego : negoList){
                if(nego.getStatus().equals("확정")) continue;

                nego.setStatus("거절");
            }

            negotiationrepository.save(upNego);
            return NegotiationDTO.fromEntity(upNego);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }


    public void deleteProposal(NegotiationDTO dto, Long proposald) {
        Optional<NegotiationEntity> optinalNegoentity = negotiationrepository.findById(proposald);
        if (!optinalNegoentity.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        NegotiationEntity upNego = optinalNegoentity.get();
        if (upNego.getPassword().equals(dto.getPassword()) && upNego.getWriter().equals(dto.getWriter())) {
            negotiationrepository.deleteById(proposald);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
