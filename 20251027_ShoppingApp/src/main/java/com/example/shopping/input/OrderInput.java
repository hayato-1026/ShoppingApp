package com.example.shopping.input;

import java.io.Serializable;

import com.example.shopping.enumeration.PaymentMethod;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

// シリアライズ可能なクラスとして、注文情報を保持するデータモデル
@SuppressWarnings("serial") // シリアライズに関する警告を抑制
public class OrderInput implements Serializable {
    @NotBlank // 空文字やnullを許可しないバリデーション
    private String name; // 注文者の名前

    @NotBlank // 空文字やnullを許可しないバリデーション
    private String address; // 配送先住所

    @NotBlank // 空文字やnullを許可しないバリデーション
    @Pattern(regexp = "0\\d{1,4}-\\d{1,4}-\\d{4}") // 電話番号の形式を制約（例: 0123-456-7890）
    private String phone; // 電話番号

    @NotBlank // 空文字やnullを許可しないバリデーション
    @Email // 正しいメールアドレス形式かどうかをチェック
    private String emailAddress; // メールアドレス

    @NotNull // nullを許可しないバリデーション
    private PaymentMethod paymentMethod; // 支払い方法

    // 支払い方法を取得するゲッターメソッド
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    // 支払い方法を設定するセッターメソッド
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // 電話番号を取得するゲッターメソッド
    public String getPhone() {
        return phone;
    }

    // 電話番号を設定するセッターメソッド
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // 注文者の名前を取得するゲッターメソッド
    public String getName() {
        return name;
    }

    // 注文者の名前を設定するセッターメソッド
    public void setName(String name) {
        this.name = name;
    }

    // 配送先住所を取得するゲッターメソッド
    public String getAddress() {
        return address;
    }

    // 配送先住所を設定するセッターメソッド
    public void setAddress(String address) {
        this.address = address;
    }

    // メールアドレスを取得するゲッターメソッド
    public String getEmailAddress() {
        return emailAddress;
    }

    // メールアドレスを設定するセッターメソッド
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}