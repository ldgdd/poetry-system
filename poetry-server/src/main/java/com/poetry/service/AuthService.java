package com.poetry.service;

import com.poetry.dto.LoginRequest;
import com.poetry.dto.LoginResponse;
import com.poetry.dto.RegisterRequest;
import com.poetry.exception.BusinessException;
import com.poetry.model.User;
import com.poetry.repository.UserRepository;
import com.poetry.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private static final int MAX_LOGIN_FAIL_COUNT = 5;
    private static final int LOCK_MINUTES = 30;

    /**
     * 用户注册
     */
    @Transactional
    public void register(RegisterRequest req) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BusinessException("用户名已被注册");
        }
        // 检查邮箱是否已存在
        if (req.getEmail() != null && !req.getEmail().isEmpty()
                && userRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getUsername());  // 默认昵称为用户名
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        userRepository.save(user);
    }

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest req) {
        // 支持用户名或邮箱登录
        User user = userRepository.findByUsernameOrEmail(req.getAccount(), req.getAccount())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        // 检查账号是否被锁定
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被封禁");
        }
        if (user.getLockTime() != null && user.getLockTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("账号已被锁定，请 " + LOCK_MINUTES + " 分钟后重试");
        }

        // 验证密码
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            // 记录失败次数（防空）
            int failCount = user.getLoginFailCount() != null ? user.getLoginFailCount() : 0;
            user.setLoginFailCount(failCount + 1);
            if (user.getLoginFailCount() >= MAX_LOGIN_FAIL_COUNT) {
                user.setLockTime(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
                user.setLoginFailCount(0);
                userRepository.save(user);
                throw new BusinessException("密码错误次数过多，账号已锁定 " + LOCK_MINUTES + " 分钟");
            }
            userRepository.save(user);
            throw new BusinessException("用户名或密码错误");
        }

        // 登录成功，重置失败计数
        user.setLoginFailCount(0);
        user.setLockTime(null);
        userRepository.save(user);

        // 记住密码时 Token 有效期延长
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return LoginResponse.of(token, user);
    }
}
