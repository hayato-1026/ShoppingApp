package com.example.shopping.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.math.RoundingMode;

import com.example.shopping.entity.AppUser;
import com.example.shopping.input.OrderInput;
import com.example.shopping.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.shopping.entity.Order;
import com.example.shopping.enumeration.PaymentMethod;
import com.example.shopping.exception.StockShortageException;
import com.example.shopping.service.OrderService;

// 注文処理を管理するコントローラー
@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final OrderSession orderSession;
    private final UserService userService; // 追加: ユーザ取得用サービス

    public OrderController(OrderService orderService, OrderSession orderSession, UserService userService) {
        this.orderService = orderService;
        this.orderSession = orderSession;
        this.userService = userService;
    }

    @GetMapping("/display-form")
    public String displayForm(Model model, Principal principal) {
        OrderInput orderInput = new OrderInput();
        orderInput.setPaymentMethod(PaymentMethod.BANK);
        model.addAttribute("orderInput", orderInput);
        model.addAttribute("availablePoints", resolveAvailablePoints(principal));
        return "order/orderForm";
    }

    @PostMapping("/validate-input")
    public String validateInput(
        @Validated OrderInput orderInput, BindingResult bindingResult, Model model, Principal principal) {
        BigDecimal availablePoints = resolveAvailablePoints(principal);
        model.addAttribute("availablePoints", availablePoints);
        if (bindingResult.hasErrors()) {
            return "order/orderForm";
        }

        BigDecimal billingAmount = orderSession.getCartInput() == null || orderSession.getCartInput().getBillingAmount() == null
                ? BigDecimal.ZERO
                : orderSession.getCartInput().getBillingAmount();
        BigDecimal requestedUsePoints = BigDecimal.valueOf(orderInput.getUsePoints() == null ? 0 : orderInput.getUsePoints())
                .max(BigDecimal.ZERO);
        BigDecimal allowedMax = availablePoints.min(billingAmount).setScale(0, RoundingMode.HALF_UP);
        if (requestedUsePoints.compareTo(allowedMax) > 0) {
            bindingResult.rejectValue(
                "usePoints",
                "usePoints.exceeds",
                "使用ポイントは「保有ポイント」と「請求額」以下で入力してください。");
            model.addAttribute("availablePoints", availablePoints);
            return "order/orderForm";
        }

        sanitizePointsForConfirmation(orderInput, model);
        orderSession.setOrderInput(orderInput);
        model.addAttribute("cartInput", orderSession.getCartInput());
        return "order/orderConfirmation";
    }

    @PostMapping(value = "/place-order", params = "correct")
    public String correctInput(Model model, Principal principal) {
        model.addAttribute("orderInput", orderSession.getOrderInput());
        model.addAttribute("availablePoints", resolveAvailablePoints(principal));
        return "order/orderForm";
    }

    @PostMapping(value = "/place-order", params = "order")
    public String order(RedirectAttributes redirectAttributes, Principal principal) {
        // 未ログインチェック
        if (principal == null || principal.getName() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "ログインが必要です。ログインしてください。");
            return "redirect:/login";
        }

        // username から AppUser を取得してセッションに格納
        AppUser appUser = userService.findByUsername(principal.getName());
        if (appUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "ユーザ情報が見つかりません。再度ログインしてください。");
            return "redirect:/login";
        }
        orderSession.setAppUser(appUser);

        // 注文確定（OrderService 内でも orderSession を参照します）
        Order order = orderService.placeOrder(orderSession.getOrderInput(), orderSession.getCartInput());

        // 最新のポイント残高を取得
        AppUser updated = userService.findByUsername(principal.getName());

        redirectAttributes.addFlashAttribute("order", order);
        redirectAttributes.addFlashAttribute("usedPoints", order.getPointsUsed());
        redirectAttributes.addFlashAttribute("earnedPoints", order.getPointsEarned());
        redirectAttributes.addFlashAttribute("remainingPoints", updated == null ? BigDecimal.ZERO : updated.getPoints());
        redirectAttributes.addFlashAttribute("payableAmount", order.getBillingAmount());

        // orderService.placeOrder 内でクリアしている場合は二重クリアになりますが問題ありません
        orderSession.clearData();

        return "redirect:/order/display-completion";
    }

    @GetMapping("/display-completion")
    public String displayComplete() {
        return "order/orderCompletion";
    }

    @ExceptionHandler(StockShortageException.class)
    public String displayStockShortagePage() {
        return "order/stockShortage";
    }

    private void sanitizePointsForConfirmation(OrderInput orderInput, Model model) {
        BigDecimal availablePoints = orderSession.getAppUser() != null ? orderSession.getAppUser().getPoints() : BigDecimal.ZERO;
        BigDecimal billingAmount = orderSession.getCartInput() == null || orderSession.getCartInput().getBillingAmount() == null
                ? BigDecimal.ZERO
                : orderSession.getCartInput().getBillingAmount();

        BigDecimal requested = BigDecimal.valueOf(orderInput.getUsePoints() == null ? 0 : orderInput.getUsePoints())
                .max(BigDecimal.ZERO)
                .setScale(0, RoundingMode.HALF_UP);

        BigDecimal payable = billingAmount.subtract(requested).max(BigDecimal.ZERO).setScale(0, RoundingMode.HALF_UP);
        model.addAttribute("usedPoints", requested);
        model.addAttribute("availablePoints", availablePoints);
        model.addAttribute("payableAmount", payable);
    }

    private BigDecimal resolveAvailablePoints(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return BigDecimal.ZERO;
        }
        AppUser appUser = userService.findByUsername(principal.getName());
        if (appUser != null) {
            orderSession.setAppUser(appUser);
            return appUser.getPoints();
        }
        return BigDecimal.ZERO;
    }
}