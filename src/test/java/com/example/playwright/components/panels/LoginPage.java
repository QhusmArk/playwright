package com.example.playwright.components.panels;

import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.InputField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginPage {

    InputField emailField;
    InputField passwordField;

    String errorMessage;
    String errorExplanation;
    Button loginButton;
}
