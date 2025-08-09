package com.iss.hdbPilot;

import com.iss.hdbPilot.mapper.CommentMapper;
import com.iss.hdbPilot.mapper.PropertyMapper;
import com.iss.hdbPilot.mapper.UserMapper;
import com.iss.hdbPilot.model.entity.Comment;
import com.iss.hdbPilot.model.entity.Property;
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
}
