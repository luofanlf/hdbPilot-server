package com.iss.hdbPilot.controller;

import com.iss.hdbPilot.model.dto.CommentRequest;
import com.iss.hdbPilot.model.entity.Comment;
import com.iss.hdbPilot.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/comments")
@CrossOrigin
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> submitComment(@RequestBody CommentRequest request) {
        Comment comment = new Comment();
        comment.setRating(request.getRating());
        comment.setContent(request.getContent());
        comment.setPropertyId(request.getPropertyId());
        comment.setCreatedAt(LocalDateTime.now());

        commentService.submitComment(comment);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Comment submitted successfully");
        return ResponseEntity.ok(response);
    }



    @GetMapping("/property/{propertyId}")
    public List<Comment> getCommentsByProperty(@PathVariable Long propertyId) {
        return commentService.getCommentsByProperty(propertyId);
    }

    @GetMapping("/property/{propertyId}/average")
    public Double getAverageRatingByProperty(@PathVariable Long propertyId) {
        return commentService.getAverageRatingByProperty(propertyId);
    }

}
