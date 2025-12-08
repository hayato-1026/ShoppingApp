package com.example.shopping.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartPricingResult {

    private List<LinePricingResult> lineResults = new ArrayList<>();

    private BigDecimal totalBaseAmount = BigDecimal.ZERO;
    private BigDecimal totalDiscountAmount = BigDecimal.ZERO;
    private BigDecimal totalAmountAfterDiscount = BigDecimal.ZERO;
    private BigDecimal totalTaxAmount = BigDecimal.ZERO;
    private BigDecimal billingAmount = BigDecimal.ZERO;

    // 表示用に丸めた base 合計（互換）
    private BigDecimal totalBaseAmountRounded = BigDecimal.ZERO;

    public static CartPricingResult empty() {
        return new CartPricingResult();
    }

    public void addLineResult(LinePricingResult line) {
        if (line != null) {
            lineResults.add(line);
        }
    }

    public void aggregateTotals() {
        totalBaseAmount = BigDecimal.ZERO;
        totalDiscountAmount = BigDecimal.ZERO;
        totalAmountAfterDiscount = BigDecimal.ZERO;
        totalTaxAmount = BigDecimal.ZERO;

        for (LinePricingResult lr : lineResults) {
            if (lr.getBaseAmount() != null) totalBaseAmount = totalBaseAmount.add(lr.getBaseAmount());
            if (lr.getDiscountAmount() != null) totalDiscountAmount = totalDiscountAmount.add(lr.getDiscountAmount());
            if (lr.getAmountAfterDiscount() != null) totalAmountAfterDiscount = totalAmountAfterDiscount.add(lr.getAmountAfterDiscount());
            if (lr.getTaxAmount() != null) totalTaxAmount = totalTaxAmount.add(lr.getTaxAmount());
        }
    }

    // getters / setters

    public List<LinePricingResult> getLineResults() { return Collections.unmodifiableList(lineResults); }

    public BigDecimal getTotalBaseAmount() { return totalBaseAmount; }
    public BigDecimal getTotalDiscountAmount() { return totalDiscountAmount; }
    public BigDecimal getTotalAmountAfterDiscount() { return totalAmountAfterDiscount; }
    public BigDecimal getTotalTaxAmount() { return totalTaxAmount; }
    public BigDecimal getBillingAmount() { return billingAmount; }
    public void setBillingAmount(BigDecimal billingAmount) { this.billingAmount = billingAmount; }

    public BigDecimal getTotalBaseAmountRounded() { return totalBaseAmountRounded; }
    public void setTotalBaseAmountRounded(BigDecimal totalBaseAmountRounded) { this.totalBaseAmountRounded = totalBaseAmountRounded; }

    // 互換メソッド名（CartController が参照しやすいように）
    public void setTotalBaseAmount(BigDecimal v) { this.totalBaseAmount = v; }
    public BigDecimal getTotalBaseAmountForDisplay() { return totalBaseAmountRounded; }
}