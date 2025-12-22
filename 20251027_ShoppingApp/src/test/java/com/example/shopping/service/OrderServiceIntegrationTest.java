package com.example.shopping.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.shopping.controller.OrderSession;
import com.example.shopping.entity.AppUser;
import com.example.shopping.enumeration.PaymentMethod;
import com.example.shopping.exception.StockShortageException;
import com.example.shopping.input.CartInput;
import com.example.shopping.input.CartItemInput;
import com.example.shopping.input.OrderInput;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderSession orderSession;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void placeOrder_shouldPersistOrderAndAdjustBalances() {
        AppUser admin = userService.findByUsername("admin");
        orderSession.setAppUser(admin);

        CartItemInput item = new CartItemInput();
        item.setProductId("p10");
        item.setQuantity(1);

        CartInput cartInput = new CartInput();
        cartInput.setCartItemInputs(List.of(item));

        OrderInput orderInput = new OrderInput();
        orderInput.setName("注文者");
        orderInput.setAddress("東京都");
        orderInput.setPhone("090-0000-0000");
        orderInput.setEmailAddress("test@example.com");
        orderInput.setPaymentMethod(PaymentMethod.BANK);
        orderInput.setUsePoints(100);

        BigDecimal beforeSales = jdbcTemplate.queryForObject(
                "select department_sales from t_department where code = ?", BigDecimal.class, 1);

        var order = orderService.placeOrder(orderInput, cartInput);

        Map<String, Object> orderRow = jdbcTemplate.queryForMap("select * from t_order where id = ?", order.getId());
        assertEquals(new BigDecimal("1000"), orderRow.get("billing_amount"));
        assertEquals(new BigDecimal("100.00"), orderRow.get("points_used"));
        assertEquals(new BigDecimal("18.00"), orderRow.get("points_earned"));

        Integer orderItemCount = jdbcTemplate.queryForObject(
                "select count(*) from t_order_item where order_id = ?", Integer.class, order.getId());
        assertEquals(1, orderItemCount);

        Integer stock = jdbcTemplate.queryForObject("select stock from t_product where id = ?", Integer.class, "p10");
        assertEquals(99, stock);

        BigDecimal userPoints = jdbcTemplate.queryForObject("select points from t_user where id = ?", BigDecimal.class, "u01");
        assertEquals(new BigDecimal("918.00"), userPoints);

        BigDecimal afterSales = jdbcTemplate.queryForObject(
                "select department_sales from t_department where code = ?", BigDecimal.class, 1);
        assertEquals(beforeSales.add(new BigDecimal("1100.00")), afterSales);
    }

    @Test
    void placeOrder_shouldRollbackWhenStockShortage() {
        AppUser admin = userService.findByUsername("admin");
        orderSession.setAppUser(admin);

        CartItemInput item = new CartItemInput();
        item.setProductId("p01");
        item.setQuantity(999);

        CartInput cartInput = new CartInput();
        cartInput.setCartItemInputs(List.of(item));

        OrderInput orderInput = new OrderInput();
        orderInput.setName("注文者");
        orderInput.setAddress("東京都");
        orderInput.setPhone("090-0000-0000");
        orderInput.setEmailAddress("test@example.com");
        orderInput.setPaymentMethod(PaymentMethod.BANK);
        orderInput.setUsePoints(0);

        assertThrows(StockShortageException.class, () -> orderService.placeOrder(orderInput, cartInput));

        Integer orderCount = jdbcTemplate.queryForObject("select count(*) from t_order", Integer.class);
        assertEquals(2, orderCount);

        Integer orderItemCount = jdbcTemplate.queryForObject("select count(*) from t_order_item", Integer.class);
        assertEquals(2, orderItemCount);

        Integer stock = jdbcTemplate.queryForObject("select stock from t_product where id = ?", Integer.class, "p01");
        assertEquals(10, stock);
    }
}
