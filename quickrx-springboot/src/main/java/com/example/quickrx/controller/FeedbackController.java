package com.example.quickrx.controller;

import com.example.quickrx.dto.FeedbackRequestDto;
import com.example.quickrx.dto.FeedbackResponseDto;
import com.example.quickrx.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    @PreAuthorize("hasRole('USER')") // Only authenticated users can submit feedback
    public ResponseEntity<FeedbackResponseDto> createFeedback(
            @Valid @RequestBody FeedbackRequestDto feedbackRequestDto,
            Authentication authentication) {
        String userEmail = authentication.getName(); // Assuming email is used as username in UserDetails
        FeedbackResponseDto createdFeedback = feedbackService.createFeedback(feedbackRequestDto, userEmail);
        return new ResponseEntity<>(createdFeedback, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Only admins can view all feedback
    public ResponseEntity<List<FeedbackResponseDto>> getAllFeedback() {
        List<FeedbackResponseDto> feedbackList = feedbackService.getAllFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only admins can view specific feedback by ID
    public ResponseEntity<FeedbackResponseDto> getFeedbackById(@PathVariable Long id) {
        FeedbackResponseDto feedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(feedback);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only admins can delete feedback
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
