package com.dahtracker.api.controller;

import com.dahtracker.api.model.Card;
import com.dahtracker.api.model.User;
import com.dahtracker.api.dto.CardRequest;
import com.dahtracker.api.dto.CardResponse;
import com.dahtracker.api.dto.MessageResponse;
import com.dahtracker.api.service.CardService;
import com.dahtracker.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllCards(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CardResponse> cards = cardService.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCard(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card card = cardService.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Unauthorized"));
        }

        return ResponseEntity.ok(toResponse(card));
    }

    @PostMapping
    public ResponseEntity<?> createCard(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CardRequest request) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card card = new Card();
        card.setUser(user);
        card.setName(request.getName());
        card.setType(request.getType());
        card.setDefaultCard(request.getDefaultCard() != null ? request.getDefaultCard() : false);
        card.setColor(request.getColor());
        card.setHolder(request.getHolder());
        card.setSeq(request.getSeq());

        Card savedCard = cardService.createCard(card);

        return ResponseEntity.ok(toResponse(savedCard));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCard(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody CardRequest request) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card card = cardService.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Unauthorized"));
        }

        if (request.getName() != null) card.setName(request.getName());
        if (request.getType() != null) card.setType(request.getType());
        if (request.getDefaultCard() != null) card.setDefaultCard(request.getDefaultCard());
        if (request.getColor() != null) card.setColor(request.getColor());
        if (request.getHolder() != null) card.setHolder(request.getHolder());
        if (request.getSeq() != null) card.setSeq(request.getSeq());

        Card updatedCard = cardService.updateCard(card);

        return ResponseEntity.ok(toResponse(updatedCard));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card card = cardService.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Unauthorized"));
        }

        cardService.deleteCard(id);

        return ResponseEntity.ok(new MessageResponse("Card deleted successfully"));
    }

    private CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getName(),
                card.getType().name(),
                card.getDefaultCard(),
                card.getColor(),
                card.getHolder(),
                card.getSeq(),
                card.getCreatedAt(),
                card.getUpdatedAt()
        );
    }
}
