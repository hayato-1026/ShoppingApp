package com.example.shopping.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.shopping.entity.Department;
import com.example.shopping.entity.Product;

class DiscountCalculatorImplTest {

    private DiscountCalculatorImpl calculator;

    @BeforeEach
    void setUp() {
        calculator = new DiscountCalculatorImpl();
    }

    @Test
    void determineDiscountRate_prefersProductRateWhenPresent() {
        Product product = new Product();
        product.setDiscountRate(new BigDecimal("0.10"));
        Department department = new Department();
        department.setDiscountRate(new BigDecimal("0.20"));

        BigDecimal result = calculator.determineDiscountRate(product, department);

        assertEquals(new BigDecimal("0.10"), result);
    }

    @Test
    void determineDiscountRate_fallsBackToDepartmentWhenProductMissing() {
        Product product = new Product();
        Department department = new Department();
        department.setDiscountRate(new BigDecimal("0.05"));

        BigDecimal result = calculator.determineDiscountRate(product, department);

        assertEquals(new BigDecimal("0.05"), result);
    }

    @Test
    void determineDiscountRate_returnsZeroWhenNoneProvided() {
        Product product = new Product();
        Department department = new Department();

        BigDecimal result = calculator.determineDiscountRate(product, department);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculateDiscountAmount_returnsExpectedAmount() {
        BigDecimal result = calculator.calculateDiscountAmount(new BigDecimal("1000"), 2, new BigDecimal("0.10"));

        assertEquals(new BigDecimal("200.000000"), result);
    }
}
