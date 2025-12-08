package com.example.shopping.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.shopping.dto.UserProfileDto;
import com.example.shopping.service.UserProfileService;

@Controller
public class UserProfileController {
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/user/profile")
    public String profile(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        UserProfileDto dto = userProfileService.getProfileByUsername(username);
        model.addAttribute("profile", dto);
        return "user/profile";
    }
}