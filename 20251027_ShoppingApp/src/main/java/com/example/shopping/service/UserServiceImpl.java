package com.example.shopping.service;

import java.math.BigDecimal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.shopping.entity.AppUser;
import com.example.shopping.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(AppUser user) {
        // パスワードを暗号化して登録
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getPointRate() == null || user.getPointRate().compareTo(BigDecimal.ZERO) == 0) {
            user.setPointRate(new BigDecimal("0.01"));
        }
        user.setPoints(BigDecimal.ZERO);
        userRepository.insert(user);
    }

    @Override
    public AppUser findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}