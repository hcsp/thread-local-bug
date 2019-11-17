package com.github.hcsp.service;

import com.github.hcsp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserContextInterceptor implements HandlerInterceptor {
    private final UserService userService;

    @Autowired
    public UserContextInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            User user = userService.getUserByUsername(authentication.getName());
            if (user != null) {
                UserContext.setCurrentUser(user);
            }
        }
        UserContext.removeCurrentUser();
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) {
        UserContext.removeCurrentUser();
    }

}
