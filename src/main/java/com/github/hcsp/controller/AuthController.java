package com.github.hcsp.controller;

import com.github.hcsp.entity.LoginResult;
import com.github.hcsp.service.UserContext;
import com.github.hcsp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/auth")
    @ResponseBody
    public LoginResult auth() {
        return UserContext.getCurrentUser()
                .map(LoginResult::success)
                .orElse(LoginResult.success("用户没有登录", false));
    }

    @GetMapping("/auth/logout")
    @ResponseBody
    public LoginResult logout() {
        SecurityContextHolder.clearContext();
        // logout要清理掉在ThreadLocal中保存的用户
        UserContext.removeUser();
        return UserContext.getCurrentUser()
                .map(user -> LoginResult.success("success", false))
                .orElse(LoginResult.failure("用户没有登录"));
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public LoginResult register(@RequestBody Map<String, String> usernameAndPassword) {
        String username = usernameAndPassword.get("username");
        String password = usernameAndPassword.get("password");
        if (username == null || password == null) {
            return LoginResult.failure("username/password == null");
        }
        if (username.length() < 1 || username.length() > 15) {
            return LoginResult.failure("invalid username");
        }
        if (password.length() < 1 || password.length() > 15) {
            return LoginResult.failure("invalid password");
        }

        try {
            userService.save(username, password);
        } catch (DuplicateKeyException e) {
            return LoginResult.failure("user already exists");
        }
        return LoginResult.success("注册成功", userService.getUserByUsername(username));
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public Object login(@RequestBody Map<String, Object> usernameAndPassword, HttpServletRequest request) {
        String username = usernameAndPassword.get("username").toString();
        String password = usernameAndPassword.get("password").toString();

        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return LoginResult.failure("用户不存在");
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        try {
            authenticationManager.authenticate(token);
            // 把用户信息保存在一个地方
            //   Cookie
            SecurityContextHolder.getContext().setAuthentication(token);

            return LoginResult.success("登录成功", userService.getUserByUsername(username));
        } catch (BadCredentialsException e) {
            return LoginResult.failure("密码不正确");
        }
    }
}
