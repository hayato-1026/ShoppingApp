package com.example.shopping.dto;

import java.math.BigDecimal;

public class UserProfileDto {
    private final String username;
    private final String role;
    private final int itemsPurchased;
    private final int purchaseCount;
    private final BigDecimal totalSpent;
    private final String totalSpentFormatted;
    private final BigDecimal points;
    private final BigDecimal pointRate;
    private final String pointRateFormatted;

    public UserProfileDto(String username, String role, int itemsPurchased, int purchaseCount, BigDecimal totalSpent, String totalSpentFormatted,
            BigDecimal points, BigDecimal pointRate, String pointRateFormatted) {
        this.username = username;
        this.role = role;
        this.itemsPurchased = itemsPurchased;
        this.purchaseCount = purchaseCount;
        this.totalSpent = totalSpent;
        this.totalSpentFormatted = totalSpentFormatted;
        this.points = points;
        this.pointRate = pointRate;
        this.pointRateFormatted = pointRateFormatted;
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

    public BigDecimal getPoints() {
        return points;
    }

    public BigDecimal getPointRate() {
        return pointRate;
    }

    public String getPointRateFormatted() {
        return pointRateFormatted;
    }
}