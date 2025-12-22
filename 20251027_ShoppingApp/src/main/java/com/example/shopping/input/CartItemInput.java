package com.example.shopping.input;

import java.io.Serializable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// シリアライズ可能なクラスとして、カート内の各商品情報を保持するデータモデル
@SuppressWarnings("serial") // シリアライズに関する警告を抑制
public class CartItemInput implements Serializable {
    private String id; // カートアイテムの一意のID

    @NotBlank(message = "商品IDが不正です")
    private String productId; // 商品のID

    private String productName; // 商品名

    private Integer productPrice; // 商品の価格

    @NotNull(message = "数量を入力してください")
    @Min(value = 1, message = "数量は1以上で入力してください")
    @Max(value = 99, message = "数量は99以下で入力してください")
    private Integer quantity; // 商品の数量

    // 商品名を取得するゲッターメソッド
    public String getProductName() {
        return productName;
    }

    // 商品名を設定するセッターメソッド
    public void setProductName(String productName) {
        this.productName = productName;
    }

    // 商品の価格を取得するゲッターメソッド
    public Integer getProductPrice() {
        return productPrice;
    }

    // 商品の価格を設定するセッターメソッド
    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }

    // 商品IDを取得するゲッターメソッド
    public String getProductId() {
        return productId;
    }

    // 商品IDを設定するセッターメソッド
    public void setProductId(String productId) {
        this.productId = productId;
    }

    // カートアイテムのIDを取得するゲッターメソッド
    public String getId() {
        return id;
    }

    // カートアイテムのIDを設定するセッターメソッド
    public void setId(String id) {
        this.id = id;
    }

    // 商品数量を取得するゲッターメソッド
    public Integer getQuantity() {
        return quantity;
    }

    // 商品数量を設定するセッターメソッド
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}