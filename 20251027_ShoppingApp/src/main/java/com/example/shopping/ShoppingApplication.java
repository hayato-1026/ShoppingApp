package com.example.shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication // Spring Bootアプリケーションとして設定するためのアノテーション
public class ShoppingApplication {
    public static void main(String[] args) {
        // Spring Bootアプリケーションを起動
        SpringApplication.run(ShoppingApplication.class, args);
    }
}