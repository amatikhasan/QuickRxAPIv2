package com.example.quickrx.service;

import com.example.quickrx.dto.FeedbackRequestDto;
import com.example.quickrx.dto.FeedbackResponseDto;
import com.example.quickrx.model.Feedback;
import com.example.quickrx.model.User;
import com.example.quickrx.repository.FeedbackRepository;
import com.example.quickrx.repository.UserRepository;
import com.example.quickrx.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository; // To link feedback to a user

    @Transactional
    public FeedbackResponseDto createFeedback(FeedbackRequestDto feedbackRequestDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail + ". Cannot submit feedback."));

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setFeedback(feedbackRequestDto.getFeedback());

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return FeedbackResponseDto.fromEntity(savedFeedback);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getAllFeedback() {
        // The PHP code joins with users table to get user_name.
        // The FeedbackRepository's findAllWithUserOrderByDesc already handles this.
        return feedbackRepository.findAllWithUserOrderByDesc().stream()
                .map(FeedbackResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FeedbackResponseDto getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
        // If you need user details fetched, ensure the repository method does that or do it here.
        // FeedbackResponseDto.fromEntity will map user details if present.
        return FeedbackResponseDto.fromEntity(feedback);
    }

    @Transactional
    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new ResourceNotFoundException("Feedback not found with id: " + id);
        }
        feedbackRepository.deleteById(id);
    }
}
