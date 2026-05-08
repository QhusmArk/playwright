package com.example.playwright.pageObjects;

import com.example.helpers.StatusAssesser;
import com.example.helpers.StatusAssesser.Status;
import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.Table.TableRow;
import com.example.playwright.components.parts.menues.DeleteDialog;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.AsideSize;
import com.example.playwright.helpers.enums.ColourSchema;
import com.example.playwright.helpers.enums.IconType;
import com.example.playwright.hooks.BrowserHooks;
import com.microsoft.playwright.Page;

import java.util.*;

import static com.example.helpers.StatusAssesser.Status.CLICKABLE;
import static com.example.playwright.helpers.enums.IconType.*;

public abstract class CommonPO {

    // If I need to access page from Glue layer, use this
    protected Page page() {
        return BrowserHooks.getPage();
    }

    protected PlaywrightActions actions() {
        return BrowserHooks.getActions();
    }

//    *********************************************


    // Headers
    static final String USER_ICON = "//header //*[text()='account_circle']";

    // Menu
    static final String USER_MENU_HEADER = "//div[@role='menu'] //a[@data-qa-id='user_menu_header']";
    static final String USER_MENU_PROFILE = "//div[@role='menu'] //a[@data-qa-id='user_settings']";
    static final String USER_MENU_ACADEMY = "//div[@role='menu'] //a[@data-qa-id='infra_academy_link']";
    static final String USER_MENU_SUPPORT = "//div[@role='menu'] //a[@data-qa-id='support']";
    static final String USER_MEU_CLASSIC = "//div[@role='menu'] //a[@data-qa-id='old_client_link']";
    static final String USER_MENU_LOGOUT = "//div[@role='menu'] //a[@data-qa-id='logout']"; //language sensitive?

    // Left Panel Header
    static final String ASIDE_HEADER_SEARCH = "//button[@data-qa-id='search-button']";
    static final String INPUT_SEARCH_FIELD = "//label[@data-qa-id='free-text-search-field'] //input";


    // Left Panel List
    static final String LEFT_MENU_AREA = "//div[@class='q-virtual-scroll__content']";

    static final String TOP_PLUS_SIGN = "//*[@data-qa-id='create-new-entity']";
    static final String SUBMIT = "//form //button[@type='submit']";
    static final String FORM_ARROWS = "//form //i[text()='arrow_drop_down']";

    public void setAsideSize(AsideSize asideSize) {
        actions().setAsideSize(asideSize.toString());
    }

    public void setAsideSize(String asideSize) {
        actions().setAsideSize(asideSize);
    }

    public String getValidationMessageText(String field) {
        //
        return actions().findOneElementsText("//label[.//div[contains(text(), '"+field+"')]] //div[@class='q-field__messages col']");
    }

    // todo: perhaps this belong in a PanelPO?
    /**
     * Throws TimeoutException if actualHeaderText cannot be located.
     */
    public Boolean isPanelHeaderText(String expectedHeaderText) {
        String actualHeaderText = actions().findOneElementsText("//div[@data-qa-id='panel-header'] //div[text()='"+expectedHeaderText+"']");
        return actualHeaderText.equals(expectedHeaderText);
    }

    // todo: perhaps this belong in a PanelPO?
    /**
     * Throws TimeoutException if actualHeaderText cannot be located.
     */
    public Boolean isPanelHeaderText(String idLocator, String expectedHeaderText) {
        String actualHeaderText = actions().findOneElementsText("//div[@data-qa-id='"+idLocator+"'] //div[text()='"+expectedHeaderText+"']");
        return actualHeaderText.equals(expectedHeaderText);
    }

    // todo: perhaps this belong in a PanelPO?
    /**
     * Method used to validate the existence of panel header, but not caring what it says. Mtd is like a dynamic sleep.
     * Throws TimeoutException if actualHeaderText cannot be located.
     */
    public Boolean isPanelHeaderVisible(String idLocator) {
        return actions().elementExistAndVisible("//*[@data-qa-id='"+idLocator+"']");
    }

    // todo: perhaps this belong in a MenuPO?
    public boolean isMenuPanelPresent() {
        return actions().elementExistAndVisible("//div[@role='menu']", false, 0);
    }

    // todo: perhaps this belong in a PanelPO?
    /**
     * Can be 'close' or 'arrow_back'
     */
    public String isPanelButtonXorBack() {
        return actions().findOneElementsText("//div[@data-qa-id='panel'] //button //i");
    }

    public String getToast() {
        return actions().findOneElementsText("//div[@class='q-notification__message col']");
    }

    // todo: detta är panel validation och ska vara i PanelPO
    /**
     * Works from .../users/create when omitting required input value, and clicking Save.
     */
    public boolean checkForNotifyContaining(String errorMessage) {
        return actions().elementExistAndVisible("//div[@role='alert'] //div[contains(text(),'"+errorMessage+"')]", true, 9);
    }

    public void openUserMenuAndSelectMenuItem(String menuItem) {
        actions().makeClick(USER_ICON);

        // Right after clicking on USER_ICON there is a new span element for half second. Wait it out.
        PlaywrightActions.sleep(2);

        // todo: to method in MenuPO
        switch (menuItem) {
            case "USER_PROFILE" -> actions().makeClick(USER_MENU_PROFILE);
            case "INFRA_ACADEMY" -> actions().makeClick(USER_MENU_ACADEMY);
            case "SUPPORT" -> actions().makeClick(USER_MENU_SUPPORT);
            case "CLASSIC" -> actions().makeClick(USER_MEU_CLASSIC);
            case "LOGOUT" -> actions().makeClick(USER_MENU_LOGOUT);
        }
    }

    /**
     * @param parentPath the parent of the icon
     */
    public Icon completeGetIcon(final String parentPath) {
        return getIcon1(parentPath);
    }

    public Icon getIcon(final String elementPath) {
        return getIcon2(elementPath);
    }

    public Status getElementStatusByClassName(String elementPath) {
        String elementClassName = actions().findOneElementsAttribute(elementPath, "class");
        return StatusAssesser.getStatusByClassName(elementClassName);
    }

    public ColourSchema getElementColourByClassName(String elementPath) {
        String elementClassName = actions().findOneElementsAttribute(elementPath, "class");
        return ColourSchema.getTextColourFromClassName(elementClassName);
    }

    public ColourSchema getElementBackgroundColourByClassName(String elementPath) {
        String elementClassName = actions().findOneElementsAttribute(elementPath, "class");
        ColourSchema backgroundColour = ColourSchema.getBackgroundColourFromClassName(elementClassName);

        // Some class names do not contain information about bg-colour
        backgroundColour = (backgroundColour == null)
                ? getElementColourByCss(elementPath)
                : backgroundColour;

        return backgroundColour;
    }

    public ColourSchema getElementColourByCss(String elementPath) {
        String cssValue = actions().findOneElementsCssValue(elementPath, "color");
        return ColourSchema.fromCssValue(cssValue);
    }

    public TimeFrame getTimeFrame() {
        TimeFrame timeFrame = new TimeFrame();

        // From/Start-time
        String fromDatePath = "//label[@data-qa-id='time_interval_from']";
        boolean hasFromDate = actions().elementExistAndVisible(fromDatePath, false, 0);
        if (hasFromDate) timeFrame.setFromDate(getTimeInterval(fromDatePath));

        String fromTimePath = "//label[@data-qa-id='time_interval_from_time']";
        boolean hasFromTime = actions().elementExistAndVisible(fromTimePath, false, 0);
        if (hasFromTime) timeFrame.setFromTime(getTimeInterval(fromTimePath));

        // To/End-time
        String toDatePath = "//label[@data-qa-id='time_interval_to']";
        boolean hasToDate = actions().elementExistAndVisible(toDatePath, false, 0);
        if (hasToDate) timeFrame.setToDate(getTimeInterval(toDatePath));


        String toTimePath = "//label[@data-qa-id='time_interval_to_time']";
        boolean hasToTime = actions().elementExistAndVisible(toTimePath, false, 0);
        if (hasToTime) timeFrame.setToTime(getTimeInterval(toTimePath));

        String untilFurtherNoticePath = "//form //div[@role='switch']";
        boolean hasUntilFurtherNoticeToggle = actions().elementExistAndVisible(untilFurtherNoticePath, false, 0);
        if (hasUntilFurtherNoticeToggle) timeFrame.setUntilFurtherNoticeToggle(getAllTogglesInPanel().get("Until further notice"));

        return timeFrame;
    }

    public TimeFrame getTimeFrame(boolean getCalendarContent, boolean getTimeDropdownContent) {
        TimeFrame timeFrame = new TimeFrame();

        // From/Start-time
        String fromDatePath = "//label[@data-qa-id='time_interval_from']";
        boolean hasFromDate = actions().elementExistAndVisible(fromDatePath, false, 0);
        if (hasFromDate) {
            timeFrame.setFromDate(getTimeInterval(fromDatePath));

            if (getCalendarContent) {
                // Expand from-date-calendar
                actions().makeClick("//label[@data-qa-id='time_interval_from']");

                MenuCalendar fromCalendar = getMenuCalendar();
                timeFrame.setFromMenuCalendar(fromCalendar);
            }
        }

        String fromTimePath = "//label[@data-qa-id='time_interval_from_time']";
        boolean hasFromTime = actions().elementExistAndVisible(fromTimePath, false, 0);
        if (hasFromTime) {
            timeFrame.setFromTime(getTimeInterval(fromTimePath));

            if (getTimeDropdownContent) {
                // Expand from-time-dropdown
                actions().makeClick("//label[@data-qa-id='time_interval_from_time']");
            }
        }

        /************************************************************************************************/

        // To/End-time
        String toDatePath = "//label[@data-qa-id='time_interval_to']";
        boolean hasToDate = actions().elementExistAndVisible(toDatePath, false, 0);
        if (hasToDate) {
            timeFrame.setToDate(getTimeInterval(toDatePath));

            if (getCalendarContent) {
                // Expand to-calendar
                actions().makeClick("//label[@data-qa-id='time_interval_to']");
                PlaywrightActions.sleep(2);

                MenuCalendar toCalendar = getMenuCalendar();
                timeFrame.setToMenuCalendar(toCalendar);
            }
        }

        String toTimePath = "//label[@data-qa-id='time_interval_to_time']";
        boolean hasToTime = actions().elementExistAndVisible(toTimePath, false, 0);
        if (hasToTime) {
            timeFrame.setToTime(getTimeInterval(toTimePath));

            if (getTimeDropdownContent) {
                // Expand to-time-dropdown
                actions().makeClick("//label[@data-qa-id='time_interval_to_time']");
                Dropdown toTimeDropdown;
            }
        }

        String untilFurtherNoticePath = "//form //div[@role='switch']";
        boolean hasUntilFurtherNoticeToggle = actions().elementExistAndVisible(untilFurtherNoticePath, false, 0);
        if (hasUntilFurtherNoticeToggle) timeFrame.setUntilFurtherNoticeToggle(getAllTogglesInPanel().get("Until further notice"));

        return timeFrame;
    }

    private TimeFrame.TimeInterval getTimeInterval(String path) {
        TimeFrame.TimeInterval timeInterval = new TimeFrame.TimeInterval();

        boolean hasHeader = actions().elementExistAndVisible(path + " //div[@class='q-field__label no-pointer-events absolute ellipsis']", false, 0);
        String header = (hasHeader)
                ? actions().findOneElementsText(path + " //div[@class='q-field__label no-pointer-events absolute ellipsis']")
                : null;
        timeInterval.setHeader(header);

        String date = actions().findOneElementsValueAttribute(path + " //input");
        timeInterval.setValue(date);

        boolean hasFooter = actions().elementExistAndVisible(path + " //div[@class='q-field__messages col']", false, 0);
        String footer = (hasFooter)
                ? actions().findOneElementsText(path + " //div[@class='q-field__messages col']")
                : null;
        timeInterval.setFooter(footer);

        return timeInterval;
    }

    public Map<String, Boolean> getToggleInPanel(String wantedTogglesText) {
        Map<String, Boolean> allToggles = getAllTogglesInPanel();

        Map<String, Boolean> result = new HashMap<>();
        if (allToggles.containsKey(wantedTogglesText)) {
            result.put(wantedTogglesText, allToggles.get(wantedTogglesText));
        }
        return result;
    }

    /**
     * @return Map<toggleText, isToggleOn>
     */
    public Map<String, Boolean> getAllTogglesInPanel() {
        Map<String, Boolean> toggle = new HashMap<>();

        int togglesInPage = actions().countHowManyElements("//form //div[@role='switch']");

        for (int i = 1; i <= togglesInPage; i++) {
            String toggleText = actions().findOneElementsText("(//form //div[@role='switch'])["+i+"]");
            boolean toggleState = actions().findOneElementsAttribute("(//form //div[@role='switch'])["+i+"]", "aria-checked").equals("true");
            toggle.put(toggleText, toggleState);
        }
        return toggle;
    }

    /**
     * Method s h o u l d work for all toggle interactions.
     */
    public void setToggle(String expectedToggleText, boolean save) {
        actions().makeClick("//div[@role='switch' and @aria-label='"+expectedToggleText+"']");
        if (save) {
            actions().makeClick("//button[@type='submit']");
        }
    }

    public void clickAsideHeaderIcon(IconType iconType) {
        switch (iconType) {
            case COLUMNS_SELECT -> actions().makeClick("//div[@data-qa-id='aside-header'] //button[@data-qa-id='select-columns-button']");
            case SEARCH -> actions().makeClick("//div[@data-qa-id='aside-header'] //button[@data-qa-id='search-button']");
            case CANCEL -> actions().makeClick("//div[@data-qa-id='aside-header'] //i[@class='q-icon notranslate material-icons q-field__focusable-action']");
            default -> throw new IllegalStateException("Unexpected iconType: " + iconType);
        }
    }

    public void clickAsideHeaderActionIcon(IconType iconType) {
        switch (iconType) {
            case PROJECT -> actions().makeClick("//div[@data-qa-id='aside-header-action'] //i[@class='q-icon text-infra-secondary icon-projects']");
            case REPORTS -> actions().makeClick("//div[@data-qa-id='aside-header-action'] //i[@class='q-icon text-infra-secondary icon-reports']");
            default -> throw new IllegalStateException("Unexpected iconType: " + iconType);
        }
    }

    public void clickNamedButton(String buttonText) {
        actions().makeClick("//button //span[text()='"+buttonText+"']");
    }

    public void clickOnTab(String tabText) {
        actions().makeClick("//div[@class='q-tab__label' and text()='"+tabText+"']");
    }

    /**
     * @return the header below panel header, e.g., project description from .../company/projects/5657/settings/general
     */
    public String getPanelBodyHeader() {
        return actions().findOneElementsText("//form //div[contains(@data-qa-id, 'panel-body')] //span");
    }

    public void clickCreateNewUserButton() {
        actions().makeClick("//form //button //span[contains(text(), 'Create user')]");
    }

    public void clickOnButton(String buttonToClick) {
        actions().makeClick("//button //span[text()='"+buttonToClick+"']");
    }

    public boolean buttonPresent(String buttonText) {
        return actions().elementExistAndVisible("//button //span[text()='"+buttonText+"']", false, 0);
    }

    public void clickAsideListItem(String textOnListItem) {
        actions().makeClick("//div[@data-qa-id='list-item'] //span[contains(text(),'"+textOnListItem+"')]");
    }

    public void clickPanelSelection(String panelSelectionText) {
        actions().makeClick("//div[@data-qa-id='panel'] //a //div[text()='"+panelSelectionText+"']");
    }

    PanelHeader getPanelHeader() {
        return getPanelHeader("panel");
    }

    PanelHeader getPanelHeader(String type) {
        switch (type) {
            case "columnSettingsPanel" -> {  // as in TableColumnSettingsPanel
                PanelHeader panelHeader = new PanelHeader();
                String panelHeaderPath = "//div[@class='q-card']/div[1]";

                Button exitButton = getButton(panelHeaderPath + "/button");
                panelHeader.setLeftButton(exitButton);

                String panelHeaderText = actions().findOneElementsText(panelHeaderPath + "/div");
                panelHeader.setHeaderText(panelHeaderText);

                return panelHeader;
            }
            case "report" -> {
                PanelHeader panelHeader = new PanelHeader();
                String panelHeaderPath = "//div[@class='container fixed-top fullscreen'] //div[@role='toolbar']/div";

                Button exitButton = getButton(panelHeaderPath + "/div[position()=1]/descendant::button[1]");
                panelHeader.setLeftButton(exitButton);

                String panelHeaderText = actions().findOneElementsText(panelHeaderPath + "/div[position()=2]");
                panelHeader.setHeaderText(panelHeaderText);

                boolean hasRightButton = actions().elementExistAndVisible(panelHeaderPath + "/div[position()=3]/descendant::button[1]", false, 0);
                if (hasRightButton) {
                    Button menuButton = getButton(panelHeaderPath + "/div[position()=3]/descendant::button[1]");
                    panelHeader.setRightButton(menuButton);
                }

                return panelHeader;
            }
            case "panel" -> {
                PanelHeader panelHeader = new PanelHeader();
                String panelHeaderPath = "//div[contains(@data-qa-id, 'panel-header')]";

                Button leftButton = getButton(panelHeaderPath + " //button[position()=1]");
                panelHeader.setLeftButton(leftButton);

                String panelHeaderText = actions().findOneElementsText(panelHeaderPath + " /div[contains(@class, 'text-title')] ");
                panelHeader.setHeaderText(panelHeaderText);

                boolean hasRightButton = actions().elementExistAndVisible(panelHeaderPath + " //span/button", false, 0);
                if (hasRightButton) {
                    Button rightButton = getButton(panelHeaderPath + " //span/button");
                    panelHeader.setRightButton(rightButton);
                }

                return panelHeader;
            }

            default -> throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    public void clickOnPanel(String destination) {
        actions().makeClick("//div[@data-qa-id='panel'] //div[text()='"+destination+"']");
    }

    public void expandCalendar(String dropDownIdentifier) {
        switch (dropDownIdentifier) {
            case "From" -> actions().makeClick("//label[@data-qa-id='time_interval_from']");
            default -> throw new IllegalStateException("Unknown dropDownIdentifier: " + dropDownIdentifier);
        }
    }

    public void selectCalendarDate(String time) {
        switch (time) {
            case "month" -> actions().makeClick("(//div[@role='menu'] //button //span[@class='block'])[1]");
            case "year" -> actions().makeClick("(//div[@role='menu'] //button //span[@class='block'])[2]");
            default -> actions().makeClick("//div[@role='menu'] //span[text()='"+time+"']");
        }
    }

    /**
     * @param searchPhrase Locating the div holding searchPhrase, and then step up to //label, so that the next method gets the correct dropdownPath.
     */
    public Dropdown getDropdownByName(String searchPhrase) {
        return getDropdownByName(searchPhrase, false);
    }

    public Dropdown getDropdownByName(String searchPhrase, boolean withContent) {
        String dropdownPath = "//form //div[text()='"+searchPhrase+"']/ancestor::label";
        return getDropdown(dropdownPath, withContent);
    }

    public Dropdown getDropdownByName(String searchPhrase, boolean withContent, boolean withDynamicContent) {
        String dropdownPath = "//form //div[text()='"+searchPhrase+"']/ancestor::label";
        return getDropdown(dropdownPath, withContent, withDynamicContent);
    }

    public Dropdown getDropdownByPath(String dropdownPath) {
        return getDropdownByPath(dropdownPath, false);
    }

    public Dropdown getDropdownByPath(String dropdownPath, boolean withContent) {
        return getDropdown(dropdownPath, withContent);
    }

    private Dropdown getDropdown(String dropdownPath, boolean withContent) {
        if (!dropdownPath.contains("label")) {
            throw new IllegalStateException("dropdownPath '"+dropdownPath+"' has to contain 'label'");
        }

        Dropdown dropdown = new Dropdown();

        // Get the header, if present
        boolean hasHeader = actions().elementExistAndVisible(dropdownPath + " //div[@class='q-field__label no-pointer-events absolute ellipsis']", false, 0);
        if (hasHeader) {
            String headerText = actions().findOneElementsText(dropdownPath + " //div[@class='q-field__label no-pointer-events absolute ellipsis']");
            dropdown.setHeader(headerText);
        }

        // todo: Build support for dropdown with multiple content, eg: device/service_message
        String text;
        boolean hasSpan = actions().elementExistAndVisible(dropdownPath + " //span", false, 0);
        if (hasSpan) {
            text = actions().findOneElementsText(dropdownPath + " //span");
        } else {
            boolean hasDropdownValue = actions().elementExistAndVisible(dropdownPath + " //label[@data-qa-id='dropdown_value']", false, 0);

            if (hasDropdownValue) {
                text = actions().findOneElementsText(dropdownPath + " //div[@data-qa-id='dropdown_value']");
            } else {
                boolean hasInputValue = actions().elementExistAndVisible(dropdownPath + " //input", false, 0);
                if (hasInputValue) {
                    text = actions().findOneElementsAttribute(dropdownPath + " //input", "value");
                } else {
                    // todo: alla input har ju parent, även om alla kanske inte har en <div> parent...
                    boolean hasInputParent = actions().elementExistAndVisible( dropdownPath + " //input/parent::div", false, 0);
                    if (hasInputParent) {
                        text = actions().findOneElementsText(dropdownPath + " //input/parent::div");     // create user
                    } else {
                        throw new IllegalArgumentException("I give up...");
                    }
                }
            }
        }
        dropdown.setText(text);

        if (withContent) {
            List<String> expandedDropdownContent = getDropdownContentByPath(dropdownPath, false);
            dropdown.setExpandedDropdownContent(expandedDropdownContent);
        }

        boolean hasFooter = actions().elementExistAndVisible(dropdownPath + " //div[@class='q-field__messages col']", false,0);
        if (hasFooter) {
            String footer = actions().findOneElementsText(dropdownPath + " //div[@class='q-field__messages col']");
            dropdown.setFooter(footer);
        }

        Status status = StatusAssesser.getStateByClassName(actions().findOneElementsAttribute(dropdownPath, "class"));
        dropdown.setStatus(status);

        return dropdown;
    }
    private Dropdown getDropdown(String dropdownPath, boolean withContent, boolean withDynamicContent) {
        if (!dropdownPath.contains("label")) {
            throw new IllegalStateException("dropdownPath '"+dropdownPath+"' has to contain 'label'");
        }

        Dropdown dropdown = new Dropdown();

        // Get the header, if present
        boolean hasHeader = actions().elementExistAndVisible(dropdownPath + " //div[@class='q-field__label no-pointer-events absolute ellipsis']", false, 0);
        if (hasHeader) {
            String headerText = actions().findOneElementsText(dropdownPath + " //div[@class='q-field__label no-pointer-events absolute ellipsis']");
            dropdown.setHeader(headerText);
        }

        // todo: Build support for dropdown with multiple content, eg: device/service_message
        String text;
        boolean hasSpan = actions().elementExistAndVisible(dropdownPath + " //span", false, 0);
        if (hasSpan) {
            text = actions().findOneElementsText(dropdownPath + " //span");
        } else {
            boolean hasDropdownValue = actions().elementExistAndVisible(dropdownPath + " //label[@data-qa-id='dropdown_value']", false, 0);

            if (hasDropdownValue) {
                text = actions().findOneElementsText(dropdownPath + " //div[@data-qa-id='dropdown_value']");
            } else {
                boolean hasInputValue = actions().elementExistAndVisible(dropdownPath + " //input", false, 0);
                if (hasInputValue) {
                    text = actions().findOneElementsAttribute(dropdownPath + " //input", "value");
                } else {
                    // todo: alla input har ju parent, även om alla kanske inte har en <div> parent...
                    boolean hasInputParent = actions().elementExistAndVisible( dropdownPath + " //input/parent::div", false, 0);
                    if (hasInputParent) {
                        text = actions().findOneElementsText(dropdownPath + " //input/parent::div");     // create user
                    } else {
                        throw new IllegalArgumentException("I give up...");
                    }
                }
            }
        }
        dropdown.setText(text);

        if (withContent) {
            List<String> expandedDropdownContent = getDropdownContentByPath(dropdownPath, withDynamicContent);
            dropdown.setExpandedDropdownContent(expandedDropdownContent);
        }

        boolean hasFooter = actions().elementExistAndVisible(dropdownPath + " //div[@class='q-field__messages col']", false,0);
        if (hasFooter) {
            String footer = actions().findOneElementsText(dropdownPath + " //div[@class='q-field__messages col']");
            dropdown.setFooter(footer);
        }

        Status status = StatusAssesser.getStateByClassName(actions().findOneElementsAttribute(dropdownPath, "class"));
        dropdown.setStatus(status);

        return dropdown;
    }

    /**
     * @param dropdownPath to the label we are looking for
     */
    public Map<String, String> getDropdownParts(String dropdownPath) {
        actions().validateXpathLastElement("label", dropdownPath);

        Map<String, String> dropdownParts = new HashMap<>();

        // Save classname so that we can evaluate if active or disabled
        String className = actions().findOneElementsAttribute(dropdownPath, "class");
        dropdownParts.put("className", className);

        // Get the header, if present
        String header = (actions().elementExistAndVisible(dropdownPath + " //div[@class='q-field__label no-pointer-events absolute ellipsis']", false, 0))
                ? actions().findOneElementsText(dropdownPath + " //div[@class='q-field__label no-pointer-events absolute ellipsis']")
                : null;
        dropdownParts.put("header", header);

        String text;
        // todo: Build support for dropdown with multiple content, eg: device/service_message

        boolean hasSpan = actions().elementExistAndVisible(dropdownPath + " //span", false, 0);
        if (hasSpan) {
            text = actions().findOneElementsText(dropdownPath + " //span");
        } else {
            boolean hasDropdownValue = actions().elementExistAndVisible(dropdownPath + " //label[@data-qa-id='dropdown_value']", false, 0);

            if (hasDropdownValue) {
                text = actions().findOneElementsText(dropdownPath + " //div[@data-qa-id='dropdown_value']");
            } else {
                boolean hasInputValue = actions().elementExistAndVisible(dropdownPath + " //input", false, 0);
                if (hasInputValue) {
                    text = actions().findOneElementsAttribute(dropdownPath + " //input", "value");
                } else {
                    boolean hasInputParent = actions().elementExistAndVisible( dropdownPath + " //input/parent::div", false, 0);
                    if (hasInputParent) {
                        text = actions().findOneElementsText(dropdownPath + " //input/parent::div");     // create user
                    } else {
                        throw new IllegalArgumentException("I give up...");
                    }
                }
            }
        }
        dropdownParts.put("text", text);

        // Get the footer, if present
        String footer = actions().elementExistAndVisible(dropdownPath + " //div[@class='q-field__messages col']", false,0)
                ? actions().findOneElementsText(dropdownPath + " //div[@class='q-field__messages col']")
                : null;
        dropdownParts.put("footer", footer);

        return dropdownParts;
    }

    public SearchBox getSearchBox() {
        String searchPath = "//form //label[.//input[contains(@placeholder,'Search...')]]";
        return getSearchBox(searchPath);
    }

    public SearchBox getSearchBox(String searchPath) {
        if (!searchPath.contains("label")) { throw new IllegalStateException("searchPath '"+searchPath+"' has to contain 'label'"); }

        SearchBox searchBox = new SearchBox();

        String searchText = actions().findOneElementsValueAttribute(searchPath + " //input");
        if (searchText.isEmpty()) {
            searchText = actions().findOneElementsAttribute(searchPath + " //input", "placeholder");
        }
        searchBox.setText(searchText);

        Icon icon = completeGetIcon(searchPath + "/div/div/div[2]");
        searchBox.setIcon(icon);
        return searchBox;
    }

    public InputField getInputFieldByHeader(String labelHeaderText) {
        String labelPath = "//label[.//div[text()='"+labelHeaderText+"']]";
        return completeGetInputField(labelPath);
    }

    public InputField getInputFieldByPath(String labelPath) {
        return completeGetInputField(labelPath);
    }

    public InputField completeGetInputField(String labelPath) {
        if (!labelPath.contains("label")) { throw new IllegalStateException("labelPath '"+labelPath+"' has to contain 'label'"); }

        InputField inputField = new InputField();

        String className = actions().findOneElementsAttribute(labelPath, "class");
        Status status = StatusAssesser.getInputFieldStatus(className);
        inputField.setStatus(status);

        // A form with fillable fields contain <input>
        boolean hasInput = actions().elementExistAndVisible(labelPath + " //input", false, 0);

        if (hasInput) {

            // As in FDA.first/last sample
            boolean isReadOnlyInputField = actions().elementExistAndVisible(labelPath + " //input[@readonly]", false, 0);
            if (isReadOnlyInputField) {

                boolean headerExist = actions().elementExistAndVisible(labelPath + " /div/div[1]/div/div[1]", false, 0);
                if (headerExist) {
                    String headerText = actions().findOneElementsText(labelPath + " /div/div[1]/div/div[1]");
                    inputField.setHeader(headerText);
                }

                // Some input fields show value as text in the DOM
                String text = actions().findOneElementsText(labelPath + " //input");

                // If the user has entered a value, the input's 'value' attribute will contain the data.
                if (text.isEmpty()) {
                    text = actions().findOneElementsAttribute(labelPath + " //input", "value");

                    if (text.isEmpty()) {
                        text = actions().findOneElementsAttribute(labelPath + " //input", "placeholder");
                    }
                }
                inputField.setText(text);

                // to be implemented when we find readonly inputfields with unitValue and/or footer.
                String unitValue;
                String footer;

            } else {

                boolean headerExist = actions().elementExistAndVisible(labelPath + " /div/div[1]/div/div[1]", false, 0);
                if (headerExist) {
                    String headerText = actions().findOneElementsText(labelPath + " /div/div[1]/div/div[1]");
                    inputField.setHeader(headerText);
                }

                // Some input fields show value as text in the DOM
                String text = actions().findOneElementsText(labelPath + " //input");

                // If the user has entered a value, the input's 'value' attribute will contain the data.
                if (text.isEmpty()) {
                    text = actions().findOneElementsAttribute(labelPath + " //input", "value");

                    if (text.isEmpty()) {
                        text = actions().findOneElementsAttribute(labelPath + " //input", "placeholder");
                    }
                }
                inputField.setText(text);

                String unitValue = actions().elementExistAndVisible(labelPath + " //input/following-sibling::div[1]", false, 0)
                        ? actions().findOneElementsText(labelPath + " //input/following-sibling::div[1]")
                        : null;
                inputField.setUnit(unitValue);

                boolean footerExist = actions().elementExistAndVisible("(" + labelPath + " //div)[8]", false, 0);
                if (footerExist) {
                    String footer = actions().findOneElementsText("(" + labelPath + " //div)[8]");
                    inputField.setFooter(footer);
                }
            }

            // A "locked" input field /e.g., /company/devices/S50/5307/details
        } else {
            String headerText = actions().findOneElementsText(labelPath + "/div/div/div/div[2]");
            inputField.setHeader(headerText);

            String textValue = actions().findOneElementsText(labelPath + "/div/div/div/div[1]/div");
            inputField.setText(textValue);
        }

        return inputField;
    }

    public Tab getTab(String tabType, String tabPath) {
        actions().validateXpathLastElements(List.of("*", "div"), tabPath);

        Tab tab = new Tab();

        String iconPath;
        String textPath;

        if (tabType.equals("billing_report")) {
            actions().verifyLastNodeInXpath("div", tabPath);

            //div[@role='tab' and .//span[text()=' Devices']]
            iconPath = tabPath + "/div[2]/span";
            textPath = tabPath + "/div[2]/span";


        } else if (tabType.equals("data_report")) {

            int divChildren = actions().countHowManyElements(tabPath + "/div");

            if (divChildren == 2) { // I.e., a disabled tab
                tabPath = tabPath.replace("*", "div");
                iconPath = tabPath + "/div[1]";
                textPath = tabPath + "/div[2]/div";
            } else if (divChildren == 3) {
                tabPath = tabPath.replace("*", "a");
                iconPath = tabPath + "/div[2]";
                textPath = tabPath + "/div[3]/div";
            } else {
                throw new IllegalArgumentException("Unsupported divChildren count: " + divChildren);
            }

        } else if(tabType.equals("scheduled_report")) {
            iconPath = null;
            textPath = tabPath + "/div[2]/div";

        } else if(tabType.equals("message_rule")) {
            iconPath = null;
            textPath = tabPath + "/div[2]/div";

        } else {
            throw new IllegalArgumentException("Unsupported tabType: " + tabType);
        }

        if (tabType.equals("billing_report")
                || tabType.equals("data_report")) {
            //Icon
            Icon tabIcon = completeGetIcon(iconPath);

            // Remove colour and state from Icon, as it's the status of Tab that's vital
            tabIcon.setColour(null);
//            tabIcon.setStatus(null);
            tab.setIcon(tabIcon);

        }

        String tabClassName = actions().findOneElementsAttribute(tabPath, "class");
        Status status = StatusAssesser.getStateByClassName(tabClassName);
        tab.setStatus(status);

        // Text
        String tabText = actions().findOneElementsText(textPath);
        tab.setText(tabText);

        return tab;
    }

    public List<NoticeItem> getDeviceDetailNoticeItems(String noticeItemsPath) {

        List<NoticeItem> noticeItems = new ArrayList<>();

        int noticeItemsCount = actions().countHowManyElements(noticeItemsPath + "/*");

        for (int n = 1; n <= noticeItemsCount; n++) {
            NoticeItem noticeItem = new NoticeItem();
            String noticeItemPath = noticeItemsPath + "/*["+n+"]";

            String tagName = actions().getTagName(noticeItemPath);

            // Keep this as is until I know if there are more types of tags holding noticeItem
            switch (tagName) {
                case "div" -> {
                    Icon leftIcon = completeGetIcon(noticeItemPath + "/div[1]");
                    noticeItem.setLeftIcon(leftIcon);

                    String text = actions().findOneElementsText(noticeItemPath + "/div[2]");
                    noticeItem.setText(text);

                    boolean hasRightButton = actions().elementExistAndVisible(noticeItemPath + "/div[3]/button", false, 0);
                    if (hasRightButton) {
                        Button button = getButton(noticeItemPath + "/div[3]/button");
                        noticeItem.setButton(button);
                    }
                }
                case "a" -> {
                    Icon leftIcon = completeGetIcon(noticeItemPath + "/div[2]");
                    noticeItem.setLeftIcon(leftIcon);

                    String text = actions().findOneElementsText(noticeItemPath + "/div[3]");
                    noticeItem.setText(text);

                    Icon rightIcon = completeGetIcon(noticeItemPath + "/div[4]");
                    noticeItem.setRightIcon(rightIcon);
                }
                default -> throw new IllegalArgumentException("Unsupported tag: " + tagName);
            }
            noticeItems.add(noticeItem);
        }

        return noticeItems;
    }

    public List<NoticeItem> getDeviceSettingsNoticeItems(String noticeItemsPath) {

        List<NoticeItem> noticeItems = new ArrayList<>();

        int noticeItemCount = actions().countHowManyElements(noticeItemsPath + "/*");

        for (int n = 1; n <= noticeItemCount; n++) {
            String noticeItemPath = noticeItemsPath + "/*["+n+"]";

            NoticeItem noticeItem = getNoticeItem(noticeItemPath);
            noticeItems.add(noticeItem);
        }

        return noticeItems;
    }

    /**
     *
     * @param noticeItemPath Last tag must be <div> and contain role='listitem'
     */
    NoticeItem getNoticeItem(String noticeItemPath) {
        NoticeItem noticeItem = new NoticeItem();

        int noticeItemDivCount = actions().countHowManyElements(noticeItemPath + "/div");

        switch (noticeItemDivCount) {
            case 3 -> {
                Icon leftIcon = completeGetIcon(noticeItemPath + "/div[1]");
                noticeItem.setLeftIcon(leftIcon);

                String noticeText = actions().findOneElementsText(noticeItemPath + "/div[2]");
                noticeItem.setText(noticeText);
            }
            case 4 -> {
                Icon leftIcon = completeGetIcon(noticeItemPath + "/div[2]");
                noticeItem.setLeftIcon(leftIcon);

                String noticeText = actions().findOneElementsText(noticeItemPath + "/div[3]");
                noticeItem.setText(noticeText);

                boolean hasRightIcon = actions().elementExistAndVisible(noticeItemPath + "/div/div[4]/i", false, 0);
                if (hasRightIcon) {
                    Icon rightIcon = completeGetIcon(noticeItemPath + "/div/div[4]");
                    noticeItem.setRightIcon(rightIcon);
                }
            }
            default -> throw new IllegalStateException("A NoticeItem shall only have 3 or 4 children <div>, but had " + noticeItemDivCount);
        }

        String className = actions().findOneElementsAttribute(noticeItemPath, "class");

        // Colour
        ColourSchema noticeColour = ColourSchema.getBackgroundColourFromClassName(className);
        noticeItem.setNoticeColour(noticeColour);

        // Status
        Status status = StatusAssesser.getStatusByClassName(className);
        noticeItem.setStatus(status);

        return noticeItem;
    }

    /**
     * Narrow preface, such as
     *      /project/10523/devices/POINT/101695/settings
     *      /devices/S50/5307/details
     * Extended preface such as
     *      /company/devices/COMPACT/79049/details
     *      /company/projects/5657/details
     *      /project/10523/scheduled_reports/226/details
     */
    Preface getPreface() {
        String prefacePath = "//div[@data-qa-id='preface']";

        int divChildren = actions().countHowManyElements(prefacePath + "/div");

        return (divChildren == 1)
                ? getNarrowPreface(prefacePath + "/div[1]")
                : getLargePreface(prefacePath);
    }

    private Preface getNarrowPreface(String prefacePath) {
        Preface preface = new Preface();

        Icon leftIcon = completeGetIcon(prefacePath + "/div[1]");
        preface.setLeftIcon(leftIcon);

        String text = actions().findOneElementsText(prefacePath + "/div[2] //span");
        preface.setText(text);

        return preface;
    }

    /**
     * An extended Preface can have expansionIcon, ToggleField or none
     */
    private Preface getLargePreface(String prefacePath) {
        Preface preface = new Preface();

        Icon leftIcon = completeGetIcon(prefacePath + "/div[1]");
        preface.setLeftIcon(leftIcon);

        String text = actions().findOneElementsText(prefacePath + "/div[2] //span");
        preface.setText(text);

        boolean hasSubTexts = actions().elementExistAndVisible(prefacePath + "/div[2] //div[@class='q-pb-xs']", false, 0);
        if (hasSubTexts) {

            List<String> subTexts = new ArrayList<>();

            boolean hasHeadline = actions().elementExistAndVisible(prefacePath + "/div[2]/div/div/div/div[2]/div[1]/span", false, 0);
            if (hasHeadline) {
                String headlineText = actions().findOneElementsText(prefacePath + "/div[2]/div/div/div/div[2]/div[1]/span");
                subTexts.add(headlineText);
            }

            // NB. A device wo description has empty string at the first <div> found by this xpath
            List<String> subHeadlineTexts = actions().findManyElementsTexts(prefacePath + "/div[2]/div/div/div/div[2]/div[2]/div[contains(text(),'')]");
            subTexts.addAll(subHeadlineTexts);

            // Only look for expansion icon that is not hidden.
            // Preface with toggle (eg project details) have hidde expansion icon.
            boolean hasExpansionIcon = actions().elementExistAndVisible(prefacePath + "/div[2] //div[@role='button'] //i/parent::div[not(contains(@class,'hidden'))]", false, 0);
            if (hasExpansionIcon) {
//                Icon expansionIcon = completeGetIcon(prefacePath + "/div[2] //div[@role='button'] //i/parent::div");
                Icon expansionIcon = getIcon(prefacePath + "/div[2] //div[@role='button'] //i");
                preface.setExpansionIcon(expansionIcon);

                // An expanded icon ought to have text
                if (expansionIcon.getType().equals(EXPANDED)) {
                    String expandedText = actions().findOneElementsText(prefacePath + "/div/div/div[2]");
                    subTexts.add(expandedText);
                }
            }

            preface.setSubTexts(subTexts);
        }

        boolean hasToggle = actions().elementExistAndVisible(prefacePath + "/div[3] //div[@role='switch']", false, 0);
        if (hasToggle) {
            ToggleField toggleField = getToggle("right", prefacePath);
            preface.setToggleField(toggleField);
        }

        return preface;
    }


    /**
     * DataViewStatus is the top item in the intervals_chart and transient_table views.
     */
    public DataViewStatus getDataViewStatus() {
        DataViewStatus dataViewStatus = new DataViewStatus();

        ChartSectionHeader chartSectionHeader = getChartSectionHeader("//div[@data-qa-id='data_view_status']");
        dataViewStatus.setChartSectionHeader(chartSectionHeader);

        // If header is expanded, then we can get data about which measuring_points that have data
        if (chartSectionHeader.getExpansionIcon().getType().equals(EXPANDED)) {

            List<String> dataPresentList = actions().findManyElementsTexts(" //div[@data-qa-id='data_view_status'] //div[text()='Data present']/following-sibling::div //span");
            dataViewStatus.setDataPresentList(dataPresentList);

            List<String> noDataPresentList = actions().findManyElementsTexts(" //div[@data-qa-id='data_view_status'] //div[text()='No data present']/following-sibling::div //span");
            dataViewStatus.setDataNotPresentList(noDataPresentList);
        }

        return dataViewStatus;
    }

    /**
     * NB. Expansion icon is non-clickable as the entire header is located in a Button, and it's the button that's clickable.
     */
    public ChartSectionHeader getChartSectionHeader(String chartSectionPath) {
        ChartSectionHeader chartSectionHeader = new ChartSectionHeader();

        String chartSectionHeaderPath = chartSectionPath + " //div[@role='button']";

        Icon leftIcon = completeGetIcon("(" + chartSectionHeaderPath + " //i)[position()=1]/parent::div");
        chartSectionHeader.setLeftIcon(leftIcon);

        String mainText = actions().findOneElementsText("(" + chartSectionHeaderPath + " //span)[position()=1]");
        chartSectionHeader.setMainText(mainText);

        // Then decide the attribute to use that describes the icon's state
        Icon expansionIcon = completeGetIcon("(" + chartSectionHeaderPath + " //i)[position()=2]/parent::div");
        chartSectionHeader.setExpansionIcon(expansionIcon);

        if (expansionIcon.getType().equals(COLLAPSED)) {
            Banner banner = getBanner("same", "(" + chartSectionHeaderPath + " //span)[position()=3]");
            chartSectionHeader.setMpDataInformation(banner);
        } else {
            // todo: expand and capture the dataPresent and/or noDataPresent banners
        }

        return chartSectionHeader;
    }

    /**
     * For banners that have not colour programmatically added.
     * Until now only in Aside.
     */
    public List<Banner> getAsideBanners(String bannersPath) {
        List<Banner> banners = new ArrayList<>();

        int bannerCount = actions().countHowManyElements(bannersPath + "/div");

        for (int b = 1; b <= bannerCount; b++) {
            String bannerPath = bannersPath + "/div["+b+"]";
            Banner banner = getAsideBanner(bannerPath);

            banners.add(banner);
        }

        return banners;
    }

    public Banner getAsideBanner(String bannerPath) {
        Banner banner = new Banner();

        String bannerText = actions().findOneElementsText(bannerPath);
        banner.setText(bannerText);

        String bannerClassName = actions().findOneElementsAttribute(bannerPath, "class");
        ColourSchema bannerColour = ColourSchema.getBackgroundColourFromClassName(bannerClassName);
        banner.setColour(bannerColour);

        return banner;
    }


    /**
     * The bg-information is in the <div> parent.
     * Or at least I thought so. It seems that bg-light-red for RegressionAnalysis is added programmatically, and not set in the DOM.
     * So color is not to be captured until there is a workaround.
     */
    public Banner getBanner(String colourIn, String bannerPath) {
        Banner banner = new Banner();

        String bannerText = actions().findOneElementsText(bannerPath);
        banner.setText(bannerText);

        return banner;
    }

    public Banner getBannerDropdown(String bannerPath) {
        Banner banner = new Banner();

        // First get the key
        String bannerText = actions().findOneElementsText(bannerPath + "/div");
        banner.setText(bannerText);

        // Then get the dropdown
        Dropdown bannerDropdown = getDropdownByPath(bannerPath + " //label");
        banner.setDropdown(bannerDropdown);

        return banner;
    }


    // todo: flytta alla ChartSectionHeader till DataReportPO
    // todo: merge with getExpansionHeaderVibrationReport
    /**
     * NB. Expansion icon is non-clickable as the entire header is located in a Button, and it's the button that's clickable.
     */
    public ChartSectionHeader getExpansionHeaderIntervalsChartMpData(String chartSectionPath) {
        ChartSectionHeader chartSectionHeader = new ChartSectionHeader();

        // Set left icon
        Icon leftIcon = getChartSectionIcon("leftIcon", chartSectionPath);
        chartSectionHeader.setLeftIcon(leftIcon);

        // Set text
        String mainText = getChartSectionHeaderMainText(chartSectionPath);
        chartSectionHeader.setMainText(mainText);

        // Set download icon
        Icon downloadIcon = getChartSectionIcon("downloadIcon", chartSectionPath);
        chartSectionHeader.setDownloadIcon(downloadIcon);

        // Set menu icon
        Icon menuIcon = getChartSectionIcon("menuIcon", chartSectionPath);
        chartSectionHeader.setMenuIcon(menuIcon);

        // Set expansion icon, aka right icon
        Icon expansionIcon = completeGetIcon("(" + chartSectionPath + "  //i)[position()=4]/parent::div");
        chartSectionHeader.setExpansionIcon(expansionIcon);

        return chartSectionHeader;
    }
    // todo: merge with getExpansionHeaderIntervalsChartMpData
    public ChartSectionHeader getExpansionHeaderVibrationReport(String chartSectionPath) {
        ChartSectionHeader chartSectionHeader = new ChartSectionHeader();

        // Set left icon
        Icon leftIcon = getChartSectionIcon("leftIcon", chartSectionPath);
        chartSectionHeader.setLeftIcon(leftIcon);

        // Set text
        String mainText = getChartSectionHeaderMainText(chartSectionPath);
        chartSectionHeader.setMainText(mainText);

        // Set expansion icon, aka right icon
        Icon expansionIcon = completeGetIcon("(" + chartSectionPath + " //i)[position()=2]/parent::div");
        chartSectionHeader.setExpansionIcon(expansionIcon);

        return chartSectionHeader;
    }

    public ChartSectionHeader getExpansionHeaderOctaveGraph(String octaveGraphPath) {
        ChartSectionHeader chartSectionHeader = new ChartSectionHeader();

        // Set left icon
        Icon leftIcon = getChartSectionIcon("leftIcon", octaveGraphPath);
        chartSectionHeader.setLeftIcon(leftIcon);

        // Set text
        String mainText = getChartSectionHeaderMainText(octaveGraphPath);
        chartSectionHeader.setMainText(mainText);

        // Set expansion icon, aka right icon
        Icon expansionIcon = completeGetIcon("(" + octaveGraphPath + " //i)[position()=2]/parent::div");
        chartSectionHeader.setExpansionIcon(expansionIcon);

        // Some elements are only visible if octaveSection is expanded
//        if (expansionIcon.getType().equals(EXPANDED)) {
//
//        }

        return chartSectionHeader;
    }

    /**
     * NB. Expansion icon is non-clickable as the entire header is located in a Button, and it's the button that's clickable.
     */
    public ChartSectionHeader getExpansionHeaderFreqData(String chartSectionPath) {
        ChartSectionHeader chartSectionHeader = new ChartSectionHeader();

        // Set left icon
        Icon leftIcon = getChartSectionIcon("leftIcon", chartSectionPath);
        chartSectionHeader.setLeftIcon(leftIcon);

        // Set text
        String mainText = getChartSectionHeaderMainText(chartSectionPath);
        chartSectionHeader.setMainText(mainText);

        // Set expansion icon, aka right icon
        Icon expansionIcon = completeGetIcon("(" + chartSectionPath + "  //i)[position()=4]/parent::div");
        chartSectionHeader.setExpansionIcon(expansionIcon);

        // Some elements are only visible if freqDataChartSection is expanded
        if (expansionIcon.getType().equals(EXPANDED)) {
            Dropdown benchmarkSelector = getDropdownByPath(chartSectionPath + " //label");

            chartSectionHeader.setFrequencyDataBenchmarkSelector(benchmarkSelector);

            Icon settingsIcon = getChartSectionIcon("settingsIcon", chartSectionPath);
            chartSectionHeader.setSettingsIcon(settingsIcon);
        }

        return chartSectionHeader;
    }

    public ChartSectionHeader getTransientAnalysisExpansionHeader(String chartSectionPath) {
        ChartSectionHeader chartSectionHeader = new ChartSectionHeader();

        // Set status, i.e., if the header is CLICKABLE
        Status status = StatusAssesser.getStateByClassName(actions().findOneElementsAttribute(chartSectionPath, "class"));
        chartSectionHeader.setStatus(status);

        // Set left icon
        Icon leftIcon = getChartSectionIcon("leftIcon", chartSectionPath);
        chartSectionHeader.setLeftIcon(leftIcon);

        // Set text
        String mainText = getChartSectionHeaderMainText(chartSectionPath);
        chartSectionHeader.setMainText(mainText);

        // Set filter icon
        Icon filterIcon = getChartSectionIcon("filterIcon", chartSectionPath);
        chartSectionHeader.setFilterIcon(filterIcon);

        // Set download icon
        Icon downloadIcon = getChartSectionIcon("DownloadIcon", chartSectionPath);
        chartSectionHeader.setDownloadIcon(downloadIcon);

        // Set expansion icon, aka right icon
        Icon expansionIcon = completeGetIcon("(" + chartSectionPath + "  //i)[position()=4]/parent::div");
        chartSectionHeader.setExpansionIcon(expansionIcon);

        return chartSectionHeader;
    }

    public ChartSectionHeader getRegressionAnalysisChartSectionHeader(String chartSectionPath) {
        ChartSectionHeader chartSectionHeader = new ChartSectionHeader();

        // Set left icon
        Icon leftIcon = getChartSectionIcon("leftIcon", chartSectionPath);
        chartSectionHeader.setLeftIcon(leftIcon);

        // Set text
        String mainText = actions().findOneElementsText(chartSectionPath + "/div[1]/div[2]/div/div[1]");
        chartSectionHeader.setMainText(mainText);

        String subText = getExpansionHeaderSubText(chartSectionPath);
        chartSectionHeader.setSubText(subText);

        // Set filter icon
        Icon filterIcon = getChartSectionIcon("filterIcon", chartSectionPath);
        chartSectionHeader.setFilterIcon(filterIcon);

        // Set download icon
        Icon calculationsIcon = getChartSectionIcon("calculationsIcon", chartSectionPath);
        chartSectionHeader.setCalculationsIcon(calculationsIcon);

        return chartSectionHeader;
    }

    public String getChartSectionHeaderMainText(String chartSectionPath) {
        String mainTextPath = "(" + chartSectionPath + " //div)[5]";
        return actions().findOneElementsText(mainTextPath);
    }

    public String getExpansionHeaderSubText(String chartSectionPath) {
        String subTextPath = chartSectionPath + "/div[1]/div[2]/div/div[2]";
        return actions().findOneElementsText(subTextPath);
    }

    private Icon getChartSectionIcon(String headerPart, String chartSectionPath) {
        String iconPath = switch (headerPart) {
            case "leftIcon" -> "(" + chartSectionPath + " //i)[position()=1]/parent::div";
            case "settingsIcon" -> chartSectionPath + " //span/i[text()='settings']/ancestor::button";
            case "downloadIcon" -> chartSectionPath + " //i[text()='download']/ancestor::button";   // IntervalChart
            case "filterIcon" -> chartSectionPath + " //i[text()='tune']/ancestor::button";
            case "DownloadIcon" -> chartSectionPath + " //i[contains(@class,'Download')]/ancestor::button";   // Transient
            case "menuIcon" -> chartSectionPath + " //i[text()='more_vert']/ancestor::button";
            case "calculationsIcon" -> chartSectionPath + " //i[text()='calculate']/ancestor::button";
            default -> throw new IllegalStateException("Unknown part: " + headerPart);
        };

        return completeGetIcon(iconPath);
    }

    /**
     * Packaging method of web elements like the summaryPanel in /project/id/blasts/id/details, or in expanded BlastDetails.CalculatedValues.
     */
    public Map<String, String> getSummaryMap(List<String> fieldHeaders) {
        Map<String, String> summaryMap = new HashMap<>();

        // Depending on passed or future Blast, one header is dynamic
        for (String header : fieldHeaders) {
            String fieldHeaderPath = "(//form //label[.//div[text()='" + header + "']] //div)[5]";

            boolean requiresPresenceCheck =
                    "Blasting occurred".equals(header)
                            || "Blasting planned".equals(header)
                            || "Guide value (V10)".equals(header)
                            || "Blasting planned".equals(header)
                            || "Uncorrected velocity".equals(header)
                            || "Alarm".equals(header)
                            || "Alert".equals(header);

            boolean isVisible = requiresPresenceCheck
                    ? actions().elementExistAndVisible(fieldHeaderPath, false, 0)
                    : true;

            if (isVisible) {
                summaryMap.put(header, actions().findOneElementsText(fieldHeaderPath));
            }
        }
        return summaryMap;
    }

    public SettingsItem getSettingsItemByPath(String settingsItemTextPath) {
        return getSettingsItem(settingsItemTextPath, false);
    }

    public SettingsItem getSettingsItemByPath(String settingsItemTextPath, boolean hasExpansionIcon) {
        return getSettingsItem(settingsItemTextPath, hasExpansionIcon);
    }

    public SettingsItem getSettingsItemByDataQaId(String settingsItemDataQaId) {
        // "Calculated values" and "Advanced settings" are SettingsItems with dropdown values.
        boolean hasExpansionIcon = settingsItemDataQaId.contains("Calculated values")
                || settingsItemDataQaId.contains("Advanced settings");

        String settingsItemPath = (hasExpansionIcon)
                ? "//form //div[@role='button'][.//div[text()='" + settingsItemDataQaId + "']]/parent::div"
                : "//*[contains(@data-qa-id,'"+settingsItemDataQaId+"')]";

        return getSettingsItem(settingsItemPath, hasExpansionIcon);
    }

    /**
     * A SettingsItem that can be expanded. E.g.,
     * BlastDetails.CalculatedValues
     * MeasuringPoint.Settings.AdvancedSettings
     */
    private SettingsItem getSettingsItem(String settingsItemPath, boolean hasExpansionIcon) {
        SettingsItem settingsItem = new SettingsItem();

        Icon leftIcon = completeGetIcon("(" + settingsItemPath + " //i)[1]/parent::div");
        settingsItem.setLeftIcon(leftIcon);

        String mainText = actions().findOneElementsText(settingsItemPath + " //div[@class='q-item__label']");
        settingsItem.setMainText(mainText);

        String subText = actions().findOneElementsText(settingsItemPath + " //div[@class='q-item__label q-item__label--caption text-caption']");
        settingsItem.setSubText(subText);

        if (hasExpansionIcon) {
            Icon expansionIcon = completeGetIcon("(" + settingsItemPath + " //i)[2]/parent::div");
            settingsItem.setExpansionIcon(expansionIcon);
        }

        return settingsItem;
    }

    public ToggleField getFormToggleByName(String textLocated, String toggleText) {
        String togglePath = "//form //label[.//div[contains(text(), '"+toggleText+"')]]";
        return getToggleFields(textLocated, togglePath).getFirst();
    }

    public ToggleField getAriaLabelToggle(String textLocated, String toggleText) {
        String togglePath = "//form //div[@aria-label='" + toggleText + "']/parent::div";
        return getToggleFields(textLocated, togglePath).getFirst();
    }

    /**
     * Gateway-methods for the two different types of toggles.
     */
    public ToggleField getToggle(String textLocated, String togglePath) {
        return getToggleFields(textLocated, togglePath).getFirst();
    }

    /**
     * @param textLocated Where the toggle text, if any, is located in regard to the toggle.
     * @param toggleFieldPath End with <label> or <div>
     */
    // todo: kan det vara så att det är bara //label som har headertext?
    public ToggleField completeGetToggleField(String textLocated, String toggleFieldPath) {
        actions().validateXpathLastElements(List.of("div", "label"), toggleFieldPath);

        if (!List.of("left", "right", "none").contains(textLocated)) {
            throw new IllegalArgumentException("Only 'left', 'right' or 'none' are acceptable as textLocated: " + textLocated);
        }

        ToggleField toggleField = new ToggleField();
        System.out.println("toggleFieldPath: " + toggleFieldPath);
        String tagName = actions().getTagName(toggleFieldPath);

        if (tagName.equals("label")) {
            // get header
            // at least this is the way to find header for Calculated statistics on C50 mon.settings
            String headerText = actions().findOneElementsText(toggleFieldPath + "/div/div/div/div[1]");
            toggleField.setHeaderText(headerText);

            //form //label[.//div[text()='Calculated statistics']] //div[@role='switch']
            ToggleField.Toggle toggle = completeGetToggle(toggleFieldPath + "//div[@role='switch']");
            toggleField.setToggle(toggle);

            if (textLocated.equals("none")) {
                return toggleField;
            }

            // todo: finns det ens en toggleLabel som har texten till höger?
            String textPath = switch (textLocated) {
//                case "right" -> toggleFieldPath + "...";
                case "left" -> toggleFieldPath + "/div/div/div/div[2]/div[1]";
                default -> throw new IllegalArgumentException("Only '' or '' are acceptable as textLocated: " + textLocated);
            };

            String text = actions().findOneElementsText(textPath);
            toggleField.setSideText(text);

        } else if (tagName.equals("div")) {
            ToggleField.Toggle toggle = completeGetToggle(toggleFieldPath);
            toggleField.setToggle(toggle);

            if (textLocated.equals("none")) {
                return toggleField;
            }

            // todo: finns det ens en toggleDiv som har texten till vänster?
            String textPath = switch (textLocated) {
                case "right" -> toggleFieldPath + "/div/following-sibling::div";
                case "left" -> "(" + toggleFieldPath + " //div)[last()]";
                default -> throw new IllegalArgumentException("Only '' or '' are acceptable as textLocated: " + textLocated);
            };

            String text = actions().findOneElementsText(textPath);
            toggleField.setSideText(text);

        }

        return toggleField;
    }

    /**
     * @param togglePath xPath ending with //div[@role='switch' and @aria-checked]
     */
    private ToggleField.Toggle completeGetToggle(String togglePath) {
        actions().validateXpathLastElement("div", togglePath);

        ToggleField.Toggle toggle = new ToggleField.Toggle();

        Boolean state = actions().findOneElementsAttribute(togglePath, "aria-checked").equals("true");
        toggle.setState(state);

        return toggle;
    }

    /**
     * @param toggleFieldPath ends with <label>
     */
    public ToggleField getC50CalculatedStatisticsToggleField(String toggleFieldPath) {
        actions().validateXpathLastElement("label", toggleFieldPath);

        ToggleField toggleField = new ToggleField();

        String headerText = actions().findOneElementsText(toggleFieldPath + " //div[contains(@class,'q-field__label')]");
        toggleField.setHeaderText(headerText);

        String sideText = actions().findOneElementsText(toggleFieldPath + " //div[contains(@class,'q-field__label')]" + "/following-sibling::div/div");
        toggleField.setSideText(sideText);

        ToggleField.Toggle toggle = completeGetToggle(toggleFieldPath + " //div[@role='switch' and @aria-checked]");
        toggleField.setToggle(toggle);

        String className = actions().findOneElementsAttribute(toggleFieldPath, "class");
        Status status = StatusAssesser.getToggleStatus(className);
        toggleField.setStatus(status);   // todo; denna blir fel: "status": "NON_CLICKABLE"

        return toggleField;
    }



    // todo: gör om hela grejen, så att botten-metoden returnerar Toggle, sen en for-loop i glue om man vill ha flera Toggles
    /**
     * @textLocated There are two types of toggles.
     * Those with text to the right of toggle have text in a '//div' nested within '//div[@role='switch']'.
     * @togglesPath Should end with '//div[@role='switch']'
     */
    public List<ToggleField> getToggleFields(String textLocated, String togglesPath) {
        if (textLocated.equals("left") || textLocated.equals("right")) {

            List<ToggleField> toggleFields = new ArrayList<>();

            int togglesCount = actions().countHowManyElements(togglesPath);

            for (int t = 1; t <= togglesCount; t++) {
                ToggleField toggleField = new ToggleField();

                String togglePath = "(" + togglesPath + ")["+t+"]";

                Boolean state = actions().findOneElementsAttribute(togglePath + " //div[@role='switch']", "aria-checked").equals("true");
                toggleField.setState(state);

                String textPath = ("right".equals(textLocated))
                        ? togglePath + " //div[@role='switch']/div/following-sibling::div"
                        : "(" + togglePath + " //div)[last()]";

                String text = actions().findOneElementsText(textPath);
                toggleField.setSideText(text);

                toggleFields.add(toggleField);
            }
            return toggleFields;
        } else {
            throw new IllegalArgumentException("Only 'left', 'right' are acceptable as textLocated: " + textLocated);
        }
    }

    public ToggleField getToggleWithoutText(String togglePath) {
        ToggleField toggleField = new ToggleField();

        Boolean state = actions().findOneElementsAttribute(togglePath, "aria-checked").equals("true");
        toggleField.setState(state);

        return toggleField;
    }

    /**
     * Not all that looks like a QFieldset-FieldWrapper is a QFieldset-FieldWrapper.
     */
    public FieldWrapper getFieldWrapperCommonPartsByHeader(String fieldWrapperHeader) {

        if (fieldWrapperHeader.contains("Client+") || fieldWrapperHeader.contains("Client") || fieldWrapperHeader.contains("Blaster")) {
            String fieldWrapperPath = "//form //div[@class='q-mb-md' and .//div[text()='"+fieldWrapperHeader+"']]";
            return getListItemFieldWrapperCommonParts(fieldWrapperPath);

        } else {

            String fieldWrapperPath = "//form //div[contains(@class,'q-fieldset') and .//div[contains(text(),'"+fieldWrapperHeader+"')]]";
            return getQFieldsetFieldWrapperCommonParts(fieldWrapperPath);
        }
    }

    public FieldWrapper getFieldWrapperByPath(String fieldWrapperPath) {
        FieldWrapper fieldWrapper = new FieldWrapper();

        Icon icon = completeGetIcon(fieldWrapperPath + " //i/parent::div");
        fieldWrapper.setLeftIcon(icon);

        String header = actions().findOneElementsText(fieldWrapperPath + "/div/div/div/div");
        fieldWrapper.setHeader(header);

        return fieldWrapper;
    }



    /**
     * @return The common parts of a ListItem, ie the icon and header
     */
    private FieldWrapper getListItemFieldWrapperCommonParts(String fieldWrapperPath) {
        FieldWrapper fieldWrapper = new FieldWrapper();

        fieldWrapperPath = "(" + fieldWrapperPath + "/div)[1]";

        Icon icon = completeGetIcon(fieldWrapperPath + " //i/parent::div");
        fieldWrapper.setLeftIcon(icon);

        String header = actions().findOneElementsText("(" + fieldWrapperPath + " //div)[last()]");
        fieldWrapper.setHeader(header);

        return fieldWrapper;
    }

    /**
     * @return The common parts of a Fieldset, ie the icon and header
     */
    private FieldWrapper getQFieldsetFieldWrapperCommonParts(String fieldWrapperPath) {
        FieldWrapper fieldWrapper = new FieldWrapper();

        Icon icon = completeGetIcon(fieldWrapperPath + " //i/parent::div");

        fieldWrapper.setLeftIcon(icon);

        String header = actions().findOneElementsText(fieldWrapperPath + " //div[contains(@class,'text-body2')]");
        fieldWrapper.setHeader(header);

        return fieldWrapper;
    }

    public Button getButtonByText(String buttonText) {
        String buttonPath = "//button[.//*[contains(text(),'" + buttonText + "')]]";
        return getButton(buttonPath);
    }

    /**
     * @param buttonPath Last element in xpath have to be //button or //a.
     *                   //a is built like a button but has link to new page.
     */
    public Button getButton(String buttonPath) {
//        actions().validateXpathLastElements(List.of("button", "a"), buttonPath);

        // todo: denna är fel
//        boolean buttonExistInDOM = actions().elementExistAndVisible(buttonPath, false, 0);
//
//        // NB. Some elements have this attribute, some is Vue.hidden and cannot be identified by attribute
//        boolean isHidden = buttonExistInDOM
//                && actions().findOneElementsAttribute(buttonPath, "style").contains("display: none;");
//
//        if (isHidden) {
//            return null;
//        }

        Button button = new Button();

        // A button consist of either an icon (colour+status) or text+status+colour
        boolean hasIcon = actions().elementExistAndVisible(buttonPath + " //i", false, 0);
        if (hasIcon) {
            Icon icon = getIcon(buttonPath + " //i");
            button.setIcon(icon);
        }

        Status status = getElementStatusByClassName(buttonPath);
        button.setStatus(status);

        ColourSchema colour = getElementBackgroundColourByClassName(buttonPath);
        button.setBackgroundColour(colour);

        // todo: har alla button texten i en span/div som ligger direkt under button?
        // Nej, timeslot buttons har button/span[2]
        // alla buttons har inte text
        // en del button är klickbar icon

        // Buttons with <span> has text "inside" button
        boolean hasTextInSpanDiv = actions().elementExistAndVisible(buttonPath + "/span[last()]/div", false, 0);
        if (hasTextInSpanDiv) {
            String text = actions().findOneElementsText(buttonPath + "/span[last()]/div");
            if (!text.isEmpty()) {
                button.setText(text);
            }
        } else {
            boolean hasTextInSecondSpanSpan = actions().elementExistAndVisible(buttonPath + "/span[2]/span", false, 0);
            if (hasTextInSecondSpanSpan) {
                String text = actions().findOneElementsText(buttonPath + "/span[last()]");
                if (!text.isEmpty()) {
                    button.setText(text);
                }
            } else {
                boolean hasTextInSecondSpan = actions().elementExistAndVisible(buttonPath + "/span[2]", false, 0);
                if (hasTextInSecondSpan) {
                    String text = actions().findOneElementsText(buttonPath + "/span[2]");
                    if (!text.isEmpty()) {
                        button.setText(text);
                    }
                }
            }
        }
        return button;
    }

    /**
     * Custom made method for DeviceDetailsButtons.
     * Mtd might work well for other Buttons with text not in <span>.
     */
    public Button getButtonWithTextToTheRight(String buttonPath) {
        System.out.println("buttonPath: " + buttonPath);
        boolean buttonExistInDOM = actions().elementExistAndVisible(buttonPath, false, 0);
        boolean isHidden = buttonExistInDOM
                && actions().findOneElementsAttribute(buttonPath, "style").contains("display: none;");

        if (isHidden) {
            return null;
        }

        Button button = new Button();

        // A button consist of either an icon (colour+status) or text+status+colour
        boolean hasIcon = actions().elementExistAndVisible(buttonPath + " //i", false, 0);
        if (hasIcon) {
            Icon icon = getIcon(buttonPath + " //i");
            button.setIcon(icon);
        }

        Status status = getElementStatusByClassName(buttonPath + "/parent::*");
        button.setStatus(status);

        ColourSchema colour = getElementBackgroundColourByClassName(buttonPath + "/parent::*");
        button.setBackgroundColour(colour);

        String tag = actions().getTagName(buttonPath);

        if (tag.equals("a")) {
            String textOnTheSide = actions().findOneElementsText(buttonPath + " //div[last()]/div");
            button.setText(textOnTheSide);
        } else if (tag.equals("div")) {
            String textOnTheSide = actions().findOneElementsText(buttonPath + "/following-sibling::div/div");
            button.setText(textOnTheSide);

        } else {
            throw new IllegalStateException("This method is only adapted for <a> or <div>, not: " + tag);
        }

        return button;
    }

    /**
     * A device button can be of three kinds:
     * - <a> clickble
     * - <div> unclickable
     * - <div> clickable
     */
    public Button getDeviceDetailsButton(String buttonPath) {
        Button button = new Button();

        Status status = getElementStatusByClassName(buttonPath);
        button.setStatus(status);

        if (status.equals(CLICKABLE)) {
            Icon icon = getIcon(buttonPath + "/div[2]/i");
            button.setIcon(icon);

            String text = actions().findOneElementsText(buttonPath + "/div[3]");
            button.setText(text);

        } else {
            Icon icon = getIcon(buttonPath + "/div[1]/i");
            button.setIcon(icon);

            String text = actions().findOneElementsText(buttonPath + "/div[2]");
            button.setText(text);

        }

        ColourSchema colour = getElementBackgroundColourByClassName(buttonPath);
        button.setBackgroundColour(colour);

        return button;
    }

    public List<PanelListItem> getPanelListItems(boolean withLeftIcon, String itemsPath) {
        List<PanelListItem> items = new ArrayList<>();

        int itemsCount = actions().countHowManyElements(itemsPath);

        for (int i = 1; i <= itemsCount; i++) {
            PanelListItem mpItem = getListItem(withLeftIcon, "(" + itemsPath + ")["+i+"]");
            items.add(mpItem);
        }
        return items;
    }

    private PanelListItem getListItem(boolean withLeftIcon, String itemPath) {
        PanelListItem item = new PanelListItem();

        if (withLeftIcon) {
            Icon leftIcon = completeGetIcon(itemPath + "/div");
            item.setLeftIcon(leftIcon);
        }

        String mainText = actions().findOneElementsText(itemPath + " //div[contains(@class,'text-body1')]");
        item.setMainText(mainText);

        boolean hasSubText = actions().elementExistAndVisible(itemPath + " //div[contains(@class,'text-subtitle2')]", false, 0);
        if (hasSubText) {
            String subText = actions().findOneElementsText(itemPath + " //div[contains(@class,'text-subtitle2')]");
            item.setSubText(subText);
        }

        ToggleField toggleField = getToggleWithoutText(itemPath + " //div[@role='switch']");
        item.setToggleField(toggleField);

        return item;
    }

    public Table getFieldWrapperTable(String fieldWrapperHeader) {
        if (fieldWrapperHeader.contains("Client+") || fieldWrapperHeader.contains("Client") || fieldWrapperHeader.contains("Blaster")) {
            Table table = new Table();

            String tablePath = "//form //div[@class='q-mb-md' and .//div[text()='"+fieldWrapperHeader+"']] //table";

            int columnCount = actions().countHowManyElements(tablePath + " //th");

            // Get header row
            TableRow headerRow = getFieldWrapperTableHeaderRow(columnCount, tablePath);
            table.setHeader(headerRow);

            // Get data rows
            List<TableRow> tableRows = getFieldWrapperTableRows(columnCount, tablePath + " //tbody");
            table.setContent(tableRows);

            return table;

        } else {  // It is possible there exist FieldWrapper.QFieldSet-tables, but I've not yet seen any.
            return null;
        }
    }

    private List<TableRow> getFieldWrapperTableRows(int columnCount, String tablePath) {
        List<TableRow> tableRows = new ArrayList<>();
        String rowsPath = tablePath + "/tr";

        int rowCount = actions().countHowManyElements(rowsPath);

        // for each row
        for (int r = 1; r <= rowCount; r++) {
            String rowPath = "(" + rowsPath + ")["+r+"]";

            TableRow tableRow = getCheckboxedTableRow(columnCount, rowPath);
            tableRows.add(tableRow);
        }

        return tableRows;
    }

    private TableRow getCheckboxedTableRow(int columns, String rowPath) {
        TableRow row = new TableRow();

        // for each column
        for (int c = 1; c <= columns; c++) {
            String cellPath = "(" + rowPath + " //td)["+c+"]";

            // First row is checkbox    // todo: likheter med Medium/full aside?
            if (c == 1) {
                Icon checkbox = completeGetIcon(cellPath + "/div");
                row.addContent(checkbox);
            } else {
                String cellValue = actions().findOneElementsText(cellPath);
                row.addContent(cellValue);
            }
        }
        return row;
    }


    /**
     * Atm there is only one use of this method.
     * .../project/id/blasts/id/details, the second panel with all measuring points.
     */
    public Table getBlastDetailsMeasuringPointsTable() {
        Table table = new Table();

        String containerPath = "//div[contains(@class,'table__container')]";

        int columnCount = actions().countHowManyElements(containerPath + " //th");

        // Get header row
        TableRow headerRow = getBlastDetailsMeasuringPointHeaderRow(columnCount, containerPath);
        table.setHeader(headerRow);

        // Get data rows
        String tableBodyPath = containerPath + " //tbody[@class='q-virtual-scroll__content']";
        List<TableRow> tableRows = getBlastDetailsMeasuringPointsTableRows(columnCount, tableBodyPath);

        table.setContent(tableRows);

        return table;
    }

    private List<TableRow> getBlastDetailsMeasuringPointsTableRows(int columns, String tablePath) {
        List<TableRow> tableRows = new ArrayList<>();
        String rowsPath = tablePath + "/tr";

        int rowCount = actions().countHowManyElements(rowsPath);

        // for each row
        for (int r = 1; r <= rowCount; r++) {
            String rowPath = "(" + rowsPath + ")["+r+"]";

            TableRow tableRow = getIconedTableRow(columns, rowPath);
            tableRows.add(tableRow);
        }

        return tableRows;
    }

    private TableRow getIconedTableRow(int columns, String rowPath) {
        TableRow row = new TableRow();

        // for each column
        for (int c = 1; c <= columns; c++) {
            String cellPath = "(" + rowPath + " //td)["+c+"]";

            if (c == 1) {
                Table.TableCell tableCell = new Table.TableCell();

                Icon mpIcon = completeGetIcon(cellPath + "/div");
                tableCell.addCellIcon(mpIcon);

                String cellValue = actions().findOneElementsText(cellPath + " //span");
                tableCell.addCellText(cellValue);
                // access with getStringSharedByIconByTableHeader() or getIconSharedByIconByTableHeader()
                row.addContent(tableCell);
            } else {
                String cellValue = actions().findOneElementsText(cellPath);
                row.addContent(cellValue);
            }
        }
        return row;
    }

    private TableRow getBlastDetailsMeasuringPointHeaderRow(int columnCount, String containerPath) {
        TableRow headerRow = new TableRow();

        // for each column
        for (int c = 1; c <= columnCount; c++) {
            String cellPath = "(" + containerPath + " //th)["+c+"]";

            String cellValue = actions().findOneElementsText(cellPath);
            cellValue = cellValue.replace("arrow_upward", "");
            cellValue = cellValue.replace("\n", "");
            headerRow.addContent(cellValue);
        }
        return headerRow;
    }

    public Table getProjectBillingReportTable() {
        Table table = new Table();

        String tablePath = "//div[@class='container fixed-top fullscreen'] //table";

        int columns = actions().countHowManyElements(tablePath + "/thead/tr/th");

        // Get header row
        TableRow headerRow = getProjectBillingReportTableHeaders(columns, tablePath + "/thead/tr");
        table.setHeader(headerRow);

        List<TableRow> tableRows = getProjectBillingReportTableContent(tablePath, columns);
        table.setContent(tableRows);

        return table;
    }

    public Table getDeviceBillingReportTable() {
        Table table = new Table();

        String tablePath = "//div[@class='container fixed-top fullscreen'] //table";

        int columns = actions().countHowManyElements(tablePath + "/thead/tr/th");

        // Get header row
        TableRow headerRow = getDeviceBillingReportTableHeaders(columns, tablePath + "/thead/tr");
        table.setHeader(headerRow);

        List<TableRow> tableRows = getDeviceBillingReportTableContent(tablePath, columns);
        table.setContent(tableRows);

        return table;
    }

    private TableRow getDeviceBillingReportTableHeaders(int columns, String headerRowPath) {
        TableRow headerRow = new TableRow();

        for (int c = 1; c <= columns; c++) {
            String header = switch (c) {
                case 1, 6 -> "";    // First column for Device Billing report is icon, and last column for Device Billing report is expandIcon
                default -> actions().findOneElementsText(headerRowPath + "/th[position()="+c+"]");
            };

            headerRow.addContent(header);
        }

        return headerRow;
    }

    private List<TableRow> getDeviceBillingReportTableContent(String tablePath, int columns) {
        List<TableRow> rows = new ArrayList<>();
        String rowPath = tablePath + "/tbody/tr[not(@style='display: none;')]";

        // Rows that are can be expanded, but aren't, are still in the DOM. Don't read them.
        int visibleRows = actions().countHowManyElements(rowPath );

        for (int r = 1; r <= visibleRows; r++) {
            TableRow row = new TableRow();

            // count /td so that we know if the row is expansion row, or expanded row
            int tdChildCount = actions().countHowManyElements(rowPath + "[position()="+r+"]/td");

            switch (tdChildCount) {
                case 6 -> {     // A expansion row, with six /td and content in six of them
                    for (int c = 1; c <= columns; c++) {
                        // First and last column value is Icon, the rest String
                        Object cellValue = switch (c) { // First column for Device Billing report is icon, and last column for Device Billing report is expandIcon
                            case 1, 6 -> completeGetIcon(rowPath + "[position()="+r+"]" + "/td[position()="+c+"]");
                            default -> actions().findOneElementsText(rowPath + "[position()="+r+"]" + "/td[position()="+c+"]");
                        };

                        row.addContent(cellValue);
                    }
                }
                case 3 -> {     // An expanded row, with three /td and content in two of them
                    for (int c = 1; c <= columns; c++) {
                        Object cellValue = switch (c) {
                            case 1, 2, 3, 6 -> "";   // Expanded rows have columns 1 - 3 combined into one
                            case 4 -> completeGetIcon(rowPath + "[position()="+r+"]" + "/td[position()="+ (c - 2) +"]");
                            case 5 -> actions().findOneElementsText(rowPath + "[position()="+r+"]" + "/td[position()="+ (c -2) +"]");
                            default -> throw new IllegalArgumentException("Cannot find value for column: " + c);
                        };

                        row.addContent(cellValue);
                    }
                }
                default -> throw new IllegalStateException("Only 3 or 6 td children are expected: " + tdChildCount);
            }

            rows.add(row);
        }
        return rows;
    }


    private TableRow getProjectBillingReportTableHeaders(int columns, String headerRowPath) {
        TableRow headerRow = new TableRow();

        for (int c = 1; c <= columns; c++) {
            String header = actions().findOneElementsText(headerRowPath + "/th[position()="+c+"]");
            headerRow.addContent(header);
        }

        return headerRow;
    }

    private List<TableRow> getProjectBillingReportTableContent(String tablePath, int columns) {
        List<TableRow> rows = new ArrayList<>();
        String rowPath = tablePath + "/tbody[@class='q-virtual-scroll__content']/tr";

        int rowCount = actions().countHowManyElements(rowPath);

        for (int r = 1; r <= rowCount; r++) {
            TableRow row = new TableRow();
            for (int c = 1; c <= columns; c++) {
                String cellValue = actions().findOneElementsText(rowPath + "[position()="+r+"]" + "/td[position()="+c+"]");
                row.addContent(cellValue);
            }
            rows.add(row);
        }
        return rows;
    }

    // todo: varför skriver jag att första raden är tom? menar jag column, och i så fall för vilka tabeller?
    private TableRow getFieldWrapperTableHeaderRow(int columnCount, String tablePath) {
        TableRow headerRow = new TableRow();

        // Last column has no value...
        columnCount = columnCount - 1;

        // for each column
        for (int c = 1; c <= columnCount; c++) {
            String cellPath = "(" + tablePath + " //th)["+c+"]";

            if (c == 1) {
                // for /project/10523/users/manage the first column is checkbox, but header is null
                headerRow.addContent(null);
            } else {
                String cellValue = actions().findOneElementsText(cellPath);
                headerRow.addContent(cellValue);
            }
        }
        return headerRow;
    }

    private Checkbox getCheckboxByName(String searchPhrase) {
        Checkbox checkbox = new Checkbox();

        String checkboxPath = "//div[@role='checkbox' and ./div[text()='"+searchPhrase+"']]";

//        Boolean isChecked = actions().findOneElementsAttribute(checkboxPath, "aria-checked").equals("true");
//        checkbox.setIsChecked(isChecked);
        String ariaCheckedStatus = actions().findOneElementsAttribute(checkboxPath, "aria-checked");
        Status status =  StatusAssesser.getCheckboxStatus(ariaCheckedStatus);

        if (status == Status.UNCHECKED) {
            boolean isDisabled = actions().findOneElementsAttribute(checkboxPath, "aria-disabled").equals("true");
            if (isDisabled) {
                status = Status.DISABLED;
            }
        }

        checkbox.setStatus(status);

        String text = actions().findOneElementsText("(" + checkboxPath + " //div)[last()]");
        checkbox.setText(text);

        return checkbox;
    }

    public Checkbox getCheckbox(String cellPath) {
        return getCheckbox(cellPath, false);
    }

    /**
     * Possibly this is a generic method for all checkboxes, but at creation the method is only tested for table at /users/manage
     * Has to be /div[role=checkbox]
     */
    public Checkbox getCheckbox(String cellPath, boolean getText) {
        actions().verifyLastNodeInXpath("div", cellPath);

        Checkbox checkbox = new Checkbox();

//        Boolean isChecked = actions().findOneElementsAttribute(cellPath, "aria-checked").equals("true");
//        checkbox.setIsChecked(isChecked);

        String ariaCheckedStatus = actions().findOneElementsAttribute(cellPath, "aria-checked");
        Status status =  StatusAssesser.getCheckboxStatus(ariaCheckedStatus);

        // An unchecked checkbox has aria-disabled only if it's class also contain 'disabled'
        if (status == Status.UNCHECKED) {
//            boolean isDisabled = actions().findOneElementsAttribute(cellPath, "aria-disabled").equals("true");
//            if (isDisabled) {
//                status = Status.DISABLED;
//            }

            boolean isDisabled = actions().elementHasAttribute(cellPath, "aria-disabled");

//            //div[@class='q-card']/div[2]/div[1]/div[1]/div[1]
//            (//div[@role='checkbox' and @aria-disabled])[1]
            if (isDisabled) {
                status = Status.DISABLED;
            }
        }

        checkbox.setStatus(status);

        if (getText) {
            String text = actions().findOneElementsText("(" + cellPath + " //div)[last()]");
            checkbox.setText(text);
        }

        return checkbox;
    }

    /**
     * Use for non-url dialogs.
     * E.g., Delete project.
     */
    public DeleteDialog getDeleteDialog() {
        DeleteDialog dd = new DeleteDialog();
        String dialogPath = "//div[@role='dialog']";

        String header = actions().findOneElementsText(dialogPath + " //div[contains(@class,'text-title')]");
        dd.setHeader(header);

        String text = actions().findOneElementsText(dialogPath + " //span");
        dd.setMainText(text);

        Checkbox cb = getCheckboxByName("I understand");
        dd.setConfirmationCheckbox(cb);

        dd.setCancelButton(getButtonByText("Cancel"));

        dd.setDeleteButton(getButtonByText("Delete"));

        return dd;
    }

    /**
     * Use method to make a click on a panel row called SettingsItem.
     * E.g., 'Project details' at /project/id/settings
     */
    public void clickSettingsItem(String settingsItemName) {
        if (settingsItemName.equals("Delete")) {
            actions().makeClick("//div[@data-qa-id='delete']");
        } else {
            actions().makeClick("//div[contains(text(),'"+settingsItemName+"')]/ancestor::a");
        }
    }

    /**
     * Use method to tick a checkbox with aria-label.
     */
    public void tickCheckbox(String checkboxPath) {
        actions().makeClick(checkboxPath);
    }

    /**
     * Works for headers with text + sorting icon
     */
    public Table.TableCell getTableHeaderCell(String headerCellPath) {
        Table.TableCell headerCell = new Table.TableCell();

        String headerText = actions().findOneElementsText(headerCellPath);

        // Remove linebreak+icon-text manually as Selenium do not give us a way to avoid the <i>
        if (headerText.contains("\narrow_upward")) {
            headerText = headerText.replace("\narrow_upward", "");
        }

        headerCell.addCellText(headerText);

        Icon sortIcon = completeGetIcon(headerCellPath);
        headerCell.addCellIcon(sortIcon);

        return headerCell;
    }

    public void selectDropdownByHeader(String dropdownHeader, String toBeSelected) {
        String dropdownPath = "//label[.//*[text()='"+dropdownHeader+"']]";
        selectDropdownByPath(dropdownPath, toBeSelected);
    }

    public void selectDropdownByPath(String dropdownPath, String toBeSelected) {
        // Open dropdown
        openDropdown(dropdownPath);
        PlaywrightActions.sleep(1);

        // Then get the content
//        List<String> dropdownContent = getExpandedDropdownContent();
        List<String> dropdownContent = getExpandedDropdownContent(false);

        boolean existInTheContent = dropdownContent.contains(toBeSelected);

        if (existInTheContent) {
            actions().makeClick("//div[text()='"+toBeSelected+"']");
        } else {
            throw new IllegalStateException("The expected selection '" + toBeSelected + "' was not included in the dropdown.");
        }
    }

    private String evaluateDropdownType() {
        boolean isListbox = actions().elementExistAndVisible("//div[@role='listbox']", false, 0);
        boolean isMenu = actions().elementExistAndVisible("//div[@role='menu']", false, 0);

        if (isListbox) {
            return "listbox";
        } else if (isMenu) {
            return "menu";
        } else {
            throw new IllegalArgumentException("Unknown dropdown type.");
        }
    }

    private void openDropdown(String dropdownPath) {
        actions().makeClick(dropdownPath);
    }

    private void closeDropdown(String dropdownPath) {
        actions().makeClick(dropdownPath);
    }

    public List<String> getDropdownContent(String dropdownHeader) {
        return getDropdownContentByPath("//label[.//*[text()='"+dropdownHeader+"']]", false);
    }
    
    public List<String> getDropdownContentByPath(String dropdownPath, boolean withDynamicContent) {
        openDropdown(dropdownPath);
        PlaywrightActions.sleep(1);
        List<String> dropdownContent = getExpandedDropdownContent(withDynamicContent);
        closeDropdown(dropdownPath);
        PlaywrightActions.sleep(1);
        return dropdownContent;
    }

    /**
     * A Dropdown can have either Menu or Listbox as expanded content.
     * todo: anpassa Dropdown enligt detta. Listbox finns även i mapSearchRutan
     */
    private List<String> getExpandedDropdownContent(boolean withDynamicContent) {
        // Get what type of dropdown we have expanded
        String dropdownContentType = evaluateDropdownType();

        // Make sure the dropdown is expanded
        boolean isExpanded = dropdownContentType.equals("listbox") || dropdownContentType.equals("menu");
        if (!isExpanded) {
            throw new IllegalStateException("Dropdown was not expanded.");
        }

        String contentPath = switch (dropdownContentType) {
            case "listbox" -> "//div[@role='listbox'] //div[@role='option']";
            case "menu" -> "//div[@role='menu'] //div[@class='cursor-pointer']";
            default -> throw new IllegalArgumentException("Unknown dropdownType: " + dropdownContentType);
        };

        if (withDynamicContent) {

            int rowCount = actions().countHowManyElements(contentPath);
            String scrollContainer = "//div[@role='listbox']";

            // First scroll to bottom so that we know when to stop scrolling.
            actions().scrollElementToBottom(scrollContainer);
            PlaywrightActions.sleep(1);
            String actualLastOption = actions().findOneElementsText(contentPath + "[last()]");

            // Go to top of dropdown content
            actions().scrollElementToTop(scrollContainer);
            PlaywrightActions.sleep(1);

            int optionHeight = actions().getCombinedHeightOfElements(contentPath + "[1]", false);
            Set<String> uniqueDropdownContent = new LinkedHashSet<>();

            while (true) {
                List<String> optionTexts = actions().findManyElementsTexts(contentPath);
                uniqueDropdownContent.addAll(optionTexts);

                String lastInCurrentDom = actions().findOneElementsText(contentPath + "["+rowCount+"]");

                if (actualLastOption.equals(lastInCurrentDom)) {
                    break;
                }

                actions().makeScroll(scrollContainer, optionHeight * rowCount);
            }

            return new ArrayList<>(uniqueDropdownContent);

        } else {
            return actions().findManyElementsTexts(contentPath);
        }
    }

    public void setValueToInput(String inputPath, String value) {
        String tag = actions().getTagName(inputPath);
        if (!tag.equals("input")) {
            throw new IllegalArgumentException("Unknown inputPath: " + inputPath);
        }
        actions().clearAndType(inputPath, value);
    }

    public void clickOnItem(String itemPath) {
        actions().makeClick(itemPath);
    }

    public List<FilterItem> getAllMenuFilterItems() {
        List<FilterItem> filterItems = new ArrayList<>();

        String filtersPath = "//div[@role='menu'] //div[@role='listitem']";

        int filterRows =  actions().countHowManyElements(filtersPath);

        for (int row = 1; row <= filterRows; row++) {
            String filterRowPath = "(" + "//div[@role='listitem'])[" + row + "]";
            
            FilterItem filter = getFilterItem(filterRowPath);
            filterItems.add(filter);
        }
        
        return filterItems;
    }

    private FilterItem getFilterItem(String filterRowPath) {
        FilterItem filter = new FilterItem();

        // Null, icon or checkbox
        boolean hasIcon = actions().elementExistAndVisible(filterRowPath + "//i", false, 0);
        if (hasIcon) {
            Icon icon = getIcon(filterRowPath + "//i");
            filter.setIcon(icon);
        }

        boolean hasCheckbox = actions().elementExistAndVisible(filterRowPath + "//div[@role='checkbox']", false, 0);
        if (hasCheckbox) {
            Checkbox checkbox = getCheckbox(filterRowPath + "//div[@role='checkbox']");
            filter.setCheckbox(checkbox);
        }

        // Text
        String text = actions().findOneElementsText(filterRowPath);
        filter.setText(text);

        return filter;
    }


    public Listbox getListbox() {
        Listbox listbox = new Listbox();

        List<MenuOption> menuOptions = new ArrayList<>();

        int menuOptionCount = actions().countHowManyElements("//div[@role='listbox'] //div[@role='option']");

        for (int o = 1; o <= menuOptionCount; o++) {
            String menuOptionPath = "//div[@role='listbox'] //div[@role='option']["+o+"]";

            MenuOption menuOption = getMenuOption(menuOptionPath);
            menuOptions.add(menuOption);
        }

        listbox.setOptions(menuOptions);

        return listbox;
    }

    public List<MenuOption> getCommonMenuOptions() {
        List<MenuOption> menuOptions = new ArrayList<>();

        String menuPath = "//div[@role='menu'] //div[@role='list']";

        int optionCount = actions().countHowManyElements(menuPath + "/div");

        for (int o = 1; o <= optionCount; o++) {
            MenuOption menuOption = new MenuOption();

            String menuOptionPath = menuPath + "/div["+o+"]";

            Icon icon = completeGetIcon(menuOptionPath + " //div[2]");
            menuOption.setIcon(icon);

            String text = actions().findOneElementsText(menuOptionPath + " //div[3]");
            menuOption.setText(text);

            // todo: add support for rightIcon

            menuOptions.add(menuOption);
        }

        return menuOptions;
    }

    private MenuOption getMenuOption(String menuOptionPath) {
        MenuOption menuOption = new MenuOption();

        Icon icon = completeGetIcon(menuOptionPath + " //div[2]");
        menuOption.setIcon(icon);

        String text = actions().findOneElementsText(menuOptionPath + " //div[3]");
        menuOption.setText(text);

        return menuOption;
    }

    public void selectMenuOption(String selectThisOption) {
        actions().makeClick("//div[@role='menu'] //*[@role='listitem' and .//div[text()='"+selectThisOption+"']]");
        PlaywrightActions.sleep(2);
    }

    public void selectListboxOption(String selectThisOption) {
        actions().makeClick("//div[@role='listbox'] //div[@role='option' and .//div[text()='"+selectThisOption+"']]");
        PlaywrightActions.sleep(2);
    }


    public void clickOnElement(String elementText) {
        actions().makeClick("//div[text()='"+elementText+"']");
    }

    public MenuCalendar getMenuCalendar() {
        MenuCalendar menuCalendar = new MenuCalendar();

        String menuPath = "//div[@role='menu']/div/div[@class='q-date__main col column']";

        String monthAndYearPath = "/div[1]/div/div[1]";
        String month = actions().findOneElementsText(menuPath + monthAndYearPath + "/div[2] //span[@class='block']");
        String year = actions().findOneElementsText(menuPath + monthAndYearPath + "/div[5] //span[@class='block']");
        menuCalendar.setMonth(month);
        menuCalendar.setYear(year);

        //div[@role='menu']/div/div[@class='q-date__main col column']/div[1]/div/div[2]
        String weekdaysPath = "/div[1]/div/div[2]";
        List<String> weekdays = actions().findManyElementsTexts(menuPath + weekdaysPath + "/div");
        menuCalendar.setWeekdays(weekdays);

        //div[@role='menu']/div/div[@class='q-date__main col column']/div[1]/div/div[3]
        String daysInMonthPath = "/div[1]/div/div[3]";
        List<String> daysInMonth =actions().findManyElementsTexts(menuPath + daysInMonthPath + "/div/div/button");
        menuCalendar.setDaysInMonth(daysInMonth);

        //div[@role='menu']/div/div[@class='q-date__main col column'] //div[@class='q-date__actions'] //button[2]
        boolean quickSelectorFromExist = actions().elementExistAndVisible(menuPath + "//div[@class='q-date__actions'] //button[1]", false, 0);
        if (quickSelectorFromExist) {
            String quickSelectorFrom = actions().findOneElementsText(menuPath + "//div[@class='q-date__actions'] //button[1]");
            menuCalendar.setQuickSelectorFrom(quickSelectorFrom);
        }

        boolean quickSelectorToExist = actions().elementExistAndVisible(menuPath + "//div[@class='q-date__actions'] //button[2]", false, 0);
        if (quickSelectorToExist) {
            String quickSelectorTo = actions().findOneElementsText(menuPath + "//div[@class='q-date__actions'] //button[2]");
            menuCalendar.setQuickSelectorTo(quickSelectorTo);
        }

        return menuCalendar;
    }

    public void clickButtonText(String buttonText) {
        actions().makeClick("//button[.//span[text()='"+buttonText+"']]");
    }

    public void clickButton(String button) {
        switch (button.toLowerCase()) {
            case "apply", "create", "save" -> actions().makeClick("//button[@type='submit']");
            case "meatball" -> actions().makeClick("//div[@role='toolbar'] //i[text()='more_vert']/ancestor::button");
            case "vibration report" -> actions().makeClick("//a[@data-qa-id='project_mp_vibration_report']");
            case "select columns" -> actions().makeClick("//i[text()='view_column']");
            case "panel close" -> actions().makeClick("//*[@data-qa-id='panel-btn-close']");
            case "list header plus" -> actions().makeClick("//*[@data-qa-id='create-new-entity']");
            case "commit" -> actions().makeClick("//div[text()='Commit']");
            case "discard", "add time slot"-> actions().makeClick("//span[text()='"+button+"']");
            case "remove" -> actions().makeClick("//div[@role='dialog'] //span[text()='Remove']");
//            case "+ create user" -> clickCreateNewUserButton();
//            case "copy agenda" -> clickOnButton(button);
            case "mon","tue","wed","thu","fri","sat","sun"  -> actions().makeClick("//*[text()='" + button + "']");
            default -> throw new IllegalArgumentException("Unexpected button: " + button);
        }
    }

    /**
     * If project has pre-req for automatic transient reports, then this msg will likely come first if test is fast.
     * @return The error messages coming up from bottom of screen.
     */
    public List<String> getToasts() {
        //Give time for more than "Creating temporary report" to show.

        if (actions().elementExistAndVisible("//div[@class='q-notification__message col']", false)) {
            // Collect the messages into a list
            return actions().findManyElementsTexts("//div[@class='q-notification__message col']");

        } else {
            return new ArrayList<>();
        }
    }

    //    ******* From IconPO *********

    public Icon getIcon2(final String iconPath) {
        String tagName = actions().getTagName(iconPath);
        if (!"i".equals(tagName)) {
            throw new IllegalArgumentException("This method only accept <i> as tag for last element in path.");
        }

        Icon icon = new Icon();

        IconType iconType = getIconType(iconPath);
        icon.setType(iconType);

        ColourSchema iconColor = getElementColourByCss(iconPath);
//        System.out.println("iconColor: " + iconColor);
        icon.setColour(iconColor);

        String color = actions().getComputedStyle(iconPath, "color");
//        System.out.println("color: " + color);

        return icon;
    }

    // High-level entry point: analyze DOM -> choose scenario -> build icon.
    public Icon getIcon1(final String parentPath) {
        System.out.println("******************* completeGetIcon start *******************");
//        System.out.println("parentPath: " + parentPath);

        String tagName = actions().getTagName(parentPath);
        if ("i".equals(tagName)) {
            throw new IllegalArgumentException("This method do not accept <i> as tag for last element in path.");
        }

        IconContext context = buildIconContext(parentPath, tagName);
        IconScenario scenario = determineScenario(context);

        return buildIconForScenario(context, scenario);
    }


    /**
     * Holds all DOM facts used to determine and build the icon.
     */
    private static class IconContext {
        String parentPath;
        String tagName;
        String iconDescendantXpath;

        boolean isRunningReport;
        boolean isButtonOrA;
        boolean isSortingIcon;
        boolean isTableIcon;
        boolean isDiv;

        boolean hasMonIcon;
        boolean isOnCheckboxDiv;
        boolean hasIChild;
        boolean hasAChild;
        boolean hasDivChild;
        boolean hasButtonChild;

        int divChildrenCount;
        boolean childDivHasCheckbox;

        // Derived composite conditions
        boolean isTableIconWithBannerSibling;
        boolean isIconWithMultipleDivSiblings;
        boolean isSimpleIcon;

        boolean divElementExist; // for hidden icon case
    }

    /**
     * Populate all fields needed to determine scenario and build icon.
     */
    private IconContext buildIconContext(String parentPath, String tagName) {
        IconContext ctx = new IconContext();
        ctx.parentPath = parentPath;
        ctx.tagName = tagName;
        ctx.iconDescendantXpath = parentPath + " //i";

        ctx.isRunningReport = actions().elementExistAndVisible(
                parentPath + " //*[contains(@class,'spinner')]",
                false,
                0
        );

        ctx.isButtonOrA = "button".equals(tagName) || "a".equals(tagName);
        ctx.isSortingIcon = "th".equals(tagName);
        ctx.isTableIcon = "td".equals(tagName);
        ctx.isDiv = "div".equals(tagName);

        ctx.hasMonIcon = actions().elementExistAndVisible(parentPath + "/i[@data-qa-id='mon_status']", false, 0);
        ctx.isOnCheckboxDiv = actions().elementExistAndVisible(parentPath + "[@role='checkbox']", false, 0);
        ctx.hasIChild = actions().elementExistAndVisible(parentPath + "/i", false, 0);
        ctx.hasAChild = actions().elementExistAndVisible(parentPath + "/a", false, 0);
        ctx.hasDivChild = actions().elementExistAndVisible(parentPath + "/child::div", false, 0);
        ctx.hasButtonChild = actions().elementExistAndVisible(parentPath + "/button", false, 0);

        ctx.divChildrenCount = ctx.hasDivChild
                ? actions().countHowManyElements(parentPath)
                : 0;

        ctx.childDivHasCheckbox = ctx.hasDivChild
                && actions().elementExistAndVisible(parentPath + "/div[@role='checkbox']", false, 0);

        ctx.isTableIconWithBannerSibling = ctx.isTableIcon && ctx.hasIChild && ctx.hasDivChild;
        ctx.isIconWithMultipleDivSiblings = ctx.isDiv && ctx.hasIChild && ctx.hasDivChild;
        ctx.isSimpleIcon = (ctx.hasIChild && !ctx.hasDivChild) || ctx.hasMonIcon;

        ctx.divElementExist = actions().elementExistAndVisible(parentPath, false, 0);

        return ctx;
    }

    private enum IconScenario {
        SPINNER,
        SORTING_ICON_TH,
        DIV_WITH_A_CHILD,
        BUTTON_CHILD_NO_I_NO_DIV,
        ICON_INSIDE_BUTTON_OR_A,
        CHECKBOX_SELF_DIV,
        CHECKBOX_CHILD_DIV,
        TABLE_ICON_WITH_BANNER_SIBLING,
        SIMPLE_ICON_OR_MON_ICON,
        ICON_WITH_MULTIPLE_DIV_SIBLINGS,
        NESTED_ICON_DIV_CHILDREN_3,
        HIDDEN_ICON_IN_DIV
    }

    /**
     * Pure decision tree. Easy to tweak when you discover new icon structures.
     */
    private IconScenario determineScenario(IconContext ctx) {

        if (ctx.isRunningReport) {
            return IconScenario.SPINNER;
        }

        if (ctx.isSortingIcon) {
            return IconScenario.SORTING_ICON_TH;
        }

        if (ctx.hasAChild) {
            return IconScenario.DIV_WITH_A_CHILD;
        }

        if (!ctx.hasIChild && !ctx.hasDivChild && ctx.hasButtonChild) {
            return IconScenario.BUTTON_CHILD_NO_I_NO_DIV;
        }

        if (ctx.isButtonOrA) {
            return IconScenario.ICON_INSIDE_BUTTON_OR_A;
        }

        if (ctx.isOnCheckboxDiv) {
            return IconScenario.CHECKBOX_SELF_DIV;
        }

        if (!ctx.hasIChild && ctx.childDivHasCheckbox) {
            return IconScenario.CHECKBOX_CHILD_DIV;
        }

        if (ctx.isTableIconWithBannerSibling) {
            return IconScenario.TABLE_ICON_WITH_BANNER_SIBLING;
        }

        if (ctx.isSimpleIcon) {
            return IconScenario.SIMPLE_ICON_OR_MON_ICON;
        }

        if (ctx.isIconWithMultipleDivSiblings) {
            return IconScenario.ICON_WITH_MULTIPLE_DIV_SIBLINGS;
        }

        if (ctx.divChildrenCount == 3) {
            return IconScenario.NESTED_ICON_DIV_CHILDREN_3;
        }

        if (ctx.divElementExist) {
            return IconScenario.HIDDEN_ICON_IN_DIV;
        }

        throw new IllegalStateException("Could not map icon in completeGetIcon()");
    }

    /**
     * Map scenario to concrete icon-building logic.
     */
    private Icon buildIconForScenario(IconContext ctx, IconScenario scenario) {
        return switch (scenario) {
            case SPINNER -> buildSpinnerIcon(ctx);
            case SORTING_ICON_TH -> buildSortingIcon(ctx);
            case DIV_WITH_A_CHILD -> buildDivWithAChildIcon(ctx);
            case BUTTON_CHILD_NO_I_NO_DIV -> buildButtonChildNoIIcon(ctx);
            case ICON_INSIDE_BUTTON_OR_A -> buildIconInsideButtonOrA(ctx);
            case CHECKBOX_SELF_DIV -> buildCheckboxSelfDivIcon(ctx);
            case CHECKBOX_CHILD_DIV -> buildCheckboxChildDivIcon(ctx);
            case TABLE_ICON_WITH_BANNER_SIBLING -> buildTableIconWithBannerSibling(ctx);
            case SIMPLE_ICON_OR_MON_ICON -> buildSimpleOrMonIcon(ctx);
            case ICON_WITH_MULTIPLE_DIV_SIBLINGS -> buildIconWithMultipleDivSiblings(ctx);
            case NESTED_ICON_DIV_CHILDREN_3 -> buildNestedIconDivChildren3(ctx);
            case HIDDEN_ICON_IN_DIV -> buildHiddenIcon(ctx);
        };
    }

// --- Scenario builders ---------------------------------------------------

    private Icon buildSpinnerIcon(IconContext ctx) {
        Icon icon = new Icon();

        String spinnerPath = ctx.parentPath + " //*[contains(@class,'spinner')]";

        IconType iconType = getIconType(spinnerPath);
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(spinnerPath);
//        icon.setStatus(status);

        ColourSchema iconColour = getElementColourByCss(spinnerPath);
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildSortingIcon(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getIconType(ctx.parentPath + "/i");
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(ctx.parentPath);
//        icon.setStatus(status);

        ColourSchema iconColour = getElementColourByCss(ctx.parentPath + " //i");
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildDivWithAChildIcon(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getIconType(ctx.iconDescendantXpath);
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(ctx.parentPath + "/a");
//        icon.setStatus(status);

        ColourSchema iconColour = getElementColourByCss(ctx.iconDescendantXpath);
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildButtonChildNoIIcon(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getIconType(ctx.iconDescendantXpath);
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(ctx.parentPath + "/button");
//        icon.setStatus(status);

        ColourSchema iconColour = getElementColourByCss(ctx.iconDescendantXpath);
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildIconInsideButtonOrA(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getIconType(ctx.iconDescendantXpath);
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(ctx.parentPath);
//        icon.setStatus(status);

        if (iconType.equals(PLUS)) {
            ColourSchema iconClassNameColour = getElementBackgroundColourByClassName(ctx.iconDescendantXpath);
            icon.setColour(iconClassNameColour);
        } else {
            ColourSchema iconColour = getElementColourByCss(ctx.iconDescendantXpath);
            icon.setColour(iconColour);
        }

        return icon;
    }

    private Icon buildCheckboxSelfDivIcon(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getCheckboxIcon(ctx.parentPath);
        icon.setType(iconType);

        boolean hasAriaDisabledAttribute = actions().elementExistAndVisible(ctx.parentPath + "[@aria-disabled]", false, 0);
//        StatusAssesser.Status status = hasAriaDisabledAttribute
//                ? DISABLED
//                : CLICKABLE;
//        icon.setStatus(status);

        ColourSchema iconColour = getElementColourByCss(ctx.parentPath + "/div");
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildCheckboxChildDivIcon(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getCheckboxIcon(ctx.parentPath + "/div");
        icon.setType(iconType);

//        icon.setStatus(CLICKABLE);

        ColourSchema iconColour = getElementColourByClassName(ctx.parentPath + "/div/div");
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildTableIconWithBannerSibling(IconContext ctx) {
        Icon icon = new Icon();

        /*
        If the <td> in //table/tbody[@class='q-virtual-scroll__content']/tr[1]/td[2]/i[1]/parent::td
        has multiple <i> then we need to 'step up' to the numbered <i>. In this case the /i[1].
         */
        if (ctx.parentPath.contains("/parent::td")) {
            ctx.parentPath = ctx.parentPath.replace("/parent::td", "");

            IconType iconType = getIconType(ctx.parentPath);
            icon.setType(iconType);

            ColourSchema iconColour = getElementColourByCss(ctx.parentPath);
            icon.setColour(iconColour);

        } else {

            IconType iconType = getIconType(ctx.parentPath + "/i");
            icon.setType(iconType);

            ColourSchema iconColour = getElementColourByCss(ctx.parentPath + "/i");
            icon.setColour(iconColour);

        }
        return icon;
    }

    private Icon buildSimpleOrMonIcon(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getIconType(ctx.parentPath + "/i");
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(ctx.parentPath);
//        icon.setStatus(status);

        String iconClassName = actions().findOneElementsAttribute(ctx.parentPath + "/i", "class");

        ColourSchema iconColour = (!iconClassName.contains("text-"))
                ? getElementColourByCss(ctx.parentPath + "/i")
                : getElementColourByClassName(ctx.parentPath + "/i");

        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildIconWithMultipleDivSiblings(IconContext ctx) {
        // Same as buildSimpleOrMonIcon, but separated if you later want special handling
        return buildSimpleOrMonIcon(ctx);
    }

    private Icon buildNestedIconDivChildren3(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getIconType(ctx.iconDescendantXpath);
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(ctx.parentPath);
//        icon.setStatus(status);

        ColourSchema iconColour = getElementColourByCss(ctx.iconDescendantXpath);
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildHiddenIcon(IconContext ctx) {
        Icon icon = new Icon();

        String divElement = actions().findOneElementsAttribute(ctx.parentPath, "outerHTML");

        boolean divContainsIcon = divElement.contains("<i class=\"q-icon notranslate material-icons\" aria-hidden=\"true\" role=\"img\">");
        boolean iIsVisible = actions().elementExistAndVisible(ctx.parentPath + " //i", false, 0);

        if (divContainsIcon && !iIsVisible) {
            IconType iconType = null;
            if (divElement.contains("settings")) {
                iconType = IconType.fromClassName("settings");
            } else if (divElement.contains("arrow_forward")) {
                iconType = IconType.fromClassName("arrow_forward");
            }
            icon.setType(iconType);
        }

//        icon.setStatus(INVISIBLE);

        return icon;
    }

    /**
     * There are two types of icons. Those with identifier in class name ('q-icon text-primary icon-projects')
     * and those with identifier in web element text ('q-icon notranslate material-icons q-mr-md q-mt-sm q-pl-sm text-infra-secondary').
     * If we find 'material-icons' in class attribute, then we need to get the icon text. That is why this method is here, and not in IconType
     */
    private IconType getIconType(String iconPath) {
        String className = actions().findOneElementsAttribute(iconPath, "class");

        // A sorting arrow has always text 'arrow_upward'. The parent has information about it pointing down (desc).
        if (className.contains("sort-icon")) {  // 'q-icon notranslate material-icons q-table__sort-icon q-table__sort-icon--left'
            String parentClassName = actions().findOneElementsAttribute(iconPath + "/parent::th", "class");

            String sortingIconIdentifier = (parentClassName.contains("sort-desc"))
                    ? "sort-icon-descending"    // om 'text-left sortable sorted sort-desc'
                    : "sort-icon-ascending";    // om 'text-left sortable sorted'

            return IconType.fromClassName(sortingIconIdentifier);

        } else {

            if (className.contains("q-expansion")) {
                className = (className.contains("rotated"))
                        ? "keyboard_arrow_down_expanded"
                        : "keyboard_arrow_down_collapsed";
            }

            String iconIdentifier = (className.contains("material-icons"))
                    ? actions().findOneElementsText(iconPath)
                    : IconType.extractIconTypeFromIconClassName(className);

            return IconType.fromClassName(iconIdentifier);
        }

    }

    /**
     * false, true, mixed
     */
    private IconType getCheckboxIcon(String checkboxPath) {
        String checkboxIdentifier = "checkbox_" + actions().findOneElementsAttribute(checkboxPath, "aria-checked");
        return IconType.fromClassName(checkboxIdentifier);
    }


    
}
