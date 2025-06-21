package com.example.quickrx.dto;

import com.example.quickrx.model.Feedback;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FeedbackResponseDto {
    private Long id;
    private Long userId;
    private String userName; // For convenience
    private String userEmail; // For convenience
    private String feedback;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FeedbackResponseDto fromEntity(Feedback feedbackEntity) {
        if (feedbackEntity == null) return null;

        FeedbackResponseDto dto = new FeedbackResponseDto();
        dto.setId(feedbackEntity.getId());
        dto.setFeedback(feedbackEntity.getFeedback());
        dto.setCreatedAt(feedbackEntity.getCreatedAt());
        dto.setUpdatedAt(feedbackEntity.getUpdatedAt());

        if (feedbackEntity.getUser() != null) {
            dto.setUserId(feedbackEntity.getUser().getId());
            dto.setUserName(feedbackEntity.getUser().getName());
            dto.setUserEmail(feedbackEntity.getUser().getEmail());
        }
        return dto;
    }
}
