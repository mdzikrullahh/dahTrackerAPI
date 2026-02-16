package com.dahtracker.api.service;

import com.dahtracker.api.model.Card;

import java.util.List;
import java.util.Optional;

public interface CardService {
    Card createCard(Card card);
    Optional<Card> findById(Long id);
    List<Card> findByUserId(Long userId);
    Card updateCard(Card card);
    void deleteCard(Long id);
}
