package com.example.shopping.repository;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.shopping.entity.Product;

@Repository
public class JdbcProductRepository implements ProductRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String BASE_SELECT = "SELECT * FROM t_product";

    public JdbcProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Product selectById(String id) {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM t_product WHERE id=?",
            new DataClassRowMapper<>(Product.class),
            id
        );
    }

    @Override
    public List<Product> selectAll() {
        return jdbcTemplate.query(
            "SELECT * FROM t_product",
            new DataClassRowMapper<>(Product.class)
        );
    }

    @Override
    public boolean update(Product product) {
        int count = jdbcTemplate.update(
            "UPDATE t_product SET name=?, price=?, stock=? WHERE id=?",
            product.getName(),
            product.getPrice(),
            product.getStock(),
            product.getId()
        );
        return count > 0;
    }

    /**
     * 指定商品の現在在庫数を取得（存在しない場合は null を返す）
     */
    public Integer selectStockById(String productId) {
        return jdbcTemplate.queryForObject(
            "SELECT stock FROM t_product WHERE id = ?",
            Integer.class,
            productId
        );
    }

    /**
     * 在庫が十分にある場合に在庫を減らす（原子的に実行されることを利用）。
     * 更新件数を返す（1以上：成功、0：在庫不足または該当商品なし）。
     *
     * SQL: UPDATE t_product SET stock = stock - ? WHERE id = ? AND stock >= ?
     */
    public int decreaseStockIfAvailable(String productId, int quantity) {
        String sql = "UPDATE t_product SET stock = stock - ? WHERE id = ? AND stock >= ?";
        return jdbcTemplate.update(sql, quantity, productId, quantity);
    }

    /**
     * productId のリストを一括取得する。
     * 空のリストが来た場合は空リストを返す。
     */
    public List<Product> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        // sanitize / ensure no nulls
        List<String> safeIds = new ArrayList<>();
        for (String id : ids) {
            if (id != null) safeIds.add(id);
        }
        if (safeIds.isEmpty()) return Collections.emptyList();

        StringBuilder sb = new StringBuilder(BASE_SELECT);
        sb.append(" WHERE id IN (");
        String placeholders = String.join(",", Collections.nCopies(safeIds.size(), "?"));
        sb.append(placeholders);
        sb.append(")");

        return jdbcTemplate.query(sb.toString(), new DataClassRowMapper<>(Product.class), safeIds.toArray());
    }

    // ページング取得（pageは1始まり）
    public List<Product> selectPage(int page, int size) {
        int safePage = Math.max(page, 1);
        int offset = (safePage - 1) * size;
        String sql = BASE_SELECT + " ORDER BY id LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new DataClassRowMapper<>(Product.class), size, offset);
    }

    // 総件数取得
    public int countAll() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_product", Integer.class);
        return count != null ? count : 0;
    }
}