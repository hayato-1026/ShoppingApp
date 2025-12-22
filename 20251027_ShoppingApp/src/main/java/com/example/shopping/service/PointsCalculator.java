package com.example.shopping.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.shopping.entity.Product;
import com.example.shopping.input.CartItemInput;

@Component
public class PointsCalculator {

    private static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    public BigDecimal calculateRawEarnedPoints(List<CartItemInput> cartItems, Map<String, Product> productMap, BigDecimal pointRate) {
        if (cartItems == null || productMap == null) {
            return BigDecimal.ZERO.setScale(0, MONEY_ROUNDING);
        }
        BigDecimal rate = pointRate == null ? BigDecimal.ZERO : pointRate;
        BigDecimal total = BigDecimal.ZERO;

        for (CartItemInput ci : cartItems) {
            if (ci == null) continue;
            Product p = productMap.get(ci.getProductId());
            if (p == null) continue;

            int qty = ci.getQuantity() == null ? 0 : ci.getQuantity();
            if (qty <= 0) continue;

            BigDecimal baseAmount = BigDecimal.valueOf(p.getPrice()).multiply(BigDecimal.valueOf(qty));
            BigDecimal basePoints = baseAmount.multiply(rate);
            BigDecimal multiplier = BigDecimal.ONE.add(p.getPointMag());
            BigDecimal linePoints = basePoints.multiply(multiplier);
            total = total.add(linePoints);
        }

        return total.setScale(0, MONEY_ROUNDING);
    }

    public BigDecimal adjustForPointUsage(BigDecimal rawEarnedPoints, BigDecimal billingAmount, BigDecimal billingAfterPoints) {
        if (rawEarnedPoints == null) {
            rawEarnedPoints = BigDecimal.ZERO;
        }
        if (billingAmount == null || billingAfterPoints == null || billingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return rawEarnedPoints.setScale(0, MONEY_ROUNDING);
        }

        BigDecimal ratio = billingAfterPoints.divide(billingAmount, 6, RoundingMode.HALF_UP);
        BigDecimal adjusted = rawEarnedPoints.multiply(ratio);
        return adjusted.setScale(0, MONEY_ROUNDING);
    }
}
