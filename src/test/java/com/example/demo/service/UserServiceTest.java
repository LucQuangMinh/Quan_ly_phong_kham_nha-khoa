package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setId(2L);
        validUser.setUsername("testuser");
        validUser.setPassword("password123");
        validUser.setFullname("Test User");
        validUser.setRole("Bác sĩ");
        validUser.setStatus("Hoạt động");
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByUsernameOrEmail("testuser", "testuser")).thenReturn(Optional.of(validUser));

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        LoginResponse response = userService.login(request);

        assertTrue(response.isSuccess());
        assertEquals("Đăng nhập thành công", response.getMessage());
        assertEquals("Test User", response.getFullname());
    }

    @Test
    void testLogin_WrongPassword() {
        when(userRepository.findByUsernameOrEmail("testuser", "testuser")).thenReturn(Optional.of(validUser));

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpass");

        LoginResponse response = userService.login(request);

        assertFalse(response.isSuccess());
        assertEquals("Sai tài khoản hoặc mật khẩu", response.getMessage());
    }

    @Test
    void testLogin_AccountLocked() {
        validUser.setStatus("Bị khóa");
        when(userRepository.findByUsernameOrEmail("testuser", "testuser")).thenReturn(Optional.of(validUser));

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        LoginResponse response = userService.login(request);

        assertFalse(response.isSuccess());
        assertEquals("Tài khoản đang bị khóa", response.getMessage());
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.findByUsernameOrEmail(validUser.getUsername(), validUser.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        User created = userService.createUser(validUser);

        assertNotNull(created);
        assertEquals("Hoạt động", created.getStatus());
        verify(userRepository, times(1)).save(validUser);
    }

    @Test
    void testCreateUser_UsernameExists() {
        when(userRepository.findByUsernameOrEmail(validUser.getUsername(), validUser.getUsername())).thenReturn(Optional.of(validUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createUser(validUser));
        assertEquals("Tên đăng nhập đã tồn tại!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_ShortPassword() {
        validUser.setPassword("123");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createUser(validUser));
        assertEquals("Mật khẩu phải có tối thiểu 8 ký tự!", exception.getMessage());
    }

    @Test
    void testChangeStatus_AdminCannotBeLocked() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.changeStatus(1L, "Bị khóa"));
        assertEquals("Không được phép khóa Quản trị viên hệ thống!", exception.getMessage());
    }

    @Test
    void testChangeStatus_Success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(validUser));
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        User updated = userService.changeStatus(2L, "Bị khóa");
        
        assertEquals("Bị khóa", updated.getStatus());
        verify(userRepository, times(1)).save(validUser);
    }
}
