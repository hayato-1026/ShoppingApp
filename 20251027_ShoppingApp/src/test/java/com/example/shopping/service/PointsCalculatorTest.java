package com.example.shopping.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.shopping.entity.Product;
import com.example.shopping.input.CartItemInput;

class PointsCalculatorTest {

    private PointsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PointsCalculator();
    }

    @Test
    void calculateRawEarnedPoints_shouldApplyPointMagnificationAndRate() {
        CartItemInput item = new CartItemInput();
        item.setProductId("p1");
        item.setQuantity(2);

        Product product = new Product();
        product.setId("p1");
        product.setPrice(1000);
        product.setPointMag(new BigDecimal("9.00"));

        Map<String, Product> productMap = new HashMap<>();
        productMap.put(product.getId(), product);

        BigDecimal result = calculator.calculateRawEarnedPoints(List.of(item), productMap, new BigDecimal("0.02"));

        assertEquals(new BigDecimal("400"), result);
    }

    @Test
    void adjustForPointUsage_shouldProrateByBillingRatio() {
        BigDecimal adjusted = calculator.adjustForPointUsage(new BigDecimal("20"), new BigDecimal("1100"), new BigDecimal("1000"));

        assertEquals(new BigDecimal("18"), adjusted);
    }
}
