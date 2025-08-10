package com.iss.hdbPilot.service;

import com.iss.hdbPilot.model.entity.Comment;
import com.iss.hdbPilot.model.vo.CommentVO;
import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService {

    /**
     * Submit a comment
     * @param comment Comment entity
     */
    void submitComment(Comment comment);

    /**
     * Get all comments
     * @return List of comments
     */
    List<Comment> getAllComments();

    /**
     * Get average rating
     * @return Average rating
     */
    Double getAverageRating();

    /**
     * Get comments by property ID
     * @param propertyId Property ID
     * @return List of comments for the specified property
     */
    List<Comment> getCommentsByProperty(Long propertyId);

    /**
     * Get average rating by property ID
     * @param propertyId Property ID
     * @return Average rating for the specified property
     */
    Double getAverageRatingByProperty(Long propertyId);

    /**
     * Delete comments in bulk by IDs
     * @param ids List of comment IDs
     */
    void deleteCommentsByIds(List<Long> ids);

    /**
     * Get property comments as view objects (including user information)
     * @param propertyId Property ID
     * @return List of CommentVO objects
     */
    List<CommentVO> getCommentVOsByProperty(Long propertyId);

    /**
     * Get comments by user ID
     * @param userId User ID
     * @return List of comments for the specified user
     */
    List<Comment> getUserComments(Long userId);

    /**
     * Delete a single comment by ID
     * @param commentId Comment ID
     */
    void deleteCommentById(Long commentId);

    // ========= New Methods =========

    /**
     * Paginated + fuzzy search for comments
     * @param search  Search keyword (fuzzy match on content)
     * @param page    Page number (starting from 1)
     * @param size    Number of items per page
     * @return List of comments (including userId)
     */
    List<Comment> searchComments(String search, int page, int size);

    /**
     * Get total count of comments after fuzzy search (for calculating total pages)
     * @param search Search keyword
     * @return Total number of matching comments
     */
    long countSearchComments(String search);
}
