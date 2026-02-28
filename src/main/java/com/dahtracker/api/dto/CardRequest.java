package com.dahtracker.api.dto;

import com.dahtracker.api.model.Card;
import lombok.Data;

@Data
public class CardRequest {
    private String name;
    private Card.CardType type;
    private Boolean defaultCard;
    private String color;
    private String textColor;
    private String holder;
    private Integer seq;
}
