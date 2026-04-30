package com.example.playwright.components.parts.panelParts;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.playwright.components.parts.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This component is a vision on how to package the data in a panel that is structured in groups.
 * E.g., the parts of a message rule: /project/id/message_rules/id/settings/content.
 *
 * Instead of placing all page content into the FieldWrapper, it will be used as a placeholder for header rows.
 */
@Getter
@Setter
@NoArgsConstructor
public class FieldWrapper {
    private Icon leftIcon;
    private String header;
    private NoticeItem noticeItem;
    private List<Button> buttons;
    private List<Object> content;

    private Dropdown dropdown;
    private InputField inputField;
    private TimeFrame timeFrame;

   @JsonIgnore
    public void addContent(Object object) {
        if (content == null) {
            this.content = new ArrayList<>();
        }
        this.content.add(object);
    }

    @JsonIgnore
    public void addButton(Button button) {
       if (this.buttons == null) {
           this.buttons = new ArrayList<>();
       }
       buttons.add(button);
    }

    @JsonIgnore
    @SuppressWarnings("unchecked")
    public <T> List<T> getListOfType(Class<T> clazz) {
        return this.content.stream()
                .filter(obj -> obj instanceof List<?> list &&
                        list.stream().allMatch(clazz::isInstance))
                .map(obj -> (List<T>) obj)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No List<" + clazz.getSimpleName() + "> found"));
    }

    @JsonIgnore
    public Button getButton(String text) {
        return getObjects(Button.class).stream()
                .filter(button -> text.equals(button.getText()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No button with text '" + text + "'"));
    }

    @JsonIgnore
    public SearchBox getSearchBox() {
        return this.content.stream()
                .filter(SearchBox.class::isInstance)
                .map(SearchBox.class::cast)
                .findFirst()
                .orElse(null); // todo: den här hämtar bara första instansen, men det borde bara finnas en searchbox per wrapper
    }

    @JsonIgnore
    public TimeFrame getTimeFrame() {
        return this.content.stream()
                .filter(TimeFrame.class::isInstance)
                .map(TimeFrame.class::cast)
                .findFirst()
                .orElse(null);  // todo: den här hämtar bara första timeframe, men det borde bara finnas en timeframe per wrapper
    }

    @JsonIgnore
    public InputField getInputField(String name) {
        return getObjects(InputField.class).stream()
                .filter(inputField -> name.equals(inputField.getHeader()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No input field with header '" + name + "'"));
    }

    @JsonIgnore
    public Dropdown getDropdown(String name) {
        return getObjects(Dropdown.class).stream()
                .filter(dropdown -> name.equals(dropdown.getHeader()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No dropdown with header '" + name + "'"));
    }

    @JsonIgnore
    public <T> List<T> getObjects(Class<T> clazz) {
        return this.content.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .toList();
    }


}
