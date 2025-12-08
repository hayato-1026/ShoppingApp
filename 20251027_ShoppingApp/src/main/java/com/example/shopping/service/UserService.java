package com.example.shopping.service;

import com.example.shopping.entity.AppUser;

public interface UserService {
    void registerUser(AppUser user);
    AppUser findByUsername(String username);
}