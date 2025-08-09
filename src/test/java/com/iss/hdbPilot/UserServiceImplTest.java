package com.iss.hdbPilot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.mapper.*;
import com.iss.hdbPilot.model.dto.AdminUserUpdateRequest;
import com.iss.hdbPilot.model.entity.*;
import com.iss.hdbPilot.model.vo.UserVO;
import com.iss.hdbPilot.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PropertyMapper propertyMapper;

    @Mock
    private PropertyImageMapper propertyImageMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private FavoriteMapper favoriteMapper;

    @Mock
    private S3Client s3Client;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private final String SALT = "luofan";

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPasswordHash(DigestUtils.md5DigestAsHex((SALT + "password123").getBytes()));
        mockUser.setUserRole("user");
        mockUser.setEmail("test@example.com");
        mockUser.setNickname("Test User");
        mockUser.setBio("Test bio");
        mockUser.setAvatarUrl("https://example.com/avatar.jpg");
    }

    // ======================== Login Tests ========================

    @Test
    void testLogin_Success() {
        // Given
        when(request.getSession()).thenReturn(session);
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockUser);

        // When
        Long userId = userService.login("testuser", "password123", request);

        // Then
        assertEquals(1L, userId);
        verify(session).setAttribute("user", mockUser);
        verify(userMapper).selectOne(any(QueryWrapper.class));
    }

    @Test
    void testLogin_NullUsername() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.login(null, "password123", request));
        assertEquals("Username and password cannot be null", exception.getMessage());
    }

    @Test
    void testLogin_NullPassword() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.login("testuser", null, request));
        assertEquals("Username and password cannot be null", exception.getMessage());
    }

    @Test
    void testLogin_UserNotFound() {
        // Given
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.login("testuser", "wrongpassword", request));
        assertEquals("username or password is incorrect", exception.getMessage());
    }

    // ======================== Admin Login Tests ========================

    @Test
    void testAdminLogin_Success() {
        // Given
        mockUser.setUserRole("admin");
        when(request.getSession()).thenReturn(session);
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockUser);

        // When
        Long userId = userService.adminLogin("testuser", "password123", request);

        // Then
        assertEquals(1L, userId);
        verify(session).setAttribute("user", mockUser);
    }

    @Test
    void testAdminLogin_NullUsername() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.adminLogin(null, "password123", request));
        assertEquals("Username and password cannot be null", exception.getMessage());
    }

    @Test
    void testAdminLogin_NullPassword() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.adminLogin("testuser", null, request));
        assertEquals("Username and password cannot be null", exception.getMessage());
    }

    @Test
    void testAdminLogin_UserNotFound() {
        // Given
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.adminLogin("testuser", "wrongpassword", request));
        assertEquals("username or password is incorrect", exception.getMessage());
    }

    @Test
    void testAdminLogin_NotAdmin() {
        // Given
        mockUser.setUserRole("user"); // Not admin
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockUser);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.adminLogin("testuser", "password123", request));
        assertEquals("user is not admin", exception.getMessage());
    }

    // ======================== Register Tests ========================

    @Test
    void testRegister_Success() {
        // Given
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        });

        // When
        Long userId = userService.register("newuser", "password123", "password123");

        // Then
        assertEquals(1L, userId);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("newuser", capturedUser.getUsername());
        assertEquals("user", capturedUser.getUserRole());
    }

    @Test
    void testRegister_NullUsername() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register(null, "password123", "password123"));
        assertEquals("Username and password cannot be null", exception.getMessage());
    }

    @Test
    void testRegister_NullPassword() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register("newuser", null, "password123"));
        assertEquals("Username and password cannot be null", exception.getMessage());
    }

    @Test
    void testRegister_NullConfirmPassword() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register("newuser", "password123", null));
        assertEquals("Username and password cannot be null", exception.getMessage());
    }

    @Test
    void testRegister_UsernameTooShort() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register("usr", "password123", "password123"));
        assertEquals("Username must be between 4 and 16 characters long", exception.getMessage());
    }

    @Test
    void testRegister_UsernameTooLong() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register("verylongusernamethatistoolong", "password123", "password123"));
        assertEquals("Username must be between 4 and 16 characters long", exception.getMessage());
    }

    @Test
    void testRegister_PasswordTooShort() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register("newuser", "short", "short"));
        assertEquals("Password must be at least 8 characters long", exception.getMessage());
    }

    @Test
    void testRegister_PasswordsDoNotMatch() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register("newuser", "password123", "password456"));
        assertEquals("Passwords do not match", exception.getMessage());
    }

    @Test
    void testRegister_UsernameExists() {
        // Given
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register("existinguser", "password123", "password123"));
        assertEquals("Username already exists", exception.getMessage());
    }

    // ======================== Get Current User Tests ========================

    @Test
    void testGetCurrentUser_Success() {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(mockUser);

        // When
        User result = userService.getCurrentUser(request);

        // Then
        assertEquals(mockUser, result);
    }

    @Test
    void testGetCurrentUser_UserNotFound() {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getCurrentUser(request));
        assertEquals("User not found", exception.getMessage());
    }

    // ======================== Password Encryption Tests ========================

    @Test
    void testGetEncryptedPassword() {
        // When
        String encrypted = userService.getEncryptedPassword("password123");
        String expected = DigestUtils.md5DigestAsHex((SALT + "password123").getBytes());

        // Then
        assertEquals(expected, encrypted);
    }

    // ======================== List Users Tests ========================

    @Test
    void testListUsersByPage_WithoutKeyword() {
        // Given
        Page<User> userPage = new Page<>();
        userPage.setCurrent(1);
        userPage.setSize(10);
        userPage.setTotal(1);
        userPage.setRecords(Collections.singletonList(mockUser));
        when(userMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(userPage);

        // When
        Page<UserVO> result = userService.listUsersByPage(1, 10, null);

        // Then
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        verify(userMapper).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    void testListUsersByPage_WithKeyword() {
        // Given
        Page<User> userPage = new Page<>();
        userPage.setCurrent(1);
        userPage.setSize(10);
        userPage.setTotal(1);
        userPage.setRecords(Collections.singletonList(mockUser));
        when(userMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(userPage);

        // When
        Page<UserVO> result = userService.listUsersByPage(1, 10, "test");

        // Then
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void testListUsersByPage_WithEmptyKeyword() {
        // Given
        Page<User> userPage = new Page<>();
        userPage.setCurrent(1);
        userPage.setSize(10);
        userPage.setTotal(0);
        userPage.setRecords(Collections.emptyList());
        when(userMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(userPage);

        // When
        Page<UserVO> result = userService.listUsersByPage(1, 10, "   ");

        // Then
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getRecords().size());
    }

    // ======================== Remove User Tests ========================

    @Test
    void testRemoveUserById_Success() {
        // Given
        List<Property> properties = Arrays.asList(
                createProperty(1L, 1L),
                createProperty(2L, 1L)
        );
        when(propertyMapper.selectList(any(QueryWrapper.class))).thenReturn(properties);
        when(userMapper.deleteById(1L)).thenReturn(1);

        // When
        boolean result = userService.removeUserById(1L);

        // Then
        assertTrue(result);
        verify(commentMapper, times(2)).delete(any(QueryWrapper.class)); // Delete user comments + property comments
        verify(propertyImageMapper).delete(any(QueryWrapper.class)); // Delete property images
        verify(favoriteMapper, times(2)).delete(any(QueryWrapper.class)); // Delete property favorites + user favorites
        verify(propertyMapper).deleteBatchIds(Arrays.asList(1L, 2L));
        verify(userMapper).deleteById(1L);
    }

    @Test
    void testRemoveUserById_NullId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.removeUserById(null));
        assertEquals("User ID is illegal", exception.getMessage());
    }

    @Test
    void testRemoveUserById_InvalidId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.removeUserById(0L));
        assertEquals("User ID is illegal", exception.getMessage());
    }

    @Test
    void testRemoveUserById_NoProperties() {
        // Given
        when(propertyMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
        when(userMapper.deleteById(1L)).thenReturn(1);

        // When
        boolean result = userService.removeUserById(1L);

        // Then
        assertTrue(result);
        verify(propertyImageMapper, never()).delete(any(QueryWrapper.class));
        verify(propertyMapper, never()).deleteBatchIds(anyList());
    }

    @Test
    void testRemoveUsersByIds_Success() {
        // Given
        List<Long> userIds = Arrays.asList(1L, 2L);
        List<Property> properties = Arrays.asList(
                createProperty(1L, 1L),
                createProperty(2L, 2L)
        );
        when(propertyMapper.selectList(any(QueryWrapper.class))).thenReturn(properties);
        when(userMapper.deleteBatchIds(userIds)).thenReturn(2);

        // When
        boolean result = userService.removeUsersByIds(userIds);

        // Then
        assertTrue(result);
        verify(commentMapper, times(2)).delete(any(QueryWrapper.class)); // Delete user comments + property comments
        verify(propertyImageMapper).delete(any(QueryWrapper.class)); // Delete property images
        verify(favoriteMapper, times(2)).delete(any(QueryWrapper.class)); // Delete property favorites + user favorites
        verify(propertyMapper).deleteBatchIds(Arrays.asList(1L, 2L));
        verify(userMapper).deleteBatchIds(userIds);
    }

    // ======================== Update User Tests ========================

    @Test
    void testUpdateUser_Success() {
        // Given
        AdminUserUpdateRequest updateRequest = new AdminUserUpdateRequest();
        updateRequest.setId(1L);
        updateRequest.setUsername("updateduser");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setNickname("Updated User");

        // Create a spy to mock the parent class method
        UserServiceImpl spyService = spy(userService);
        doReturn(true).when(spyService).update(any(User.class), any(UpdateWrapper.class));

        // When
        boolean result = spyService.updateUser(updateRequest);

        // Then
        assertTrue(result);

        // Verify the update method was called
        verify(spyService).update(any(User.class), any(UpdateWrapper.class));
    }

    // ======================== User Self-Management Tests ========================

    @Test
    void testUpdateUsername_Success() {
        // Given
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        boolean result = userService.updateUsername(1L, "newusername");

        // Then
        assertTrue(result);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(userCaptor.capture());
        assertEquals("newusername", userCaptor.getValue().getUsername());
    }

    @Test
    void testUpdateUsername_NullUserId() {
        // When
        boolean result = userService.updateUsername(null, "newusername");

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdateUsername_NullUsername() {
        // When
        boolean result = userService.updateUsername(1L, null);

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdateUsername_EmptyUsername() {
        // When
        boolean result = userService.updateUsername(1L, "");

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdateUsername_UsernameExists() {
        // Given
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // When
        boolean result = userService.updateUsername(1L, "existinguser");

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdatePassword_Success() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        boolean result = userService.updatePassword(1L, "password123", "newpassword123");

        // Then
        assertTrue(result);
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testUpdatePassword_NullUserId() {
        // When
        boolean result = userService.updatePassword(null, "oldpass", "newpass123");

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdatePassword_NullOldPassword() {
        // When
        boolean result = userService.updatePassword(1L, null, "newpass123");

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdatePassword_NullNewPassword() {
        // When
        boolean result = userService.updatePassword(1L, "oldpass", null);

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdatePassword_NewPasswordTooShort() {
        // When
        boolean result = userService.updatePassword(1L, "oldpass", "short");

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdatePassword_UserNotFound() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(null);

        // When
        boolean result = userService.updatePassword(1L, "password123", "newpassword123");

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdatePassword_WrongOldPassword() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(mockUser);

        // When
        boolean result = userService.updatePassword(1L, "wrongpassword", "newpassword123");

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdateEmail_Success() {
        // Given
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        boolean result = userService.updateEmail(1L, "newemail@example.com");

        // Then
        assertTrue(result);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(userCaptor.capture());
        assertEquals("newemail@example.com", userCaptor.getValue().getEmail());
    }

    @Test
    void testUpdateEmail_NullUserId() {
        // When
        boolean result = userService.updateEmail(null, "newemail@example.com");

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdateEmail_NullEmail() {
        // When
        boolean result = userService.updateEmail(1L, null);

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdateEmail_EmptyEmail() {
        // When
        boolean result = userService.updateEmail(1L, "");

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdateNickname_Success() {
        // Given
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        boolean result = userService.updateNickname(1L, "New Nickname");

        // Then
        assertTrue(result);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(userCaptor.capture());
        assertEquals("New Nickname", userCaptor.getValue().getNickname());
    }

    @Test
    void testUpdateNickname_NullUserId() {
        // When
        boolean result = userService.updateNickname(null, "New Nickname");

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdateNickname_NullNickname() {
        // When
        boolean result = userService.updateNickname(1L, null);

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdateNickname_EmptyNickname() {
        // When
        boolean result = userService.updateNickname(1L, "");

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdateBio_Success() {
        // Given
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        boolean result = userService.updateBio(1L, "New bio");

        // Then
        assertTrue(result);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(userCaptor.capture());
        assertEquals("New bio", userCaptor.getValue().getBio());
    }

    @Test
    void testUpdateBio_NullUserId() {
        // When
        boolean result = userService.updateBio(null, "New bio");

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdateBio_NullBio() {
        // Given
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        boolean result = userService.updateBio(1L, null);

        // Then
        assertTrue(result); // Should allow clearing bio
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(userCaptor.capture());
        assertEquals("", userCaptor.getValue().getBio());
    }

    // ======================== Count Users Test ========================

    @Test
    void testCountAllUsers() {
        // Given
        when(userMapper.selectCount(null)).thenReturn(100L);

        // When
        long count = userService.countAllUsers();

        // Then
        assertEquals(100L, count);
        verify(userMapper).selectCount(null);
    }

    // ======================== Update Avatar Tests ========================

    @Test
    void testUpdateAvatar_Success() throws IOException {
        // Given
        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(multipartFile.getSize()).thenReturn(4L);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        String result = userService.updateAvatar(1L, multipartFile);

        // Then
        assertNotNull(result);
        assertTrue(result.startsWith("https://hdb-pilot.s3.ap-southeast-1.amazonaws.com/"));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testUpdateAvatar_UserNotFound() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateAvatar(1L, multipartFile));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testUpdateAvatar_S3UploadFails() throws IOException {
        // Given
        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(multipartFile.getSize()).thenReturn(4L);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("S3 error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateAvatar(1L, multipartFile));
        assertTrue(exception.getMessage().contains("upload image failed"));
    }

    @Test
    void testUpdateAvatar_DatabaseUpdateFails() throws IOException {
        // Given
        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(multipartFile.getSize()).thenReturn(4L);
        when(userMapper.updateById(any(User.class))).thenThrow(new RuntimeException("DB error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateAvatar(1L, multipartFile));
        assertTrue(exception.getMessage().contains("save image info failed"));
    }

    // ======================== Helper Methods ========================

    private Property createProperty(Long id, Long sellerId) {
        Property property = new Property();
        property.setId(id);
        property.setSellerId(sellerId);
        return property;
    }
}