package com.openecommerce.user.domain;

import java.util.Optional;

/**
 * User Repository Interface
 * 用户仓储接口
 */
public interface UserRepository {
    
    /**
     * 保存用户
     */
    User save(User user);
    
    /**
     * 根据ID查找用户
     */
    Optional<User> findById(Long id);
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
}