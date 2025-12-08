package com.example.shopping.service;

import java.util.List;

import com.example.shopping.entity.Product;

// 商品カタログに関連する操作を定義するためのサービスインターフェース
public interface CatalogService {

    /**
     * 商品の全リストを取得するメソッド。
     *
     * @return 商品のリスト
     */
    List<Product> findAll();

    /**
     * 商品IDを使用して特定の商品を取得するメソッド。
     *
     * @param id 商品の一意の識別ID
     * @return 商品オブジェクト（該当商品がない場合はnullを返す可能性あり）
     */
    Product findById(String id);
    List<Product> findPage(int page, int size);
    int countAll();
}