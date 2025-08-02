package com.iss.hdbPilot.service;

import com.iss.hdbPilot.model.entity.Comment;
import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService {

    /**
     * 提交评论
     * @param comment 评论实体
     */
    void submitComment(Comment comment);

    /**
     * 获取所有评论
     * @return 评论列表
     */
    List<Comment> getAllComments();

    /**
     * 获取平均评分
     * @return 平均评分
     */
    Double getAverageRating();
    List<Comment> getCommentsByProperty(Long propertyId);
    Double getAverageRatingByProperty(Long propertyId);

}

