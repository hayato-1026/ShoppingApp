package com.example.shopping.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests()
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/h2-console/**", "/register").permitAll() // 認証不要のURL
                .anyRequest().authenticated() // その他のリクエストは認証が必要
            .and()
            .formLogin()
                .loginPage("/login") // ログインページの指定
                .defaultSuccessUrl("/catalog/display-list", true) // ログイン成功時のリダイレクト先
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/logout") // ログアウトURLの指定
                .logoutSuccessUrl("/logout-success") // ログアウト後のリダイレクト先
                .permitAll();
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}