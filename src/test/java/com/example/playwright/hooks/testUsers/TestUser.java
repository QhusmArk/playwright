package com.example.playwright.hooks.testUsers;

public record TestUser(
        String email,
        String role,
        int id,
        String password,
        String token
) {
}