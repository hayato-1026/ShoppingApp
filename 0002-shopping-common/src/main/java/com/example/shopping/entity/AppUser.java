package com.example.shopping.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class AppUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String password;
    private String role;
    private int itemsPurchased = 0;
    private int purchaseCount = 0;
    private BigDecimal totalSpent = BigDecimal.ZERO;

    public String getId() {
    	return id;
    }

    public void setId(String id) {
    	this.id = id;
    }

    public String getUsername() {
    	return username;
    }

    public void setUsername(String username) {
    	this.username = username;
    }

    public String getPassword() {
    	return password;
    }

    public void setPassword(String password) {
    	this.password = password;
    }

    public String getRole() {
    	return role;
    }

    public void setRole(String role) {
    	this.role = role;
    }

    public int getItemsPurchased() {
    	return itemsPurchased;
    }

    public void setItemsPurchased(int i) {
    	this.itemsPurchased = i;
    }

    public int getPurchaseCount() {
    	return purchaseCount;
    }

    public void setPurchaseCount(int purchaseCount) {
    	this.purchaseCount = purchaseCount;
    }

    public BigDecimal getTotalSpent() {
    	// 念のためnullチェックして安全に返す
    	return totalSpent == null ? BigDecimal.ZERO : totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
    	// nullが渡された場合は BigDecimal.ZERO に置き換える
    	this.totalSpent = totalSpent == null ? BigDecimal.ZERO : totalSpent;
    }
}