package com.example.playwright.testUsers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestUserPool {

    private static final ThreadLocal<TestUser> CURRENT_USER = new ThreadLocal<>();
    private static final ConcurrentLinkedQueue<TestUser> AVAILABLE_USERS = new ConcurrentLinkedQueue<>();

    static {
        // Initialize pool (you can later replace with real users)
        AVAILABLE_USERS.addAll(List.of(
                new TestUser("user1", "password"),
                new TestUser("user2", "password")
        ));
    }

    public static Optional<TestUser> acquireUser() {
        TestUser user = AVAILABLE_USERS.poll();
        if (user != null) {
            CURRENT_USER.set(user);
        }
        return Optional.ofNullable(user);
    }

    public static void releaseUser(TestUser user) {
        if (user != null) {
            AVAILABLE_USERS.offer(user);
            CURRENT_USER.remove();
        }
    }

    public static TestUser getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void releaseCurrentUser() {
        TestUser user = CURRENT_USER.get();

        if (user != null) {
            AVAILABLE_USERS.offer(user);
            CURRENT_USER.remove();
        }
    }
}