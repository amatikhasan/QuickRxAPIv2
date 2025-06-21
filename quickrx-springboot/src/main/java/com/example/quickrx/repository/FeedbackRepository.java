package com.example.quickrx.repository;

import com.example.quickrx.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserId(Long userId);

    // To mimic: "SELECT feedback.*,users.name AS user_name FROM feedback LEFT JOIN users ON feedback.user_id=users.id ORDER BY feedback.id DESC"
    // This can be achieved by eager fetching user or handling it in a service layer with a DTO.
    // For a direct query if needed:
    @Query("SELECT f FROM Feedback f JOIN FETCH f.user ORDER BY f.id DESC")
    List<Feedback> findAllWithUserOrderByDesc();
}
