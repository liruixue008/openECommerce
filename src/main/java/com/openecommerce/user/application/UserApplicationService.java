package com.openecommerce.user.application;

import com.openecommerce.shared.domain.Money;
import com.openecommerce.user.application.dto.CreateUserRequest;
import com.openecommerce.user.application.dto.UserRechargeRequest;
import com.openecommerce.user.application.dto.UserResponse;
import com.openecommerce.user.domain.User;
import com.openecommerce.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * User Application Service
 * 用户应用服务
 */
@Service
@Transactional
public class UserApplicationService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * 创建用户
     */
    public UserResponse createUser(CreateUserRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }
        
        User user = new User(request.getUsername(), request.getEmail(), request.getCurrency());
        User savedUser = userRepository.save(user);
        
        return UserResponse.from(savedUser);
    }
    
    /**
     * 用户充值
     */
    public UserResponse rechargeUser(Long userId, UserRechargeRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        Money rechargeAmount = Money.of(request.getAmount(), request.getCurrency());
        user.recharge(rechargeAmount);
        
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }
    
    /**
     * 根据ID获取用户
     */
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserById(Long userId) {
        return userRepository.findById(userId)
            .map(UserResponse::from);
    }
    
    /**
     * 根据用户名获取用户
     */
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(UserResponse::from);
    }
    
    /**
     * 内部方法：根据ID获取用户实体（用于其他服务调用）
     */
    @Transactional(readOnly = true)
    public User getUserEntityById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
    
    /**
     * 内部方法：保存用户实体（用于其他服务调用）
     */
    public User saveUserEntity(User user) {
        return userRepository.save(user);
    }
}