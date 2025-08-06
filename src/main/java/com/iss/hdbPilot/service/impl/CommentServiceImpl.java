package com.iss.hdbPilot.service.impl;

import com.iss.hdbPilot.model.entity.Comment;
import com.iss.hdbPilot.mapper.CommentMapper;
import com.iss.hdbPilot.service.CommentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    // 构造函数注入
    public CommentServiceImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public void submitComment(Comment comment) {
        commentMapper.insert(comment);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentMapper.selectList(null);
    }


    @Override
    public Double getAverageRating() {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.select("AVG(rating) AS avg_rating");
        return commentMapper.selectObjs(wrapper)
                .stream()
                .findFirst()
                .map(obj -> ((Number)obj).doubleValue())
                .orElse(0.0);
    }
    @Override
    public List<Comment> getCommentsByProperty(Long propertyId) {
        QueryWrapper<Comment> query = new QueryWrapper<>();
        query.eq("property_id", propertyId);
        return commentMapper.selectList(query);
    }

    @Override
    public Double getAverageRatingByProperty(Long propertyId) {
        QueryWrapper<Comment> query = new QueryWrapper<>();
        query.eq("property_id", propertyId);
        List<Comment> comments = commentMapper.selectList(query);
        if (comments.isEmpty()) return 0.0;
        return comments.stream().mapToInt(Comment::getRating).average().orElse(0.0);
    }

    @Override
    public void deleteCommentsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        QueryWrapper<Comment> query = new QueryWrapper<>();
        query.in("id", ids);
        commentMapper.delete(query);
    }

}
