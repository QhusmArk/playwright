package com.example.playwright.session;

import com.example.playwright.testUsers.TestUser;
import com.google.gson.Gson;
import com.microsoft.playwright.options.Cookie;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class SessionCookieManager {

    private static final Gson GSON = new Gson();
    private static final Path SESSION_DIR = Path.of("src/test/resources/session-cookies");

    public static Path getSessionCookiePath(TestUser user) {
        ensureSessionDirExists();
        return SESSION_DIR.resolve(toSessionCookieFileName(user));
    }

    public static boolean hasSessionCookie(TestUser user) {
        return Files.exists(getSessionCookiePath(user));
    }

    public static Optional<Cookie> loadSessionCookie(TestUser user) {
        Path path = getSessionCookiePath(user);

        if (!Files.exists(path)) {
            return Optional.empty();
        }

        try {
            String json = Files.readString(path);
            return Optional.of(GSON.fromJson(json, Cookie.class));
        } catch (Exception e) {
            throw new IllegalStateException("Could not load session cookie for user: " + user.email(), e);
        }
    }

    public static void saveSessionCookie(TestUser user, Cookie cookie) {
        Path path = getSessionCookiePath(user);

        try {
            Files.writeString(path, GSON.toJson(cookie));
            System.out.println("Saved session cookie for user: " + user.email());
        } catch (Exception e) {
            throw new IllegalStateException("Could not save session cookie for user: " + user.email(), e);
        }
    }

    private static void ensureSessionDirExists() {
        try {
            Files.createDirectories(SESSION_DIR);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create session cookie directory: " + SESSION_DIR, e);
        }
    }

    private static String toSessionCookieFileName(TestUser user) {
        String emailPrefix = user.email()
                .replace("@sigicom.com", "")
                .replaceAll("_$", "");

        return emailPrefix + "_session_cookie.json";
    }
}