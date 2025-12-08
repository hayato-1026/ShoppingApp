package com.example.shopping.repository;

import java.sql.Timestamp;
import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.shopping.entity.Order;

@Repository
public class JdbcOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * INSERT into t_order
     */
    public int insert(Order order) {
        Objects.requireNonNull(order, "order must not be null");
        String sql = "INSERT INTO t_order (id, order_date_time, billing_amount, customer_name, customer_address, customer_phone, customer_email_address, payment_method) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                sql,
                order.getId(),
                Timestamp.valueOf(order.getOrderDateTime()),
                order.getBillingAmount(), // BigDecimal（scale 0）
                order.getCustomerName(),
                order.getCustomerAddress(),
                order.getCustomerPhone(),
                order.getCustomerEmailAddress(),
                order.getPaymentMethod() == null ? null : order.getPaymentMethod().name()
        );
    }
}