package com.iss.hdbPilot;

import com.iss.hdbPilot.mapper.CommentMapper;
import com.iss.hdbPilot.model.entity.Comment;
import com.iss.hdbPilot.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSubmitComment() {
        Comment comment = new Comment();
        comment.setRating(5);
        comment.setContent("测试评论");
        comment.setPropertyId(1L);
        comment.setCreatedAt(LocalDateTime.now());

        when(commentMapper.insert(comment)).thenReturn(1); // ✅ 正确模拟返回值

        commentService.submitComment(comment);

        verify(commentMapper, times(1)).insert(comment); // ✅ 验证调用次数
    }


    @Test
    void testGetAllComments() {
        List<Comment> comments = Arrays.asList(
                new Comment(), new Comment()
        );
        when(commentMapper.selectList(null)).thenReturn(comments);

        List<Comment> result = commentService.getAllComments();

        assertEquals(2, result.size());
    }

    @Test
    void testGetCommentsByProperty() {
        Long propertyId = 1L;
        List<Comment> comments = Arrays.asList(
                new Comment(), new Comment()
        );
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
        Comment c1 = new Comment();
        c1.setRating(4);
        Comment c2 = new Comment();
        c2.setRating(5);
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
}