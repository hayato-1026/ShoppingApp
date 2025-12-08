package com.example.shopping.controller;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.UUID;
import com.example.shopping.input.CartInput;
import com.example.shopping.input.CartItemInput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.shopping.entity.Product;
import com.example.shopping.service.CatalogService;
import com.example.shopping.service.OrderService;
import com.example.shopping.service.PricingService;
import com.example.shopping.dto.CartPricingResult;

// ショッピングカートに関連する操作を管理するコントローラー
@Controller
@RequestMapping("/cart") // /cart以下のURLに対応
public class CartController {
	private final CatalogService catalogService;
    private final OrderSession orderSession;
    private final PricingService pricingService; // 追加

    public CartController(CatalogService catalogService, OrderService orderService, OrderSession orderSession, PricingService pricingService) {
        this.catalogService = catalogService;
        this.orderSession = orderSession;
        this.pricingService = pricingService;
    }

    @PostMapping("/add-item") // POSTリクエストでアイテムをカートに追加
    public String addToCart(
        @Validated CartItemInput cartItem, BindingResult bindingResult, Model model) {
        // 商品IDで商品情報を取得
        Product product = catalogService.findById(cartItem.getProductId());

        // 入力チェックでエラーがあれば商品詳細ページに戻る
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", product);
            return "catalog/productDetails";
        }

        // カートアイテム情報を設定
        cartItem.setId(UUID.randomUUID().toString()); // ランダムなIDを生成
        cartItem.setProductName(product.getName());  // 商品名を設定
        cartItem.setProductPrice(product.getPrice());// 商品価格を設定

        // セッションからカート情報を取得（なければ新規作成）
        CartInput cartInput = orderSession.getCartInput();
        if (cartInput == null) {
            cartInput = new CartInput();
            cartInput.setCartItemInputs(new ArrayList<>());
            orderSession.setCartInput(cartInput);
        }

        // カートにアイテムを追加
        cartInput.getCartItemInputs().add(cartItem);

        // 合計金額と請求金額を計算
        calculatedAmounts(cartInput);

        // カート情報をモデルに追加
        model.addAttribute("cartInput", cartInput);

        // カートアイテム一覧ページを表示
        return "cart/cartItems";
    }

    @GetMapping("/display-cart") // GETリクエストでカートの内容を表示
    public String displayCart(Model model) {
        // セッション内のカート情報をモデルに追加
        model.addAttribute("cartInput", orderSession.getCartInput());
        return "cart/cartItems"; // カートアイテム一覧ページを表示
    }

    @PostMapping("/remove-item") // POSTリクエストでカートからアイテムを削除
    public String removeFromCart(String cartItemId, Model model) {
        // セッションからカート情報を取得
        CartInput cartInput = orderSession.getCartInput();
        CartItemInput target = null;

        // 削除対象アイテムを検索
        for (CartItemInput item : cartInput.getCartItemInputs()) {
            if (cartItemId.equals(item.getId())) {
                target = item;
                break;
            }
        }

        // アイテムをカートから削除
        cartInput.getCartItemInputs().remove(target);

        // 合計金額と請求金額を再計算
        calculatedAmounts(cartInput);

        // 更新されたカート情報をモデルに追加
        model.addAttribute("cartInput", cartInput);
        return "cart/cartItems"; // カートアイテム一覧ページを表示
    }

    // カート内の合計金額と請求金額を計算するメソッド
    private void calculatedAmounts(CartInput cartInput) {
        CartPricingResult pr = pricingService.priceCart(cartInput.getCartItemInputs());

        // 割引後（税抜）を合計として表示する（scale=0, 銀行丸め）
        BigDecimal totalAfterDiscount = pr.getTotalAmountAfterDiscount() == null
                ? BigDecimal.ZERO
                : pr.getTotalAmountAfterDiscount().setScale(0, RoundingMode.HALF_EVEN);
        cartInput.setTotalAmount(totalAfterDiscount);

        // 請求金額（既に DefaultPricingService で丸め済みであればそのままセット）
        cartInput.setBillingAmount(pr.getBillingAmount());
    }
}