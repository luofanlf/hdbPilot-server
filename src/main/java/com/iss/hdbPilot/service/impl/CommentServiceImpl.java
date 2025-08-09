package com.iss.hdbPilot.service.impl;

import com.iss.hdbPilot.model.entity.Comment;
import com.iss.hdbPilot.mapper.CommentMapper;
import com.iss.hdbPilot.service.CommentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import com.iss.hdbPilot.mapper.PropertyMapper;
import com.iss.hdbPilot.model.entity.Property;
import com.iss.hdbPilot.exception.BusinessException;
import com.iss.hdbPilot.mapper.UserMapper;
import com.iss.hdbPilot.model.entity.User;
import com.iss.hdbPilot.model.vo.CommentVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PropertyMapper propertyMapper;
    private final UserMapper userMapper;

    // 构造函数注入
    public CommentServiceImpl(CommentMapper commentMapper, PropertyMapper propertyMapper, UserMapper userMapper) {
        this.commentMapper = commentMapper;
        this.propertyMapper = propertyMapper;
        this.userMapper = userMapper;
    }

    @Override
    public void submitComment(Comment comment) {
        Property property = propertyMapper.selectById(comment.getPropertyId());
        if (property != null && property.getSellerId() != null && property.getSellerId().equals(comment.getUserId())) {
            throw new BusinessException("You cannot comment on your own property.");
        }
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
    public List<Comment> getUserComments(Long userId) {
        QueryWrapper<Comment> query = new QueryWrapper<>();
        query.eq("user_id", userId)
             .orderByDesc("created_at");
        return commentMapper.selectList(query);
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
    
    @Override
    public void deleteCommentById(Long commentId) {
        if (commentId == null) {
            return;
        }
        QueryWrapper<Comment> query = new QueryWrapper<>();
        query.eq("id", commentId);
        commentMapper.delete(query);
    }

    @Override
    public List<CommentVO> getCommentVOsByProperty(Long propertyId) {
        List<Comment> comments = getCommentsByProperty(propertyId);
        List<CommentVO> result = new ArrayList<>();
        for (Comment c : comments) {
            User user = userMapper.selectById(c.getUserId());
            CommentVO vo = new CommentVO();
            vo.setId(c.getId());
            vo.setPropertyId(c.getPropertyId());
            vo.setUsername(user != null ? user.getUsername() : "Unknown");
            vo.setRating(c.getRating());
            vo.setContent(c.getContent());
            vo.setCreatedAt(c.getCreatedAt());
            result.add(vo);
        }
        return result;
    }

}
