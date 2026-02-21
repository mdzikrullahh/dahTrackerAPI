package com.dahtracker.api.dto.brevo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// 22-2-2025: change login signup feature - Brevo Email Recipient DTO

@Data
public class EmailRecipient {

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    // Default constructor
    public EmailRecipient() {
    }

    // Constructor with email only
    public EmailRecipient(String email) {
        this.email = email;
    }

    // Constructor with email and name
    public EmailRecipient(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
