package com.iss.hdbPilot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.mapper.UserMapper;
import com.iss.hdbPilot.model.dto.AdminUserUpdateRequest;
import com.iss.hdbPilot.model.entity.User;
import com.iss.hdbPilot.model.vo.UserVO;
import com.iss.hdbPilot.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setPasswordHash(userService.getEncryptedPassword("12345678"));

        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockUser);

        MockHttpServletRequest request = new MockHttpServletRequest();
        Long userId = userService.login("user1", "12345678", request);

        assertEquals(1L, userId);
        assertEquals(mockUser, request.getSession().getAttribute("user"));
    }

    @Test
    void testLoginFail() {
        when(userMapper.selectOne(any())).thenReturn(null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertThrows(RuntimeException.class, () -> userService.login("x", "x", request));
    }

    @Test
    void testAdminLoginSuccess() {
        User admin = new User();
        admin.setId(2L);
        admin.setUserRole("admin");
        admin.setPasswordHash(userService.getEncryptedPassword("adminpass"));
        when(userMapper.selectOne(any())).thenReturn(admin);

        MockHttpServletRequest request = new MockHttpServletRequest();
        Long id = userService.adminLogin("admin", "adminpass", request);
        assertEquals(2L, id);
    }

    @Test
    void testAdminLoginNotAdmin() {
        User user = new User();
        user.setUserRole("user");
        user.setPasswordHash(userService.getEncryptedPassword("test"));
        when(userMapper.selectOne(any())).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        assertThrows(RuntimeException.class, () -> userService.adminLogin("user", "test", request));
    }

    @Test
    void testRegisterSuccess() {
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(userMapper.insert(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(10L);
            return 1;
        });

        Long id = userService.register("newuser", "12345678", "12345678");
        assertEquals(10L, id);
    }

    @Test
    void testRegisterInvalidUsername() {
        assertThrows(RuntimeException.class, () -> userService.register("a", "12345678", "12345678"));
    }

    @Test
    void testGetCurrentUserSuccess() {
        User mockUser = new User();
        mockUser.setId(1L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("user", mockUser);
        assertEquals(mockUser, userService.getCurrentUser(request));
    }

    @Test
    void testGetCurrentUserNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertThrows(RuntimeException.class, () -> userService.getCurrentUser(request));
    }

    @Test
    void testListUsersByPage() {
        Page<User> mockPage = new Page<>();
        User user = new User();
        user.setUsername("test");
        mockPage.setRecords(Collections.singletonList(user));
        mockPage.setTotal(1);

        when(userMapper.selectPage(any(), any())).thenReturn(mockPage);

        Page<UserVO> result = userService.listUsersByPage(1, 10, "test");
        assertEquals(1, result.getTotal());
        assertEquals("test", result.getRecords().get(0).getUsername());
    }

    @Test
    void testRemoveUserByIdSuccess() {
        when(userMapper.deleteById(1L)).thenReturn(1);
        assertTrue(userService.removeUserById(1L));
    }

    @Test
    void testRemoveUserByIdFail() {
        assertThrows(IllegalArgumentException.class, () -> userService.removeUserById(-1L));
    }

    @Test
    void testRemoveUsersByIds() {
        when(userMapper.deleteBatchIds(anyList())).thenReturn(2);
        assertTrue(userService.removeUsersByIds(Arrays.asList(1L, 2L)));
    }

    @Test
    void testUpdateUser() {
        AdminUserUpdateRequest req = new AdminUserUpdateRequest();
        req.setId(1L);
        req.setUsername("test");
        req.setEmail("t@test.com");
        req.setNickname("tt");

        when(userMapper.update(any(), any())).thenReturn(1);
        assertTrue(userService.updateUser(req));
    }

    @Test
    void testUpdateUsernameSuccess() {
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(userMapper.updateById(any())).thenReturn(1);
        assertTrue(userService.updateUsername(1L, "newname"));
    }

    @Test
    void testUpdateUsernameFail() {
        when(userMapper.selectCount(any())).thenReturn(1L);
        assertFalse(userService.updateUsername(1L, "usedname"));
    }

    @Test
    void testUpdatePasswordSuccess() {
        User user = new User();
        user.setId(1L);
        user.setPasswordHash(userService.getEncryptedPassword("oldpass"));
        when(userMapper.selectById(1L)).thenReturn(user);
        when(userMapper.updateById(any())).thenReturn(1);
        assertTrue(userService.updatePassword(1L, "oldpass", "newpassword"));
    }

    @Test
    void testUpdatePasswordFail() {
        when(userMapper.selectById(1L)).thenReturn(null);
        assertFalse(userService.updatePassword(1L, "x", "y"));
    }

    @Test
    void testUpdateEmail() {
        when(userMapper.updateById(any())).thenReturn(1);
        assertTrue(userService.updateEmail(1L, "new@mail.com"));
    }

    @Test
    void testUpdateNickname() {
        when(userMapper.updateById(any())).thenReturn(1);
        assertTrue(userService.updateNickname(1L, "newnick"));
    }

    @Test
    void testUpdateBio() {
        when(userMapper.updateById(any())).thenReturn(1);
        assertTrue(userService.updateBio(1L, "new bio"));
        assertTrue(userService.updateBio(1L, null)); // 清空 bio
    }
}
