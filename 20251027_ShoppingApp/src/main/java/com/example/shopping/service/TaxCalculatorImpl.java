package com.example.shopping.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.example.shopping.entity.Department;
import com.example.shopping.entity.Product;

@Service
public class TaxCalculatorImpl implements TaxCalculator {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final int INTERNAL_SCALE = 6;

    @Override
    public BigDecimal determineTaxRate(Product product, Department department) {
        if (product != null && product.getTaxRate() != null) {
            return product.getTaxRate();
        }
        if (department != null && department.getTaxRate() != null) {
            return department.getTaxRate();
        }
        return ZERO;
    }

    @Override
    public BigDecimal calculateTaxAmount(BigDecimal amountAfterDiscount, BigDecimal taxRate) {
        if (amountAfterDiscount == null) {
            amountAfterDiscount = ZERO;
        }
        if (taxRate == null) {
            taxRate = ZERO;
        }
        // 高精度で計算（丸めは最終合計で行う）
        return amountAfterDiscount.multiply(taxRate).setScale(INTERNAL_SCALE, RoundingMode.HALF_EVEN);
    }
}