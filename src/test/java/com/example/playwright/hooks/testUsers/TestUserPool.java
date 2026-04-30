package com.example.playwright.hooks.testUsers;

import com.example.playwright.config.TestUserLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestUserPool {

    private static final ThreadLocal<TestUser> CURRENT_USER = new ThreadLocal<>();
    private static final Object LOCK = new Object();

    private static final List<TestUser> AVAILABLE_USERS = new ArrayList<>();

    static {
        AVAILABLE_USERS.addAll(TestUserLoader.loadUsers());
    }

    public static Optional<TestUser> acquireUser() {
        synchronized (LOCK) {
            if (AVAILABLE_USERS.isEmpty()) {
                return Optional.empty();
            }

            TestUser user = AVAILABLE_USERS.removeFirst();
            CURRENT_USER.set(user);
            return Optional.of(user);
        }
    }

    public static TestUser acquireUserWithRole(String role) {
        synchronized (LOCK) {
            while (true) {
                for (int i = 0; i < AVAILABLE_USERS.size(); i++) {
                    TestUser user = AVAILABLE_USERS.get(i);

                    if (user.role().equalsIgnoreCase(role)) {
                        AVAILABLE_USERS.remove(i);
                        CURRENT_USER.set(user);
                        return user;
                    }
                }

                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(
                            "Interrupted while waiting for test user with role: " + role,
                            e
                    );
                }
            }
        }
    }

    public static TestUser getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void releaseCurrentUser() {
        synchronized (LOCK) {
            TestUser user = CURRENT_USER.get();

            if (user != null) {
                AVAILABLE_USERS.add(user);
                CURRENT_USER.remove();

                LOCK.notifyAll(); // wake waiting threads
            }
        }
    }
}