package com.dahtracker.api.service;

import com.dahtracker.api.model.Card;
import com.dahtracker.api.repository.CardRepository;
import com.dahtracker.api.repository.ExpenseRepository;
import com.dahtracker.api.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public Card createCard(Card card) {
        return cardRepository.save(card);
    }

    @Override
    public Optional<Card> findById(Long id) {
        return cardRepository.findById(id);
    }

    @Override
    public List<Card> findByUserId(Long userId) {
        return cardRepository.findByUserIdOrderBySeqAsc(userId);
    }

    @Override
    public Card updateCard(Card card) {
        return cardRepository.save(card);
    }

    @Override
    @Transactional
    public void deleteCard(Long id) {
        // First, delete all expenses associated with this card
        expenseRepository.deleteByCardId(id);
        // Then delete the card
        cardRepository.deleteById(id);
    }
}
