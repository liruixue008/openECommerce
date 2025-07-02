package com.openecommerce.user.infrastructure;

import com.openecommerce.user.domain.User;
import com.openecommerce.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * User Repository Implementation
 * 用户仓储实现
 */
@Component
public class UserRepositoryImpl implements UserRepository {
    
    private final UserJpaRepository userJpaRepository;
    
    @Autowired
    public UserRepositoryImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }
    
    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}