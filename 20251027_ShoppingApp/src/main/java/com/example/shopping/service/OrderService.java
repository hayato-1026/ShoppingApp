package com.example.shopping.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.shopping.entity.Order;
import com.example.shopping.input.CartInput;
import com.example.shopping.input.CartItemInput;
import com.example.shopping.input.OrderInput;

// 注文に関連するビジネスロジックを定義するためのサービスインターフェース
public interface OrderService {

    /**
     * カート内の商品合計金額を計算するメソッド。
     * 各商品の価格と数量を元に合計金額を計算します。
     *
     * @param cartItems カート内の商品リスト
     * @return 合計金額（税抜）
     */
    BigDecimal calculateTotalAmount(List<CartItemInput> cartItems);

    /**
     * 税額を計算するメソッド。
     * 商品価格に基づき税額を計算します。
     *
     * @param totalAmount 商品の価格（税抜）
     * @return 税額
     */
    BigDecimal calculateTax(BigDecimal totalAmount);

    /**
     * 注文を確定するメソッド。
     * ユーザーの注文情報とカート情報を基に新しい注文を作成します。
     *
     * @param orderInput 注文情報（ユーザー名、住所、支払い方法など）
     * @param cartInput カート情報（カート内の商品リストなど）
     * @return 作成された注文オブジェクト
     */
    Order placeOrder(OrderInput orderInput, CartInput cartInput);
}