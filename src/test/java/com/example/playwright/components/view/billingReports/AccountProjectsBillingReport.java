package com.example.playwright.components.view.billingReports;

import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.DataViewStatus;
import com.example.playwright.components.parts.Table;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountProjectsBillingReport {
    PanelHeader panelHeader;
    String reportDuration;
    Button exportButton;
    DataViewStatus dataViewStatus;
    Button columnFilter;
    Table reportContent;
}
