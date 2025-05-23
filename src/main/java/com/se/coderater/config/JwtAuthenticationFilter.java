package com.se.coderater.config;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// 移除 JWT 相关依赖
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 移除 UserDetailsService 和 secretKey 字段
    // private final UserDetailsService userDetailsService;
    // @Value("${jwt.secret}")
    // private String secretKey;

    public JwtAuthenticationFilter() {
        // 构造函数无需参数
        // this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 禁用 JWT 验证逻辑，直接放行所有请求
        filterChain.doFilter(request, response);
    }
}