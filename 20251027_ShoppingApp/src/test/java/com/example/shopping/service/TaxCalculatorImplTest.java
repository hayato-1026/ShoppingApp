package com.example.shopping.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.shopping.entity.Department;
import com.example.shopping.entity.Product;

class TaxCalculatorImplTest {

    private TaxCalculatorImpl calculator;

    @BeforeEach
    void setUp() {
        calculator = new TaxCalculatorImpl();
    }

    @Test
    void determineTaxRate_prefersProductRate() {
        Product product = new Product();
        product.setTaxRate(new BigDecimal("0.08"));
        Department department = new Department();
        department.setTaxRate(new BigDecimal("0.10"));

        BigDecimal result = calculator.determineTaxRate(product, department);

        assertEquals(new BigDecimal("0.08"), result);
    }

    @Test
    void determineTaxRate_usesDepartmentWhenProductMissing() {
        Product product = new Product();
        Department department = new Department();
        department.setTaxRate(new BigDecimal("0.10"));

        BigDecimal result = calculator.determineTaxRate(product, department);

        assertEquals(new BigDecimal("0.10"), result);
    }

    @Test
    void determineTaxRate_returnsZeroWhenNoneProvided() {
        Product product = new Product();
        Department department = new Department();

        BigDecimal result = calculator.determineTaxRate(product, department);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculateTaxAmount_returnsExpectedAmount() {
        BigDecimal result = calculator.calculateTaxAmount(new BigDecimal("900"), new BigDecimal("0.10"));

        assertEquals(new BigDecimal("90.000000"), result);
    }
}
