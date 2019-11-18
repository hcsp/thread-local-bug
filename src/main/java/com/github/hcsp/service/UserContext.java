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

    public static void removeuser() {
        currentUser.remove();
    }

=======
>>>>>>> e5b2cb957e44b2802c3eef7ed270ed51acafb806
}
