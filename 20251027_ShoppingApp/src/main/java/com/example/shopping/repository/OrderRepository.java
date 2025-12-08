package com.example.shopping.repository;

import com.example.shopping.entity.Order;

// OrderRepositoryインターフェース
// 注文のデータベース操作を定義するためのインターフェース
public interface OrderRepository {
    // 注文をデータベースに挿入するメソッド
    void insert(Order order);
}