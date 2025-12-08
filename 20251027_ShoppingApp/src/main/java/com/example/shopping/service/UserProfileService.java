package com.example.shopping.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.shopping.dto.UserProfileDto;
import com.example.shopping.entity.AppUser;
import com.example.shopping.repository.UserRepository;

@Service
public class UserProfileService {
    private final UserRepository userRepository;

    public UserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileDto getProfileByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("username must not be empty");
        }

        AppUser user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("ユーザーが見つかりません: " + username);
        }

        BigDecimal total = user.getTotalSpent();

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.JAPAN);
        nf.setGroupingUsed(true);
        nf.setMaximumFractionDigits(0);
        String formatted = "¥" + nf.format(total);

        return new UserProfileDto(
                user.getUsername(),
                user.getRole(),
                user.getItemsPurchased(),
                user.getPurchaseCount(),
                total,
                formatted
        );
    }
}