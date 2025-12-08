package com.example.shopping.service;

import java.util.List;

import com.example.shopping.dto.CartPricingResult;
import com.example.shopping.input.CartItemInput;

public interface PricingService {
    /**
     * カート内の行ごと・合計の価格情報を計算する。
     */
    CartPricingResult priceCart(List<CartItemInput> cartItems);
}