package com.example.playwright.components.aside.asideItems;

import com.example.playwright.components.parts.Banner;
import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.Checkbox;
import com.example.playwright.components.parts.Icon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AsideItem  {

    Icon leftIcon;
    Checkbox leftCheckbox;

    String mainText;
    String subText;
    List<Banner> banners;
    Icon listItemIcon;

//    Icon rightIcon;
    Button rightButton;
}
