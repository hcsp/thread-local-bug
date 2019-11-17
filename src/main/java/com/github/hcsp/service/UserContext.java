package com.github.hcsp.service;

import com.github.hcsp.entity.User;

import java.util.Optional;

public class UserContext {
    public static ThreadLocal<User> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public static Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser.get());
    }
<<<<<<< HEAD

    public static void removeCurrentUser() {
         currentUser.remove();
    }

=======
    public static void cleanCurrentUser(){
        currentUser.remove();
    }
>>>>>>> cc7ba7d... fix ThreadLocal 脏数据问题 (#6)
}
