package com.example.playwright.components.parts;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * E.g., MapSearchLocation
 */
@Data
@NoArgsConstructor()
public class Listbox  {

    List<MenuOption> options;
}
