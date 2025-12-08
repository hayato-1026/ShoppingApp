package com.example.shopping.dto;

import java.math.BigDecimal;

public class LinePricingResult {

    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal baseAmount;
    private BigDecimal appliedDiscountRate;
    private BigDecimal discountAmount;
    private BigDecimal amountAfterDiscount;
    private BigDecimal appliedTaxRate;
    private BigDecimal taxAmount;
    private BigDecimal finalAmount;

    // getters / setters

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }

    public BigDecimal getAppliedDiscountRate() { return appliedDiscountRate; }
    public void setAppliedDiscountRate(BigDecimal appliedDiscountRate) { this.appliedDiscountRate = appliedDiscountRate; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getAmountAfterDiscount() { return amountAfterDiscount; }
    public void setAmountAfterDiscount(BigDecimal amountAfterDiscount) { this.amountAfterDiscount = amountAfterDiscount; }

    public BigDecimal getAppliedTaxRate() { return appliedTaxRate; }
    public void setAppliedTaxRate(BigDecimal appliedTaxRate) { this.appliedTaxRate = appliedTaxRate; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
}