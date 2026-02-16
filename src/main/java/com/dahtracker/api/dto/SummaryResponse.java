package com.dahtracker.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SummaryResponse {
    private String period;
    private BigDecimal total;
}
