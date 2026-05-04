package com.example.playwright.components.parts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
Header row har antingen:
- FilterIcon + "Filter"
- KryssIcon + X selected

Textrader har:
TickIcon + Text

DeviceIcons har:
Checkbox + deviceTypeText
 */
@Getter
@Setter
@NoArgsConstructor
public class FilterItem {

    Icon icon;
    Checkbox checkbox;

    String text;
}
