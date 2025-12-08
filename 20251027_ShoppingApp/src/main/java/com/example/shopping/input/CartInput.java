package com.example.shopping.input;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("serial")
public class CartInput implements Serializable {
    private BigDecimal totalAmount;
    private BigDecimal billingAmount;
    private List<CartItemInput> cartItemInputs;

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getBillingAmount() {
        return billingAmount;
    }

    public void setBillingAmount(BigDecimal billingAmount2) {
        this.billingAmount = billingAmount2;
    }

    public List<CartItemInput> getCartItemInputs() {
        return cartItemInputs;
    }

    public void setCartItemInputs(List<CartItemInput> cartItemInputs) {
        this.cartItemInputs = cartItemInputs;
    }
}