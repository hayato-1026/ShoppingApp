package com.example.shopping.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.example.shopping.controller.OrderSession;
import com.example.shopping.entity.AppUser;
import com.example.shopping.entity.Order;
import com.example.shopping.entity.OrderItem;
import com.example.shopping.entity.Product;
import com.example.shopping.input.CartInput;
import com.example.shopping.input.CartItemInput;
import com.example.shopping.input.OrderInput;
import com.example.shopping.enumeration.PaymentMethod;
import com.example.shopping.dto.CartPricingResult;
import com.example.shopping.dto.LinePricingResult;
import com.example.shopping.repository.JdbcDepartmentRepository;
import com.example.shopping.repository.JdbcOrderItemRepository;
import com.example.shopping.repository.JdbcOrderRepository;
import com.example.shopping.repository.JdbcProductRepository;
import com.example.shopping.repository.JdbcUserRepository;

@Service
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // (既存参照不要だが残して可)
    private static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    private final JdbcOrderRepository orderRepository;
    private final JdbcOrderItemRepository orderItemRepository;
    private final JdbcProductRepository productRepository;
    private final JdbcDepartmentRepository departmentRepository; // 追加
    private final OrderSession orderSession;
    private final PricingService pricingService; // 追加
    private final JdbcUserRepository userRepository;

    public OrderServiceImpl(JdbcOrderRepository orderRepository,JdbcOrderItemRepository orderItemRepository,JdbcUserRepository userRepository,JdbcProductRepository productRepository,JdbcDepartmentRepository departmentRepository,OrderSession orderSession,PricingService pricingService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.departmentRepository = departmentRepository;
        this.orderSession = orderSession;
        this.pricingService = pricingService;
        this.userRepository = userRepository;
    }

    @Override
    public BigDecimal calculateTotalAmount(List<CartItemInput> cartItems) {
        if (CollectionUtils.isEmpty(cartItems)) {
            return BigDecimal.ZERO.setScale(0, MONEY_ROUNDING);
        }
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemInput ci : cartItems) {
            if (ci == null || ci.getProductPrice() == null || ci.getQuantity() == null) {
                continue;
            }
            BigDecimal price = BigDecimal.valueOf(ci.getProductPrice());
            BigDecimal qty = BigDecimal.valueOf(ci.getQuantity());
            total = total.add(price.multiply(qty));
        }
        // 円単位（scale 0）
        return total.setScale(0, MONEY_ROUNDING);
    }

    @Override
    public BigDecimal calculateTax(BigDecimal totalAmount) {
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
        // 課税後金額 = totalAmount * (1 + TAX_RATE)
        BigDecimal taxed = totalAmount.multiply(BigDecimal.ONE.add(TAX_RATE));
        // DECIMAL(10,0) に合わせる（円単位）
        return taxed.setScale(0, MONEY_ROUNDING);
    }

    @Override
    @Transactional
    public Order placeOrder(OrderInput orderInput, CartInput cartInput) {
        if (cartInput == null || CollectionUtils.isEmpty(cartInput.getCartItemInputs())) {
            throw new IllegalArgumentException("カートが空です。");
        }

        AppUser sessionUser = orderSession.getAppUser();
        if (sessionUser == null || sessionUser.getId() == null) {
            throw new IllegalStateException("ユーザがログインしていません。");
        }

        AppUser appUser = userRepository.findById(sessionUser.getId());
        if (appUser == null) {
            throw new IllegalStateException("ユーザ情報が見つかりません。");
        }

        // PricingService で再計算して billingAmount を取得（かつ Line ごとの金額取得）
        CartPricingResult pr = pricingService.priceCart(cartInput.getCartItemInputs());
        BigDecimal billingAmount = pr.getBillingAmount();
        if (billingAmount == null) {
            billingAmount = BigDecimal.ZERO.setScale(0, MONEY_ROUNDING);
        }
        BigDecimal availablePoints = appUser.getPoints();
        BigDecimal requestedUsePoints = BigDecimal.valueOf(orderInput.getUsePoints() == null ? 0 : orderInput.getUsePoints())
                .max(BigDecimal.ZERO);
        BigDecimal usePoints = requestedUsePoints.min(availablePoints).min(billingAmount).setScale(0, MONEY_ROUNDING);
        BigDecimal billingAfterPoints = billingAmount.subtract(usePoints).max(BigDecimal.ZERO).setScale(0, MONEY_ROUNDING);

        // Order 作成
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setOrderDateTime(LocalDateTime.now());
        order.setBillingAmount(billingAfterPoints);
        order.setPointsUsed(usePoints);

        // 顧客情報
        order.setCustomerName(orderInput.getName());
        order.setCustomerAddress(orderInput.getAddress());
        order.setCustomerPhone(orderInput.getPhone());
        order.setCustomerEmailAddress(orderInput.getEmailAddress());

        // 支払い方法処理（既存ロジック）
        try {
            if (orderInput.getPaymentMethod() != null) {
                String pm = orderInput.getPaymentMethod().toString();
                order.setPaymentMethod(PaymentMethod.valueOf(pm));
            }
        } catch (Exception e) {
            order.setPaymentMethod(null);
        }

        // 注文ヘッダ挿入
        orderRepository.insert(order);

        // 在庫チェックのため、カート内 productId を一括取得してマップ化
        List<String> productIds = cartInput.getCartItemInputs().stream()
                .map(ci -> ci.getProductId())
                .distinct()
                .collect(Collectors.toList());
        List<Product> products = productRepository.findByIds(productIds);
        Map<String, Product> productMap = new HashMap<>();
        if (products != null) {
            for (Product p : products) {
                productMap.put(p.getId(), p);
            }
        }

        // 注文明細の挿入と在庫減算
        int totalItems = 0;
        for (CartItemInput ci : cartInput.getCartItemInputs()) {
            if (ci == null) continue;
            Product p = productMap.get(ci.getProductId());
            if (p == null) {
                // 商品が見つからない場合はトランザクションを中断して例外
                throw new IllegalStateException("注文処理中に商品が見つかりません: " + ci.getProductId());
            }

            Integer qtyValue = ci.getQuantity();
            int qty = qtyValue == null ? 0 : qtyValue;
            if (qty <= 0) {
                continue; // 数量ゼロならスキップ（あるいは例外にする判断でも可）
            }

            totalItems += qty;

            int updated = productRepository.decreaseStockIfAvailable(p.getId(), qty);
            if (updated <= 0) {
                // 在庫不足 -> ロールバック
                throw new IllegalStateException("在庫不足: productId=" + p.getId());
            }

            // 注文明細作成（既存 OrderItem のフィールドに合わせて調整してください）
            OrderItem oi = new OrderItem();
            oi.setId(UUID.randomUUID().toString());
            oi.setOrderId(order.getId());
            oi.setProductId(p.getId());
            // priceAtOrder は税抜きの単価（既存互換）
            oi.setPriceAtOrder(BigDecimal.valueOf(p.getPrice()));
            oi.setQuantity(qty);
            // 注文明細を保存
            orderItemRepository.insert(oi);
        }

        BigDecimal rawEarnedPoints = calculateEarnedPoints(cartInput.getCartItemInputs(), productMap, appUser.getPointRate());
        BigDecimal earnedPoints = adjustEarnedPointsForPointUsage(rawEarnedPoints, billingAmount, billingAfterPoints);
        order.setPointsEarned(earnedPoints);

        // department_sales 集計（PricingService の Line 結果を利用）
        // 前提: pr.getLineResults() は cartInput.getCartItemInputs() と同一順序で対応している
        List<LinePricingResult> lineResults = pr.getLineResults();
        Map<Integer, BigDecimal> deptSums = new HashMap<>();
        List<CartItemInput> items = cartInput.getCartItemInputs();

        for (int i = 0; i < items.size(); i++) {
            CartItemInput ci = items.get(i);
            if (ci == null) continue;
            Product p = productMap.get(ci.getProductId());
            if (p == null) continue; // 既にチェック済みだが念のため
            Integer deptCode = p.getDepartmentCode();
            if (deptCode == null) continue;

            BigDecimal lineFinalAmount = null;
            if (lineResults != null && i < lineResults.size()) {
                LinePricingResult lr = lineResults.get(i);
                if (lr != null) {
                    lineFinalAmount = lr.getFinalAmount();
                }
            }
            // フォールバック：もし LinePricingResult が存在しなければ単価*qty（税/割引未反映）を使用
            if (lineFinalAmount == null) {
                int qty = ci.getQuantity() == null ? 0 : ci.getQuantity();
                lineFinalAmount = BigDecimal.valueOf(p.getPrice()).multiply(BigDecimal.valueOf(qty));
            }

            // 高精度のまま集計（必要ならここで setScale で丸め）
            deptSums.merge(deptCode, lineFinalAmount, BigDecimal::add);
        }

        // DB に反映（バッチ）
        if (!deptSums.isEmpty()) {
            departmentRepository.incrementDepartmentSales(deptSums);
        }

        // ユーザの集計・ポイント更新
        int updated = userRepository.updateAfterOrder(appUser.getId(), totalItems, 1, billingAfterPoints, earnedPoints, usePoints);
        if (updated == 0) {
            throw new IllegalStateException("ユーザ情報の更新に失敗しました。");
        }

        // 正常終了
        return order;
    }

    private BigDecimal calculateEarnedPoints(List<CartItemInput> cartItems, Map<String, Product> productMap, BigDecimal pointRate) {
        if (cartItems == null || productMap == null) {
            return BigDecimal.ZERO.setScale(0, MONEY_ROUNDING);
        }
        BigDecimal rate = pointRate == null ? BigDecimal.ZERO : pointRate;
        BigDecimal total = BigDecimal.ZERO;

        for (CartItemInput ci : cartItems) {
            if (ci == null) continue;
            Product p = productMap.get(ci.getProductId());
            if (p == null) continue;

            int qty = ci.getQuantity() == null ? 0 : ci.getQuantity();
            if (qty <= 0) continue;

            BigDecimal baseAmount = BigDecimal.valueOf(p.getPrice()).multiply(BigDecimal.valueOf(qty));
            BigDecimal basePoints = baseAmount.multiply(rate);
            BigDecimal multiplier = BigDecimal.ONE.add(p.getPointMag());
            BigDecimal linePoints = basePoints.multiply(multiplier);
            total = total.add(linePoints);
        }

        return total.setScale(0, MONEY_ROUNDING);
    }

    private BigDecimal adjustEarnedPointsForPointUsage(BigDecimal rawEarnedPoints, BigDecimal billingAmount, BigDecimal billingAfterPoints) {
        if (rawEarnedPoints == null) {
            rawEarnedPoints = BigDecimal.ZERO;
        }
        if (billingAmount == null || billingAfterPoints == null || billingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return rawEarnedPoints.setScale(0, MONEY_ROUNDING);
        }

        BigDecimal ratio = billingAfterPoints.divide(billingAmount, 6, RoundingMode.HALF_UP);
        BigDecimal adjusted = rawEarnedPoints.multiply(ratio);
        return adjusted.setScale(0, MONEY_ROUNDING);
    }
}