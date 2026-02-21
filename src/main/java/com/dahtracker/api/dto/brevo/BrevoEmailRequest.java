package com.dahtracker.api.dto.brevo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 22-2-2025: change login signup feature - Brevo Email Request DTO

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrevoEmailRequest {

    @JsonProperty("sender")
    private EmailSender sender;

    @JsonProperty("to")
    private List<EmailRecipient> to;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("htmlContent")
    private String htmlContent;
}
