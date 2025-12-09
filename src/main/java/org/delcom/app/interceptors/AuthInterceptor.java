package org.delcom.app.interceptors;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.AuthToken;
import org.delcom.app.services.AuthTokenService;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired AuthContext authContext;
    @Autowired AuthTokenService authTokenService;
    @Autowired UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isPublic(request.getRequestURI())) return true;

        String token = getToken(request);
        if (token != null && JwtUtil.validateToken(token, true)) {
            UUID userId = JwtUtil.extractUserId(token);
            if (userId != null) {
                AuthToken authToken = authTokenService.findUserToken(userId, token);
                if (authToken != null) {
                    var user = userService.getUserById(authToken.getUserId());
                    if (user != null) {
                        authContext.setAuthUser(user);
                        return true;
                    }
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return false;
    }

    private String getToken(HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) if ("AUTH_TOKEN".equals(c.getName())) return c.getValue();
        }
        return null;
    }
    private boolean isPublic(String uri) { return uri.startsWith("/auth") || uri.startsWith("/css") || uri.startsWith("/js") || uri.startsWith("/images") || uri.equals("/error"); }
}