package com.github.hcsp.service;

import com.github.hcsp.entity.User;

import java.util.Optional;

public class UserContext {
    private static ThreadLocal<User> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public static Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser.get());
    }

    public static void removeCurrentUser() {
         currentUser.remove();
    }
}
