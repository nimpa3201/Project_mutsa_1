package com.example.market.service;


import com.example.market.authentication.UserRepository;
import com.example.market.dto.NegotiationDTO;
import com.example.market.entity.ItemEntity;
import com.example.market.entity.NegotiationEntity;
import com.example.market.entity.UserEntity;
import com.example.market.repository.ItemRepository;
import com.example.market.repository.NegotiationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
    private final UserRepository userrepository;


    public NegotiationDTO negocreate(NegotiationDTO dto, Long id, Authentication authentication) {
        Optional<ItemEntity> optionalEntity
                = itemrepository.findById(id);
        Optional<UserEntity> userOptionalEntity
                = userrepository.findByUsername(authentication.getName());
        if (!optionalEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        ItemEntity newItem = optionalEntity.get();
        UserEntity newUser = userOptionalEntity.get();
        NegotiationEntity newNego = new NegotiationEntity();
        newNego.setSuggestedPrice(dto.getSuggestedPrice());
        newNego.setStatus("제안");
        newNego.setSalesItem(newItem);
        newNego.setUsers(newUser);


        return NegotiationDTO.fromEntity(negotiationrepository.save(newNego));
    }

    public Page<NegotiationDTO> readMaster(Long page, Long limit, Authentication authentication) {


        List<NegotiationEntity> allNegoList = negotiationrepository.findAll();
        List<NegotiationEntity> negolist = new ArrayList<>();
        for (NegotiationEntity elem : allNegoList) {
            if (elem.getSalesItem().getUsers().getUsername().equals(authentication.getName())) {
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

    public Page<NegotiationDTO> readUser(Long page, Long limit, String writer, String password, Authentication authentication) {
        List<NegotiationEntity> allNegoList
                = negotiationrepository.findAll();
        List<NegotiationEntity> buyerNegoList = new ArrayList<>();
        for (NegotiationEntity elem : allNegoList) {
            if (elem.getUsers().getUsername().equals(authentication)) {
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

    public NegotiationDTO proposal(NegotiationDTO dto, Long id, Authentication authentication) {
        // proposalId가 있는지 체크
        Optional<NegotiationEntity> optionalNegoEntity
                = negotiationrepository.findById(id);
        // 없으면 예외 던지기
        if (!optionalNegoEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        NegotiationEntity upNego = optionalNegoEntity.get();
        // 작성자의 아이디 비밀번호 체크 이후
        if (upNego.getUsers().getUsername().equals(authentication.getName())) {
            // upNego의 suggestedPrice 변경
            upNego.setSuggestedPrice(dto.getSuggestedPrice());
            negotiationrepository.save(upNego);
            return NegotiationDTO.fromEntity(upNego);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public NegotiationDTO changeStatus(NegotiationDTO dto, Long proposalId, Long itemId, Authentication authentication) {
        // proposalId가 있는지 체크
        Optional<NegotiationEntity> optionalNegoEntity
                = negotiationrepository.findById(proposalId);
        // 없으면 예외 던지기
        if (!optionalNegoEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // item의 writer 체크를 위한 itemEntity
        Optional<ItemEntity> optionalItemEntity = itemrepository.findById(itemId);
        if (!optionalItemEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        NegotiationEntity upNego = optionalNegoEntity.get();
        ItemEntity upItem = optionalItemEntity.get();


        if (upNego.getUsers().getUsername().equals(authentication.getName())) {
            // upNego의 status 변경
            upNego.setStatus(dto.getStatus());
            negotiationrepository.save(upNego);
            return NegotiationDTO.fromEntity(upNego);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public NegotiationDTO acceptProposal(NegotiationDTO dto, Long proposalId, Long itemId, Authentication authentication) {
        // proposalId가 있는지 체크
        Optional<NegotiationEntity> optionalNegoEntity
                = negotiationrepository.findById(proposalId);
        if (!optionalNegoEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // 상태를 바꾸기 위한 itemEntity
        Optional<ItemEntity> optionalItemEntity = itemrepository.findById(itemId);
        if (!optionalItemEntity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // negotiation list 가져오기
        List<NegotiationEntity> negoList = negotiationrepository.findBySalesItem_Id(itemId);

        NegotiationEntity nego = optionalNegoEntity.get();
        ItemEntity upItem = optionalItemEntity.get();

// 작성자의 아이디 비밀번호 체크 이후 status가 제안일 경우

        // 다른 제안을 모두 거절로 변경
        for (NegotiationEntity upNego : negoList) {
            if (upNego.getUsers().getUsername().equals(authentication.getName())) {
                if (upNego.getStatus().equals("확정")) {
                    // upNego의 status 확정으로 변경
                    upNego.setStatus(dto.getStatus());
                    continue;
                }

                // 다른 제안을 모두 거절로 변경
                upNego.setStatus("거절");

            }

        }
        upItem.setStatus("판매완료");
        negotiationrepository.save(nego);
        return NegotiationDTO.fromEntity(nego);

        // 대상 물품의 상태를 판매 완료로 변경

    }









    public void deleteProposal(NegotiationDTO dto, Long proposald,Authentication authentication) {
        Optional<NegotiationEntity> optinalNegoentity
                = negotiationrepository.findById(proposald);
        if (!optinalNegoentity.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        NegotiationEntity upNego = optinalNegoentity.get();
        if (!upNego.getUsers().getUsername().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
            negotiationrepository.deleteById(proposald);
        }

    }



