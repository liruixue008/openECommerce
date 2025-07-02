package com.openecommerce.user.application;

import com.openecommerce.shared.domain.Money;
import com.openecommerce.user.application.dto.CreateUserRequest;
import com.openecommerce.user.application.dto.UserRechargeRequest;
import com.openecommerce.user.application.dto.UserResponse;
import com.openecommerce.user.domain.User;
import com.openecommerce.user.domain.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserApplicationService 单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class UserApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserApplicationService userApplicationService;

    private CreateUserRequest createUserRequest;
    private UserRechargeRequest userRechargeRequest;
    private User mockUser;

    @Before
    public void setUp() {
        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testuser");
        createUserRequest.setEmail("test@example.com");
        createUserRequest.setCurrency("CNY");

        userRechargeRequest = new UserRechargeRequest();
        userRechargeRequest.setAmount(new BigDecimal("100.00"));
        userRechargeRequest.setCurrency("CNY");

        mockUser = new User("testuser", "test@example.com", "CNY");
    }

    @Test
    public void testCreateUser_Success() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        UserResponse result = userApplicationService.createUser(createUserRequest);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUser_UsernameExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When
        userApplicationService.createUser(createUserRequest);

        // Then - exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUser_EmailExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When
        userApplicationService.createUser(createUserRequest);

        // Then - exception expected
    }

    @Test
    public void testRechargeUser_Success() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        UserResponse result = userApplicationService.rechargeUser(1L, userRechargeRequest);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRechargeUser_UserNotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        userApplicationService.rechargeUser(1L, userRechargeRequest);

        // Then - exception expected
    }

    @Test
    public void testGetUserById_Success() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        // When
        Optional<UserResponse> result = userApplicationService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    public void testGetUserById_NotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<UserResponse> result = userApplicationService.getUserById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(1L);
    }

    @Test
    public void testGetUserByUsername_Success() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        // When
        Optional<UserResponse> result = userApplicationService.getUserByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    public void testGetUserByUsername_NotFound() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // When
        Optional<UserResponse> result = userApplicationService.getUserByUsername("testuser");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    public void testGetUserEntityById_Success() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        // When
        User result = userApplicationService.getUserEntityById(1L);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserEntityById_NotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        userApplicationService.getUserEntityById(1L);

        // Then - exception expected
    }

    @Test
    public void testSaveUserEntity_Success() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        User result = userApplicationService.saveUserEntity(mockUser);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(mockUser);
    }
}