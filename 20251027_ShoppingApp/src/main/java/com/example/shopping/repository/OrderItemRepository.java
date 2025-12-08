package com.example.shopping.repository;

import com.example.shopping.entity.OrderItem;

// OrderItemRepositoryインターフェース
// 注文アイテムのデータベース操作を定義するためのインターフェース
public interface OrderItemRepository {
    // 注文アイテムをデータベースに挿入するメソッド
    void insert(OrderItem orderItem);
}