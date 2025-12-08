package com.example.shopping.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.shopping.entity.OrderItem;

@Repository
public class JdbcOrderItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcOrderItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insert(OrderItem item) {
        String sql = "INSERT INTO t_order_item (id, order_id, product_id, price_at_order, quantity) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                item.getId(),
                item.getOrderId(),
                item.getProductId(),
                item.getPriceAtOrder(),
                item.getQuantity());
    }
}