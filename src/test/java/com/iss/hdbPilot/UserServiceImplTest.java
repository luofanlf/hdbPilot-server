package com.iss.hdbPilot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.mapper.UserMapper;
import com.iss.hdbPilot.model.dto.UserUpdateRequest;
import com.iss.hdbPilot.model.entity.User;
import com.iss.hdbPilot.model.vo.UserVO;

import com.iss.hdbPilot.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getSession()).thenReturn(session);
    }

    // ---------- login ----------
    @Test
    void testLoginSuccess() {
        // Arrange: mock user with correct credentials
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("test");
        mockUser.setPasswordHash(userService.getEncryptedPassword("12345678"));

        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockUser);

        // Act
        Long result = userService.login("test", "12345678", request);

        // Assert
        assertEquals(1L, result);
        verify(session).setAttribute("user", mockUser);
    }

    @Test
    void testLoginFailure_NullInput() {
        // Null username or password should throw RuntimeException
        assertThrows(RuntimeException.class, () -> userService.login(null, "123", request));
        assertThrows(RuntimeException.class, () -> userService.login("abc", null, request));
    }

    @Test
    void testLoginFailure_IncorrectPassword() {
        // Incorrect credentials should throw RuntimeException
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        assertThrows(RuntimeException.class, () -> userService.login("test", "wrong", request));
    }

    // ---------- adminLogin ----------
    @Test
    void testAdminLoginSuccess() {
        // Arrange: admin user with correct credentials
        User admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setPasswordHash(userService.getEncryptedPassword("admin1234"));
        admin.setUserRole("admin");

        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(admin);

        // Act
        Long result = userService.adminLogin("admin", "admin1234", request);

        // Assert
        assertEquals(2L, result);
    }

    @Test
    void testAdminLogin_NotAdmin() {
        // Arrange: user is not admin
        User normal = new User();
        normal.setUsername("user");
        normal.setPasswordHash(userService.getEncryptedPassword("pass1234"));
        normal.setUserRole("user");

        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(normal);

        // Should throw RuntimeException if not admin
        assertThrows(RuntimeException.class, () -> userService.adminLogin("user", "pass1234", request));
    }

    // ---------- register ----------
    @Test
    void testRegisterSuccess() {
        // Arrange: no duplicate username
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(3L);
            return 1;
        });

        // Act
        Long userId = userService.register("newuser", "password123", "password123");

        // Assert
        assertEquals(3L, userId);
    }

    @Test
    void testRegisterFailure_Validation() {
        // Username too short, password too short, mismatched passwords
        assertThrows(RuntimeException.class, () -> userService.register("a", "pwd", "pwd"));
        assertThrows(RuntimeException.class, () -> userService.register("user", "short", "short"));
        assertThrows(RuntimeException.class, () -> userService.register("user", "password123", "wrongpass"));
    }

    @Test
    void testRegisterFailure_UsernameExists() {
        // Arrange: username already exists
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);
        assertThrows(RuntimeException.class, () -> userService.register("existing", "password123", "password123"));
    }

    // ---------- getCurrentUser ----------
    @Test
    void testGetCurrentUserSuccess() {
        // Arrange: session contains user object
        User user = new User();
        when(session.getAttribute("user")).thenReturn(user);

        // Act & Assert
        assertEquals(user, userService.getCurrentUser(request));
    }

    @Test
    void testGetCurrentUserFailure() {
        // Session does not contain user
        when(session.getAttribute("user")).thenReturn(null);
        assertThrows(RuntimeException.class, () -> userService.getCurrentUser(request));
    }

    // ---------- removeUserById ----------
    @Test
    void testRemoveUserById() {
        // Arrange: deleteById returns success
        when(userMapper.deleteById(5L)).thenReturn(1);

        // Act & Assert
        assertTrue(userService.removeUserById(5L));
    }

    @Test
    void testRemoveUserById_Invalid() {
        // Invalid IDs should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> userService.removeUserById(null));
        assertThrows(IllegalArgumentException.class, () -> userService.removeUserById(-1L));
    }

    // ---------- removeUsersByIds ----------
    @Test
    void testRemoveUsersByIds() {
        // Arrange: deleteBatchIds returns success
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(userMapper.deleteBatchIds(ids)).thenReturn(3);

        // Act & Assert
        assertTrue(userService.removeUsersByIds(ids));
    }

    // ---------- updateUser ----------
    @Test
    void testUpdateUser() {
        // Arrange: prepare UserUpdateRequest
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setId(1L);
        updateRequest.setUsername("newName");
        updateRequest.setEmail("email@example.com");
        updateRequest.setNickname("nick");

        // Spy to mock the update() method's return value
        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(true).when(spyService).update(any(), any());

        // Act & Assert
        assertTrue(spyService.updateUser(updateRequest));
    }

    // ---------- listUsersByPage ----------
    @Test
    void testListUsersByPage() {
        // Arrange: mock page result with one user
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Page<User> userPage = new Page<>();
        userPage.setRecords(List.of(user));
        userPage.setTotal(1);

        when(userMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(userPage);

        // Act
        Page<UserVO> result = userService.listUsersByPage(1, 10, "user");

        // Assert
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    // ---------- getEncryptedPassword ----------
    @Test
    void testGetEncryptedPassword() {
        // Encrypting should return 32-char MD5 hash
        String encrypted = userService.getEncryptedPassword("12345678");
        assertNotNull(encrypted);
        assertEquals(32, encrypted.length());
    }
}
