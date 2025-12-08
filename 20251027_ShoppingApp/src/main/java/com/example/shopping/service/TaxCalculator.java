package com.example.shopping.service;

import java.math.BigDecimal;

import com.example.shopping.entity.Department;
import com.example.shopping.entity.Product;

public interface TaxCalculator {

    /**
     * 商品・部門から適用すべき税率を返す（例: 0.1000 = 10%）。
     * NULL は返さず、必ず BigDecimal を返す（不足時は BigDecimal.ZERO）。
     */
    BigDecimal determineTaxRate(Product product, Department department);

    /**
     * 割引適用後金額に対する税額を計算して返す（内部高精度で計算）。
     */
    BigDecimal calculateTaxAmount(BigDecimal amountAfterDiscount, BigDecimal taxRate);
}