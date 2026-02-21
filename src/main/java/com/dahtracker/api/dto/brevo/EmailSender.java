package com.dahtracker.api.dto.brevo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 22-2-2025: change login signup feature - Brevo Email Sender DTO

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailSender {

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;
}
