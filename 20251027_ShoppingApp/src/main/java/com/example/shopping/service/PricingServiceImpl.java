package com.example.shopping.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.shopping.dto.CartPricingResult;
import com.example.shopping.dto.LinePricingResult;
import com.example.shopping.entity.Department;
import com.example.shopping.entity.Product;
import com.example.shopping.input.CartItemInput;
import com.example.shopping.repository.DepartmentRepository;
import com.example.shopping.repository.JdbcProductRepository;

@Service
public class PricingServiceImpl implements PricingService {

    private static final Logger logger = LoggerFactory.getLogger(PricingServiceImpl.class);

    private static final int INTERNAL_SCALE = 6;
    private static final RoundingMode FINAL_ROUNDING = RoundingMode.HALF_EVEN;

    private final JdbcProductRepository productRepository;
    private final DepartmentRepository departmentRepository;
    private final DiscountCalculator discountCalculator;
    private final TaxCalculator taxCalculator;

    public PricingServiceImpl(JdbcProductRepository productRepository,DepartmentRepository departmentRepository,DiscountCalculator discountCalculator,TaxCalculator taxCalculator) {
        this.productRepository = productRepository;
        this.departmentRepository = departmentRepository;
        this.discountCalculator = discountCalculator;
        this.taxCalculator = taxCalculator;
    }

    @Override
    public CartPricingResult priceCart(List<CartItemInput> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return CartPricingResult.empty();
        }

        // productId を一括収集して取得（ N+1 回避）
        List<String> productIds = cartItems.stream()
                .filter(ci -> ci != null && ci.getProductId() != null)
                .map(CartItemInput::getProductId)
                .distinct()
                .collect(Collectors.toList());

        List<Product> products = productRepository.findByIds(productIds);
        Map<String, Product> productMap = products == null ? Collections.emptyMap()
                : products.stream().collect(Collectors.toMap(Product::getId, p -> p));

        // departmentCode 一括収集と取得
        Set<Integer> deptCodes = productMap.values().stream()
                .map(Product::getDepartmentCode)
                .filter(dc -> dc != null)
                .collect(Collectors.toSet());
        List<Department> departments = deptCodes.isEmpty()
                ? Collections.emptyList()
                : departmentRepository.findByCodes(deptCodes.stream().collect(Collectors.toList()));
        Map<Integer, Department> deptMap = departments == null ? Collections.emptyMap()
                : departments.stream().collect(Collectors.toMap(Department::getCode, d -> d));

        CartPricingResult result = new CartPricingResult();
        for (CartItemInput ci : cartItems) {
            if (ci == null) continue;
            Product p = productMap.get(ci.getProductId());
            Department dept = (p != null && p.getDepartmentCode() != null) ? deptMap.get(p.getDepartmentCode()) : null;
            if (p == null) {
                logger.warn("Product not found for cart item: {}", ci.getProductId());
                continue;
            }

            // ここでフォールバックを適用（メモリ上で product に割引率/税率を設定）
            applyFallbackRates(p, dept);

            int qty = ci.getQuantity() == null ? 0 : ci.getQuantity();
            BigDecimal unitPrice = BigDecimal.valueOf(p.getPrice()).setScale(INTERNAL_SCALE, FINAL_ROUNDING);
            BigDecimal baseAmount = unitPrice.multiply(BigDecimal.valueOf(qty)).setScale(INTERNAL_SCALE, FINAL_ROUNDING);
         // <<呼び出し直前にログ追加（SLF4J）
            logger.debug("Pricing: productId={} productDiscount={} deptCode={} deptDiscount={}",
                p.getId(),
                p.getDiscountRate(),
                dept == null ? null : dept.getCode(),
                dept == null ? null : dept.getDiscountRate());

            BigDecimal appliedDiscountRate = discountCalculator.determineDiscountRate(p, dept);

            logger.debug("Pricing: appliedDiscountRate for productId={} => {}", p.getId(), appliedDiscountRate);            BigDecimal discountAmount = discountCalculator.calculateDiscountAmount(unitPrice, qty, appliedDiscountRate);
         // デバッグ用>>
            BigDecimal amountAfterDiscount = baseAmount.subtract(discountAmount).setScale(INTERNAL_SCALE, FINAL_ROUNDING);

            BigDecimal appliedTaxRate = taxCalculator.determineTaxRate(p, dept);
            BigDecimal taxAmount = taxCalculator.calculateTaxAmount(amountAfterDiscount, appliedTaxRate);

            BigDecimal finalAmount = amountAfterDiscount.add(taxAmount).setScale(INTERNAL_SCALE, FINAL_ROUNDING);

            LinePricingResult line = new LinePricingResult();
            line.setUnitPrice(unitPrice);
            line.setQuantity(qty);
            line.setBaseAmount(baseAmount);
            line.setAppliedDiscountRate(appliedDiscountRate);
            line.setDiscountAmount(discountAmount);
            line.setAmountAfterDiscount(amountAfterDiscount);
            line.setAppliedTaxRate(appliedTaxRate);
            line.setTaxAmount(taxAmount);
            line.setFinalAmount(finalAmount);

            result.addLineResult(line);
        }

        // 合計を集約
        result.aggregateTotals();

        // billingAmount はカート合計に銀行丸め（scale=0）
        BigDecimal billing = result.getTotalAmountAfterDiscount()
                .add(result.getTotalTaxAmount())
                .setScale(0, FINAL_ROUNDING);
        result.setBillingAmount(billing);

        // 互換のため totalBaseAmountRounded を用意（表示用）
        result.setTotalBaseAmountRounded(result.getTotalBaseAmount().setScale(0, FINAL_ROUNDING));

        return result;
    }

    private void applyFallbackRates(Product p, Department dept) {
        if (p == null) return;

        // 割引率のフォールバック：product.discountRate が null なら department.discountRate をセット（メモリ上）
        if (p.getDiscountRate() == null && dept != null && dept.getDiscountRate() != null) {
            p.setDiscountRate(dept.getDiscountRate());
        }

        if (p.getTaxRate() == null && dept != null && dept.getTaxRate() != null) {
            p.setTaxRate(dept.getTaxRate());
        }
    }

}