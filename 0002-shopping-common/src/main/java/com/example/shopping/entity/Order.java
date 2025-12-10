package com.example.shopping.entity;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.shopping.enumeration.PaymentMethod;

@SuppressWarnings("serial")
public class Order implements Serializable {
    private String id;
    private List<OrderItem> orderItems;
    private LocalDateTime orderDateTime;
    private BigDecimal billingAmount;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private String customerEmailAddress;
    private PaymentMethod paymentMethod;
    private BigDecimal pointsUsed = BigDecimal.ZERO;
    private BigDecimal pointsEarned = BigDecimal.ZERO;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public BigDecimal getBillingAmount() {
        return billingAmount;
    }

    public void setBillingAmount(BigDecimal billingAmount) {
        this.billingAmount = billingAmount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmailAddress() {
        return customerEmailAddress;
    }

    public void setCustomerEmailAddress(String customerEmailAddress) {
        this.customerEmailAddress = customerEmailAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getPointsUsed() {
        return pointsUsed == null ? BigDecimal.ZERO : pointsUsed;
    }

    public void setPointsUsed(BigDecimal pointsUsed) {
        this.pointsUsed = pointsUsed == null ? BigDecimal.ZERO : pointsUsed;
    }

    public BigDecimal getPointsEarned() {
        return pointsEarned == null ? BigDecimal.ZERO : pointsEarned;
    }

    public void setPointsEarned(BigDecimal pointsEarned) {
        this.pointsEarned = pointsEarned == null ? BigDecimal.ZERO : pointsEarned;
    }
}
