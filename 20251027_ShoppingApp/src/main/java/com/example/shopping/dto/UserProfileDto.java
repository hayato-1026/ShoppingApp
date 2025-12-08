package com.example.shopping.dto;

import java.math.BigDecimal;

public class UserProfileDto {
    private final String username;
    private final String role;
    private final int itemsPurchased;
    private final int purchaseCount;
    private final BigDecimal totalSpent;
    private final String totalSpentFormatted;

    public UserProfileDto(String username, String role, int itemsPurchased, int purchaseCount, BigDecimal totalSpent, String totalSpentFormatted) {
        this.username = username;
        this.role = role;
        this.itemsPurchased = itemsPurchased;
        this.purchaseCount = purchaseCount;
        this.totalSpent = totalSpent;
        this.totalSpentFormatted = totalSpentFormatted;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public int getItemsPurchased() {
        return itemsPurchased;
    }

    public int getPurchaseCount() {
        return purchaseCount;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public String getTotalSpentFormatted() {
        return totalSpentFormatted;
    }
}