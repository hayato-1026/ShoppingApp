package com.example.shopping.entity;


import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
public class OrderItem implements Serializable {
    private String id;
    private String orderId;
    private String productId;
    private Product product;
    private BigDecimal priceAtOrder;
    private Integer quantity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getPriceAtOrder() {
        return priceAtOrder;
    }

    public void setPriceAtOrder(BigDecimal bigDecimal) {
        this.priceAtOrder = bigDecimal;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
