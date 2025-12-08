package com.example.shopping.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.example.shopping.entity.Department;
import com.example.shopping.entity.Product;

@Service
public class DiscountCalculatorImpl implements DiscountCalculator {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final int INTERNAL_SCALE = 6;

//    @Override
//    public BigDecimal determineDiscountRate(Product product, Department department) {
//        if (product != null && product.getDiscountRate() != null) {
//            return product.getDiscountRate();
//        }
//        if (department != null && department.getDiscountRate() != null) {
//            return department.getDiscountRate();
//        }
//        return ZERO;
//    }

 // クラス上部に logger を定義（SLF4J）
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DiscountCalculatorImpl.class);

    @Override
    public BigDecimal determineDiscountRate(Product product, Department department) {
        BigDecimal pr = (product == null) ? null : product.getDiscountRate();
        BigDecimal dr = (department == null) ? null : department.getDiscountRate();
        logger.debug("determineDiscountRate called: productId={} productDiscount={} deptCode={} deptDiscount={}",
            product == null ? null : product.getId(),
            pr,
            department == null ? null : department.getCode(),
            dr);
        if (pr != null) {
            logger.debug("determineDiscountRate => using product discount: {}", pr);
            return pr;
        }
        if (dr != null) {
            logger.debug("determineDiscountRate => using department discount: {}", dr);
            return dr;
        }
        logger.debug("determineDiscountRate => none, return ZERO");
        return ZERO;
    }

    @Override
    public BigDecimal calculateDiscountAmount(BigDecimal unitPrice, int quantity, BigDecimal discountRate) {
        if (unitPrice == null) {
            unitPrice = ZERO;
        }
        if (discountRate == null) {
            discountRate = ZERO;
        }
        BigDecimal qty = BigDecimal.valueOf(quantity);
        BigDecimal base = unitPrice.multiply(qty);
        return base.multiply(discountRate).setScale(INTERNAL_SCALE, RoundingMode.HALF_EVEN);
    }
}