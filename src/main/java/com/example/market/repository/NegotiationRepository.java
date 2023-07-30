package com.example.market.repository;

import com.example.market.entity.ItemEntity;
import com.example.market.entity.NegotiationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NegotiationRepository extends JpaRepository<NegotiationEntity, Long> {
    List<NegotiationEntity> findByItemId(Long itemId);
}
