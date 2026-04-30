package com.example.playwright.components.parts.menues;

import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.Checkbox;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteDialog {
    String header;
    String mainText;

    Checkbox confirmationCheckbox;
    Button cancelButton;
    Button deleteButton;
}
