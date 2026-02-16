package com.dahtracker.api.repository;

import com.dahtracker.api.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUserIdOrderBySeqAsc(Long userId);
}