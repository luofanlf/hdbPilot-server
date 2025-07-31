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

    @GetMapping
    public List<Comment> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/average")
    public Double getAverageRating() {
        return commentService.getAverageRating();
    }
}
