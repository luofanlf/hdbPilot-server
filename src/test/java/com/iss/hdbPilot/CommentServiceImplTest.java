package com.iss.hdbPilot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iss.hdbPilot.mapper.CommentMapper;
import com.iss.hdbPilot.mapper.PropertyMapper;
import com.iss.hdbPilot.mapper.UserMapper;
import com.iss.hdbPilot.model.entity.Comment;
import com.iss.hdbPilot.model.entity.Property;
import com.iss.hdbPilot.model.entity.User;
import com.iss.hdbPilot.model.vo.CommentVO;
import com.iss.hdbPilot.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private PropertyMapper propertyMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSubmitComment_valid() {
        Comment comment = new Comment();
        comment.setRating(5);
        comment.setContent("测试评论");
        comment.setPropertyId(1L);
        comment.setUserId(2L);
        comment.setCreatedAt(LocalDateTime.now());

        Property property = new Property();
        property.setSellerId(3L); // 不同用户，允许评论

        when(propertyMapper.selectById(1L)).thenReturn(property);
        when(commentMapper.insert(comment)).thenReturn(1);

        commentService.submitComment(comment);

        verify(commentMapper, times(1)).insert(comment);
    }

    @Test
    void testSubmitComment_selfComment_shouldThrow() {
        Comment comment = new Comment();
        comment.setPropertyId(1L);
        comment.setUserId(2L);

        Property property = new Property();
        property.setSellerId(2L); // 同一个用户，禁止评论

        when(propertyMapper.selectById(1L)).thenReturn(property);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            commentService.submitComment(comment);
        });

        assertTrue(ex.getMessage().contains("cannot comment"));
    }

    @Test
    void testGetAllComments() {
        List<Comment> comments = Arrays.asList(new Comment(), new Comment());
        when(commentMapper.selectList(null)).thenReturn(comments);

        List<Comment> result = commentService.getAllComments();

        assertEquals(2, result.size());
    }

    @Test
    void testGetCommentsByProperty() {
        Long propertyId = 1L;
        List<Comment> comments = Arrays.asList(new Comment(), new Comment());
        when(commentMapper.selectList(any())).thenReturn(comments);

        List<Comment> result = commentService.getCommentsByProperty(propertyId);

        assertEquals(2, result.size());
    }

    @Test
    void testGetAverageRating() {
        when(commentMapper.selectObjs(any())).thenReturn(Collections.singletonList(4.5));

        Double avg = commentService.getAverageRating();

        assertEquals(4.5, avg);
    }

    @Test
    void testGetAverageRatingByProperty_WithComments() {
        Long propertyId = 1L;
        Comment c1 = new Comment(); c1.setRating(4);
        Comment c2 = new Comment(); c2.setRating(5);
        when(commentMapper.selectList(any())).thenReturn(Arrays.asList(c1, c2));

        Double avg = commentService.getAverageRatingByProperty(propertyId);

        assertEquals(4.5, avg);
    }

    @Test
    void testGetAverageRatingByProperty_NoComments() {
        Long propertyId = 2L;
        when(commentMapper.selectList(any())).thenReturn(Collections.emptyList());

        Double avg = commentService.getAverageRatingByProperty(propertyId);

        assertEquals(0.0, avg);
    }

    @Test
    void testGetUserComments() {
        Long userId = 2L;
        Comment c1 = new Comment();
        c1.setUserId(userId);
        Comment c2 = new Comment();
        c2.setUserId(userId);
        List<Comment> comments = Arrays.asList(c1, c2);

        when(commentMapper.selectList(any(QueryWrapper.class))).thenReturn(comments);

        List<Comment> result = commentService.getUserComments(userId);

        assertEquals(2, result.size());


        ArgumentCaptor<QueryWrapper> captor = ArgumentCaptor.forClass(QueryWrapper.class);
        verify(commentMapper).selectList(captor.capture());

        QueryWrapper actualWrapper = captor.getValue();

        String sqlSegment = actualWrapper.getSqlSegment();
        assertTrue(sqlSegment.contains("user_id"));
        assertTrue(sqlSegment.toLowerCase().contains("order by"));
    }

    @Test
    void testDeleteCommentsByIds_nullOrEmpty() {
        commentService.deleteCommentsByIds(null);
        commentService.deleteCommentsByIds(Collections.emptyList());
        verify(commentMapper, never()).delete(any());
    }

    @Test
    void testDeleteCommentsByIds_valid() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        commentService.deleteCommentsByIds(ids);

        ArgumentCaptor<QueryWrapper> captor = ArgumentCaptor.forClass(QueryWrapper.class);
        verify(commentMapper).delete(captor.capture());

        QueryWrapper actualWrapper = captor.getValue();
        String sqlSegment = actualWrapper.getSqlSegment();

        assertTrue(sqlSegment.contains("id"));
        assertTrue(sqlSegment.toLowerCase().contains("in"));
    }

    @Test
    void testDeleteCommentById_null() {
        commentService.deleteCommentById(null);
        verify(commentMapper, never()).delete(any());
    }

    @Test
    void testDeleteCommentById_valid() {
        Long commentId = 1L;
        commentService.deleteCommentById(commentId);
        verify(commentMapper).delete(argThat(wrapper ->
                wrapper.getSqlSegment().contains("id") && wrapper.getSqlSegment().contains("=")
        ));
    }

    @Test
    void testGetCommentVOsByProperty() {
        Long propertyId = 1L;

        Comment comment = new Comment();
        comment.setId(10L);
        comment.setPropertyId(propertyId);
        comment.setUserId(5L);
        comment.setRating(4);
        comment.setContent("Nice place");
        comment.setCreatedAt(LocalDateTime.now());

        User user = new User();
        user.setId(5L);
        user.setUsername("alice");

        when(commentMapper.selectList(any())).thenReturn(Collections.singletonList(comment));
        when(userMapper.selectById(5L)).thenReturn(user);

        List<CommentVO> vos = commentService.getCommentVOsByProperty(propertyId);

        assertEquals(1, vos.size());
        CommentVO vo = vos.get(0);
        assertEquals(comment.getId(), vo.getId());
        assertEquals("alice", vo.getUsername());
        assertEquals(comment.getRating(), vo.getRating());
        assertEquals(comment.getContent(), vo.getContent());
    }

    @Test
    void testSearchComments_withSearch() {
        String search = "test";
        int page = 1, size = 10;
        List<Comment> comments = Arrays.asList(new Comment(), new Comment());

        when(commentMapper.selectList(any(QueryWrapper.class))).thenReturn(comments);

        List<Comment> result = commentService.searchComments(search, page, size);

        assertEquals(2, result.size());
        verify(commentMapper).selectList(any(QueryWrapper.class));
    }


    @Test
    void testSearchComments_withoutSearch() {
        String search = " ";
        int page = 1, size = 10;
        List<Comment> comments = Arrays.asList(new Comment());

        when(commentMapper.selectList(any())).thenReturn(comments);

        List<Comment> result = commentService.searchComments(search, page, size);

        assertEquals(1, result.size());
        verify(commentMapper).selectList(argThat(wrapper ->
                !wrapper.getSqlSegment().contains("like") && wrapper.getSqlSegment().contains("LIMIT")
        ));
    }

    @Test
    void testCountSearchComments_withSearch() {
        String search = "hello";
        when(commentMapper.selectCount(any(QueryWrapper.class))).thenReturn(5L);

        long count = commentService.countSearchComments(search);

        assertEquals(5L, count);

        ArgumentCaptor<QueryWrapper> captor = ArgumentCaptor.forClass(QueryWrapper.class);
        verify(commentMapper).selectCount(captor.capture());

        QueryWrapper actualWrapper = captor.getValue();
        String sqlSegment = actualWrapper.getSqlSegment();
        assertTrue(sqlSegment.toLowerCase().contains("like"));
    }

    @Test
    void testCountSearchComments_withoutSearch() {
        String search = null;
        when(commentMapper.selectCount(any())).thenReturn(10L);

        long count = commentService.countSearchComments(search);

        assertEquals(10L, count);
        verify(commentMapper).selectCount(argThat(wrapper ->
                !wrapper.getSqlSegment().contains("like")
        ));
    }
}
