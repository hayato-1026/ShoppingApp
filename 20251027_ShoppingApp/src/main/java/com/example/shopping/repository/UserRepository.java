package com.example.shopping.repository;

import java.math.BigDecimal;

import com.example.shopping.entity.AppUser;

public interface UserRepository {
    void insert(AppUser user);
    AppUser findByUsername(String username);

    int updateAggregatesById(String id, int addItems, int addPurchaseCount, BigDecimal orderTotal);
}