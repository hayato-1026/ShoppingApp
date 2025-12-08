package com.example.shopping.service;

import java.math.BigDecimal;

import com.example.shopping.entity.Department;
import com.example.shopping.entity.Product;

public interface DiscountCalculator {
    /**
     * 商品・部門から適用する割引率を返す（例: 0.1000 = 10%）。
     * NULL を返さず BigDecimal を返す（不足時は BigDecimal.ZERO）。
     */
    BigDecimal determineDiscountRate(Product product, Department department);

    /**
     * 単価・数量・割引率から割引額を計算して返す（内部高精度）。
     */
    BigDecimal calculateDiscountAmount(BigDecimal unitPrice, int quantity, BigDecimal discountRate);
}