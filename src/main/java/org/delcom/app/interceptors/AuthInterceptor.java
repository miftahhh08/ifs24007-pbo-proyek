package org.delcom.app.interceptors;

import jakarta.servlet.http.HttpServletRequest; // Gunakan javax.servlet jika jakarta merah
import jakarta.servlet.http.HttpServletResponse;

import org.delcom.app.entities.User; // [FIX]: Ini yang biasanya bikin error
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            request.setAttribute("user", user);
        }
        
        return true;
    }
}