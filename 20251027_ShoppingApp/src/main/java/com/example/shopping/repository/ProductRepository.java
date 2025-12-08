package com.example.shopping.repository;

import java.util.List;

import com.example.shopping.entity.Product;

// ProductRepositoryインターフェース
// 商品のデータベース操作を定義するためのインターフェース
public interface ProductRepository {
    // 商品IDを指定して1件の商品を取得するメソッド
    Product selectById(String id);

    // 全ての商品を取得するメソッド
    List<Product> selectAll();

    // 商品情報を更新するメソッド（成功時はtrue、失敗時はfalseを返す）
    boolean update(Product product);

    // ページを取得するメソッド
    List<Product> selectPage(int page, int size); // page: 1始まり
    int countAll();

    List<Product> findByIds(List<String> ids);
}