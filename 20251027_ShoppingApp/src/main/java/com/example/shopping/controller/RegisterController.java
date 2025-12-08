package com.example.shopping.controller;

import com.example.shopping.entity.AppUser;
import com.example.shopping.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping // 新規登録ページを表示
    public String displayRegisterPage(Model model) {
        model.addAttribute("user", new AppUser());
        return "auth/register";
    }

    @PostMapping // 新規登録処理
    public String registerUser(@Valid AppUser user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "入力内容に誤りがあります。再度確認してください。");
            return "auth/register"; // 入力エラーがあれば再度登録ページを表示
        }
        try {
            userService.registerUser(user);
            model.addAttribute("success", true);
            return "auth/login"; // 登録成功後ログインページへリダイレクト
        } catch (Exception e) {
            model.addAttribute("errorMessage", "登録処理中にエラーが発生しました。再度お試しください。");
            return "auth/register"; // 登録エラーが発生した場合
        }
    }
}