package com.iss.hdbPilot.controller;

import com.iss.hdbPilot.model.dto.CommentRequest;
import com.iss.hdbPilot.model.entity.Comment;
import com.iss.hdbPilot.model.vo.CommentVO;
import com.iss.hdbPilot.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.iss.hdbPilot.model.entity.User;
import com.iss.hdbPilot.common.BaseResponse;
import javax.servlet.http.HttpServletRequest;


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
        comment.setUserId(request.getUserId());

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
    
    @GetMapping("/user/{userId}")
    public List<Comment> getUserComments(@PathVariable Long userId) {
        return commentService.getUserComments(userId);
    }
    
    @GetMapping("/user/current")
    public BaseResponse<List<Comment>> getCurrentUserComments(HttpServletRequest request) {
        try {
            // 从session中获取当前用户
            User currentUser = (User) request.getSession().getAttribute("user");
            if (currentUser == null) {
                return new BaseResponse<>(-1, null, "User not logged in");
            }
            
            List<Comment> comments = commentService.getUserComments(currentUser.getId());
            return new BaseResponse<>(0, comments, "Success");
        } catch (Exception e) {
            return new BaseResponse<>(-1, null, "Failed to get user comments: " + e.getMessage());
        }
    }
    
    // get all comments
    @GetMapping
    public List<Comment> getAllComments() {
        return commentService.getAllComments();
    }

    // Deleting comments in bulk
    @PostMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteComments(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No ids provided"));
        }
        commentService.deleteCommentsByIds(ids);
        return ResponseEntity.ok(Map.of("message", "Comments deleted successfully"));
    }
    
    // Delete single comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteCommentById(commentId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Comment deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete comment: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/property/{propertyId}/with-username")
    public List<CommentVO> getCommentVOsByProperty(@PathVariable Long propertyId) {
        return commentService.getCommentVOsByProperty(propertyId);
    }

}