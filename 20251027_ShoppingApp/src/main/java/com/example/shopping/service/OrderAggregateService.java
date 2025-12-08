package com.example.shopping.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.example.shopping.repository.UserRepository;

@Service
public class OrderAggregateService {

    private final UserRepository userRepository;

    public OrderAggregateService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 注文確定時に呼び出すメソッド。
     * @param userId 更新対象の user.id
     * @param itemsPurchasedIncrement 注文内の合計点数（>=0）
     * @param orderTotal 注文金額（>=0）
     * @throws IllegalArgumentException 引数が不正な場合
     * @throws IllegalStateException 更新対象ユーザが存在しない場合
     */
    @Transactional
    public void applyOrderToUserAggregates(String userId, int itemsPurchasedIncrement, BigDecimal orderTotal) {
        Assert.hasText(userId, "userId must not be empty");
        if (itemsPurchasedIncrement < 0) {
            throw new IllegalArgumentException("itemsPurchasedIncrement must be >= 0");
        }
        if (orderTotal == null || orderTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("orderTotal must be >= 0");
        }

        int updated = userRepository.updateAggregatesById(userId, itemsPurchasedIncrement, 1, orderTotal);
        if (updated == 0) {
            // id に対応するユーザが存在しない、または何らかの想定外
            throw new IllegalStateException("ユーザが見つかりません（id=" + userId + "）");
        }
    }
}