package com.example.quickrx.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeedbackRequestDto {
    // user_id will be taken from the authenticated principal in the service/controller
    @NotBlank(message = "Feedback content cannot be blank")
    private String feedback;
}
