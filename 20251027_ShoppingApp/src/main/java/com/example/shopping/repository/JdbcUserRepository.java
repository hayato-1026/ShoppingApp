package com.example.shopping.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.example.shopping.entity.AppUser;

@Repository
public class JdbcUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AppUser> userRowMapper = (rs, rowNum) -> {
        AppUser user = new AppUser();
        user.setId(rs.getString("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setItemsPurchased(rs.getInt("items_purchased"));
        user.setPurchaseCount(rs.getInt("purchase_count"));
        user.setTotalSpent(rs.getBigDecimal("total_spent"));
        return user;
    };

    @Override
    public void insert(AppUser user) {
        // idが未設定ならUUIDを自動生成
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString());
        }

        jdbcTemplate.update(
            "INSERT INTO t_user (id, username, password, role, items_purchased, purchase_count, total_spent) VALUES (?, ?, ?, ?, ?, ?, ?)",
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getRole(),
            user.getItemsPurchased(),
            user.getPurchaseCount(),
            user.getTotalSpent()
        );
    }

    @Override
    public AppUser findByUsername(String username) {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM t_user WHERE username = ?",
            userRowMapper,
            username
        );
    }

    @Override
    public int updateAggregatesById(String id, int addItems, int addPurchaseCount, BigDecimal addAmount) {
        Assert.hasText(id, "id must not be empty");
        // SQL: items_purchased と purchase_count は整数、total_spent は数値（BigDecimal）
        String sql = "UPDATE t_user " +
                     "SET items_purchased = items_purchased + ?, " +
                     "    purchase_count = purchase_count + ?, " +
                     "    total_spent = COALESCE(total_spent, 0) + ? " +
                     "WHERE id = ?";
        // JdbcTemplate#update は更新件数を返す
        return jdbcTemplate.update(sql, addItems, addPurchaseCount, addAmount, id);
    }
}