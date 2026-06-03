package com.example.demo.security;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class RoleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            
            PreAuthorize auth = method.getMethodAnnotation(PreAuthorize.class);
            if (auth == null) {
                auth = method.getBeanType().getAnnotation(PreAuthorize.class);
            }
            
            if (auth != null) {
                String userRole = request.getHeader("X-Role");
                if (userRole != null) {
                    userRole = URLDecoder.decode(userRole, StandardCharsets.UTF_8);
                } else {
                    userRole = "";
                }
                
                boolean hasAccess = false;
                for (String role : auth.roles()) {
                    if (role.equalsIgnoreCase(userRole)) {
                        hasAccess = true;
                        break;
                    }
                }
                
                if (!hasAccess) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write("{\"message\": \"Truy cập bị từ chối: Bạn không có quyền thực hiện thao tác này!\"}");
                    return false;
                }
            }
        }
        return true;
    }
}
