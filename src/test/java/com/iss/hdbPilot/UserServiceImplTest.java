package com.iss.hdbPilot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.mapper.UserMapper;
import com.iss.hdbPilot.model.entity.User;
import com.iss.hdbPilot.model.vo.UserVO;
import com.iss.hdbPilot.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test getEncryptedPassword method
    @Test
    public void testGetEncryptedPassword() {
        String password = "mypassword";
        String salted = "luofan" + password;
        String expectedMd5 = DigestUtils.md5DigestAsHex(salted.getBytes());

        String actualMd5 = userService.getEncryptedPassword(password);

        assertEquals(expectedMd5, actualMd5);
    }

    // Test successful login
    @Test
    public void testLoginSuccess() {
        String username = "testuser";
        String password = "testpass";

        // Setup encrypted password
        String encryptedPassword = userService.getEncryptedPassword(password);

        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername(username);
        fakeUser.setPasswordHash(encryptedPassword);

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // We cannot verify QueryWrapper internal state in mock, so we mock selectOne directly:
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(fakeUser);
        when(request.getSession()).thenReturn(session);

        Long userId = userService.login(username, password, request);

        assertEquals(fakeUser.getId(), userId);
        verify(session, times(1)).setAttribute(eq("user"), eq(fakeUser));
    }

    // Test login with null username or password throws RuntimeException
    @Test
    public void testLoginNullUsernameOrPassword() {
        assertThrows(RuntimeException.class, () -> userService.login(null, "pass", request));
        assertThrows(RuntimeException.class, () -> userService.login("user", null, request));
    }

    // Test login with incorrect credentials throws RuntimeException
    @Test
    public void testLoginIncorrectCredentials() {
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        assertThrows(RuntimeException.class, () -> userService.login("user", "wrongpass", request));
    }

    // Test register success path
    @Test
    public void testRegisterSuccess() {
        String username = "newuser";
        String password = "password123";
        String confirmPassword = "password123";

        // No duplicate username
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);

        // Mock insert method to set ID on user
        doAnswer(invocation -> {
            User userArg = invocation.getArgument(0);
            userArg.setId(100L);
            return 1; // success
        }).when(userMapper).insert(any(User.class));

        Long id = userService.register(username, password, confirmPassword);

        assertEquals(100L, id);
    }

    // Test register with invalid parameters throws RuntimeException
    @Test
    public void testRegisterInvalidParams() {
        // Null checks
        assertThrows(RuntimeException.class, () -> userService.register(null, "pass", "pass"));
        assertThrows(RuntimeException.class, () -> userService.register("user", null, "pass"));
        assertThrows(RuntimeException.class, () -> userService.register("user", "pass", null));

        // Username length checks
        assertThrows(RuntimeException.class, () -> userService.register("abc", "password1", "password1"));
        assertThrows(RuntimeException.class, () -> userService.register("thisusernameistoolongggg", "password1", "password1"));

        // Password length check
        assertThrows(RuntimeException.class, () -> userService.register("username", "short", "short"));

        // Password match check
        assertThrows(RuntimeException.class, () -> userService.register("username", "password1", "password2"));
    }

    // Test register with existing username throws RuntimeException
    @Test
    public void testRegisterUsernameExists() {
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        assertThrows(RuntimeException.class, () -> userService.register("existinguser", "password123", "password123"));
    }

    // Test getCurrentUser success
    @Test
    public void testGetCurrentUserSuccess() {
        User user = new User();
        user.setUsername("currentuser");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);

        User result = userService.getCurrentUser(request);
        assertNotNull(result);
        assertEquals("currentuser", result.getUsername());
    }

    // Test getCurrentUser throws exception if user not found
    @Test
    public void testGetCurrentUserNotFound() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.getCurrentUser(request));
    }

    // Test listUsersByPage returns correct pagination and mapping
    @Test
    public void testListUsersByPage() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setUserRole("user");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setUserRole("user");

        Page<User> userPage = new Page<>(1, 10);
        userPage.setTotal(2);
        userPage.setRecords(Arrays.asList(user1, user2));

        when(userMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(userPage);

        // Mock the toVO method of User to convert to UserVO
        UserVO vo1 = new UserVO();
        vo1.setId(1L);
        vo1.setUsername("user1");

        UserVO vo2 = new UserVO();
        vo2.setId(2L);
        vo2.setUsername("user2");

        // Use spy to mock User.toVO()
        User spyUser1 = spy(user1);
        User spyUser2 = spy(user2);
        doReturn(vo1).when(spyUser1).toVO();
        doReturn(vo2).when(spyUser2).toVO();

        // Replace list with spy versions for test coverage on mapping
        userPage.setRecords(Arrays.asList(spyUser1, spyUser2));
        when(userMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(userPage);

        Page<UserVO> result = userService.listUsersByPage(1, 10);

        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
        assertEquals(2, result.getTotal());
        assertEquals(2, result.getRecords().size());
        assertEquals("user1", result.getRecords().get(0).getUsername());
        assertEquals("user2", result.getRecords().get(1).getUsername());
    }

    // Test removeUserById success
    @Test
    public void testRemoveUserByIdSuccess() {
        when(userMapper.deleteById(1L)).thenReturn(1);

        boolean result = userService.removeUserById(1L);

        assertTrue(result);
    }

    // Test removeUserById fails when id is invalid
    @Test
    public void testRemoveUserByIdInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> userService.removeUserById(null));
        assertThrows(IllegalArgumentException.class, () -> userService.removeUserById(0L));
        assertThrows(IllegalArgumentException.class, () -> userService.removeUserById(-10L));
    }

    // Test removeUserById returns false if delete fails
    @Test
    public void testRemoveUserByIdFailure() {
        when(userMapper.deleteById(100L)).thenReturn(0);

        boolean result = userService.removeUserById(100L);
        assertFalse(result);
    }
}
