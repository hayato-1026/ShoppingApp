package com.example.shopping.entity;


import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
public class Product implements Serializable {
    private String id;
    private String name;
    private Integer price;
    private Integer stock;
    private Integer departmentCode;
    private BigDecimal taxRate;
    private BigDecimal discountRate;
    private BigDecimal pointMag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getDepartmentCode() {
    	return departmentCode;
    }

    public void setDepartmentCode(Integer departmentCode) {
    	this.departmentCode = departmentCode;
    }

    public BigDecimal getTaxRate() {
    	return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
    	this.taxRate = taxRate;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getPointMag() {
        return pointMag == null ? BigDecimal.ZERO : pointMag;
    }

    public void setPointMag(BigDecimal pointMag) {
        this.pointMag = pointMag == null ? BigDecimal.ZERO : pointMag;
    }

}
