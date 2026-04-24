package com.example.playwright.testUsers;

public record TestUser(
        String email,
        String role,
        int id,
        String password,
        String token
) {
}