package com.example.playwright.components.parts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.example.helpers.StatusAssesser.Status;

@Getter
@Setter
@NoArgsConstructor
public class SearchBox {

    private Status status; // tbd keeps info if searchbox is disabled
    private String text;
//    private IconType iconType;
    Icon icon;
}
