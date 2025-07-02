package com.openecommerce.user.interfaces.rest;

import com.openecommerce.user.application.UserApplicationService;
import com.openecommerce.user.application.dto.CreateUserRequest;
import com.openecommerce.user.application.dto.UserRechargeRequest;
import com.openecommerce.user.application.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * User REST Controller
 * 用户REST控制器
 */
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    
    private final UserApplicationService userApplicationService;
    
    @Autowired
    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserResponse response = userApplicationService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据ID获取用户
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        Optional<UserResponse> user = userApplicationService.getUserById(userId);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据用户名获取用户
     */
    @GetMapping
    public ResponseEntity<UserResponse> getUserByUsername(@RequestParam String username) {
        Optional<UserResponse> user = userApplicationService.getUserByUsername(username);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 用户充值
     */
    @PostMapping("/{userId}/recharge")
    public ResponseEntity<UserResponse> rechargeUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRechargeRequest request) {
        try {
            UserResponse response = userApplicationService.rechargeUser(userId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}