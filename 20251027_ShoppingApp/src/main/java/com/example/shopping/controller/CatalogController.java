package com.example.shopping.controller;

import java.util.List;

import com.example.shopping.input.CartItemInput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.shopping.entity.Product;
import com.example.shopping.service.CatalogService;

@Controller
@RequestMapping("/catalog")
public class CatalogController {
    // カタログサービス（依存注入）
    private final CatalogService catalogService;

    // 1ページあたりの表示件数（定数）
    private static final int PAGE_SIZE = 10;

    // コンストラクタインジェクション
    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * 商品一覧（ページング表示）
     *
     * パラメータ:
     * - page: 表示するページ番号（1始まり）。クエリパラメータ ?page=2 などで指定。
     *
     * モデルにセットする属性:
     * - productList: 当該ページの商品のリスト
     * - currentPage: 現在のページ番号
     * - totalPages: 総ページ数
     * - totalCount: 全商品の総数
     * - pageSize: ページサイズ（PAGE_SIZE）
     */
    @GetMapping("/display-list")
    public String displayList(@RequestParam(value = "page", defaultValue = "1") int page,Model model) {

        // ページ番号下限チェック（1未満なら1に補正）
        if (page < 1) page = 1;

        // 全件数を取得して総ページ数を計算
        int totalCount = catalogService.countAll();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / PAGE_SIZE));

        // ページ番号上限チェック（総ページ数を超えていたら補正）
        if (page > totalPages) page = totalPages;

        // 指定ページのデータを取得
        List<Product> productList = catalogService.findPage(page, PAGE_SIZE);

        // ビュー用モデルに属性を追加
        model.addAttribute("productList", productList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pageSize", PAGE_SIZE);

        return "catalog/productList";
    }

    /**
     * 商品詳細表示
     *
     * パラメータ:
     * - productId: 表示する商品のID（必須）
     *
     * モデルにセットする属性:
     * - product: 詳細表示用の Product エンティティ
     * - cartItemInput: カートへ追加するための入力オブジェクト（初期数量1）
     */
    @GetMapping("/display-details")
    public String displayDetails(@RequestParam String productId, Model model) {
        // 商品を取得
        Product product = catalogService.findById(productId);
        model.addAttribute("product", product);

        // カートに追加するための入力オブジェクトを初期化
        CartItemInput cartItemInput = new CartItemInput();
        cartItemInput.setQuantity(1); // デフォルト数量を1に設定

        // product が null の場合は product.getId() が NPEになる
        if (product != null) {
            cartItemInput.setProductId(product.getId());
        } else {
            cartItemInput.setProductId("");
        }

        model.addAttribute("cartItemInput", cartItemInput);

        return "catalog/productDetails";
    }
}