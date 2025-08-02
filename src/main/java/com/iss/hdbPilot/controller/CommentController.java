package com.iss.hdbPilot.controller;

import com.iss.hdbPilot.model.entity.Comment;
import com.iss.hdbPilot.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public String submitComment(@RequestBody Comment comment) {
        commentService.submitComment(comment);
        return "Comment submitted successfully";
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
