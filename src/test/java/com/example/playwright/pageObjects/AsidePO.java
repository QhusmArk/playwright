package com.example.playwright.pageObjects;

import com.example.playwright.components.aside.Aside;
import com.example.playwright.components.aside.asideItems.AsideItem;
import com.example.playwright.components.aside.asideItems.listItems.*;
import com.example.playwright.components.panels.TableColumnSettingsPanel;
import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.helpers.Navigate;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.AsideSize;
import com.example.playwright.helpers.enums.ProviderType;
import com.example.playwright.hooks.BrowserHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.playwright.helpers.enums.AsideSize.*;
import static com.example.playwright.helpers.enums.IconType.ARROW_RIGHT;
import static com.example.playwright.helpers.enums.ProviderType.*;

/**
 * The class contains methods to interact with and fetch information from a list, which can be displayed in
 * different styles (Compact, Medium, or Full). It also handles different types of lists, such as projects,
 * measuring points, blasts, devices, data reports, message rules, users, and comments.
 */
public class AsidePO extends CommonPO {

    public static final String COMPACT_ASIDE_LIST_ITEMS = "//div[contains(@data-qa-id, 'items-list')] //div[@data-qa-id='list-item']";
    public static final String COMPACT_ITEM_LIST = "(//div[contains(@data-qa-id, 'items-list')] //div[@data-qa-id='list-item'])";

    public static final String MEDIUM_FULL_ASIDE_TABLE_HEADER = "//table//th";


    // List Item elements
    public static final String ASIDE_ITEM_ICON_SPINNER = "//*[@class='q-spinner q-spinner-mat']";
    public static final String ASIDE_ITEM_THREE_DOT_BUTTON = "//div[@data-qa-id='list-item'] //button";

    // Menu elements
    public static final String ASIDE_ITEM_MENU_UPDATE = "//div[@data-qa-id='update']";
    public static final String ASIDE_ITEM_MENU_RENAME = "//div[@data-qa-id='rename']";
    public static final String ASIDE_ITEM_MENU_SHARE = "//div[@data-qa-id='share']";
    public static final String ASIDE_ITEM_MENU_SAVE = "//div[@data-qa-id='save']";
    public static final String ASIDE_ITEM_MENU_COPY = "//div[@data-qa-id='copy']";
    public static final String ASIDE_ITEM_MENU_DELETE = "//div[@data-qa-id='delete']";

    // List Header
    static final String ASIDE_HEADER_DOT_MENU = "//button[@data-qa-id='more-menu-button']";
    static final String ASIDE_SIZE_COMPACT = "//div[contains(text(), 'Compact list')]";
    static final String ASIDE_SIZE_MEDIUM = "//div[contains(text(), 'Medium list')]";
    static final String ASIDE_SIZE_FULL = "//div[contains(text(), 'Full screen list')]";

    public void changeAsideSize(AsideSize asideSize) {
        System.out.println("Trying to change list size.");
        actions().makeClick(ASIDE_HEADER_DOT_MENU);
        PlaywrightActions.sleep(1);
        switch (asideSize) {
            case COMPACT -> actions().makeClick(ASIDE_SIZE_COMPACT);
            case MEDIUM -> actions().makeClick(ASIDE_SIZE_MEDIUM);
            case FULL -> actions().makeClick(ASIDE_SIZE_FULL);
        }
    }

    /**
     * Collects the GUI counter below list content. Like 'Displaying 19 of 48'.
     * We cannot use api-response for this, because then we need to filter the response content as FE does.
     *
     * NB. The counter in '//div[@data-qa-id='aside-list']/div[contains(@data-qa-id, 'items-list counter-')]'
     * cannot be used at it does not update after filtering.
     *
     * @return The first group of digits.
     */
    public int getDisplayCounter() {
        Integer listItems = null;
        // Give GUI some time to count list items. Do not remove this or we'll get StaleRef.Exceptions for sure. OH
        PlaywrightActions.sleep(2);
        String str = actions().findOneElementsText("//*[@class='text-right']");

        // For safety reason. The counter can be a bit slow to count...
        if (str.equals("Displaying 0 of 0")) {
            str = actions().findOneElementsText("//*[@class='text-right']");
        }

        // Get the first group of digits in "Displaying XXX of XXX"
        Pattern pattern = Pattern.compile("Displaying (\\d+) of"); // match "Displaying ", followed String one or more digits (captured), followed String " of"
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            String output = matcher.group(1); // extract the first captured group (the digits)
            listItems = Integer.parseInt(output);
        }
        return listItems;
    }

    /**
     * @return Number of list items or rows.
     */
    private int getExpectedAsideContentCount(AsideSize asideSize, ProviderType providerType) {
        if (asideSize.equals(COMPACT)) {
            return (!providerType.equals(OVERVIEW))
                    ? getDisplayCounter()
                    : getExpectedOverviewListItems();
        } else {
            return getDisplayCounter();
        }
    }

    // todo: behöver jag en metod mellan getAside() och getAsideItems/AsideTable(), som kan hantera limits o scroll?
    public <T extends AsideItem> List<T> getAsideItems(ProviderType providerType, final int limit, boolean hover) {
        List<T> foundItems = new ArrayList<>();

        // Get the counter to how many items we should fetch
        int displayCounter = getExpectedAsideContentCount(COMPACT, providerType);

//        System.out.println("displayCounter: " + displayCounter);

        // No use continue if there are no listitems
        if (displayCounter == 0) {
            return foundItems;
        }

        // Check how many items it the DOM
        int actualItemsInDOM = actions().countHowManyVisibleElements("//div[contains(@data-qa-id, 'items-list')] //div[@data-qa-id='list-item']");
//        System.out.println("actualItemsInDOM: " + actualItemsInDOM);

        boolean needsScroll = displayCounter > actualItemsInDOM;

        // Do not scroll if we only want a portion of the listitems
        if (limit != -1) {
            if (limit < actualItemsInDOM) {
                needsScroll = false;
            }
        }

        // No use scrolling if all listitems are showing
        if (!needsScroll) {

            int listItemsToGet;
            if (limit == -1) {
                listItemsToGet = actualItemsInDOM;
            } else if (actualItemsInDOM < limit) {
                listItemsToGet = actualItemsInDOM;
            } else {
                listItemsToGet = limit;
            }

//            System.out.println("listItemsToGet no scroll: " + listItemsToGet);

            List<T> asideListItems = getAllAsideItemsInDOM(providerType, listItemsToGet, hover);
            foundItems.addAll(asideListItems);

            return foundItems;
        }

        while (foundItems.size() < displayCounter) {
            int listItemsToGet =  (limit == -1)
                    ? actualItemsInDOM
                    : limit - foundItems.size();

//            System.out.println("listItemsToGet1: " + listItemsToGet);

            // Safety valve, as we cannot have listItemsToGet being more than actualItemsInDOM
            if ((limit - foundItems.size()) > actualItemsInDOM) {
                listItemsToGet = actualItemsInDOM;
            }

//            System.out.println("listItemsToGet2: " + listItemsToGet);

            int nextScrollPlan;
            if (providerType.equals(DEVICE) || providerType.equals(DEVICE_INCL_BANNER)) {
                nextScrollPlan = (foundItems.isEmpty())
                        ? getFirstScrollIndexForDynamicListItems(actualItemsInDOM)
                        : getListItemFollowingScrollIndex(actualItemsInDOM);
            } else {
                nextScrollPlan = (foundItems.isEmpty())
                        ? getListItemFirstScrollIndex(actualItemsInDOM)
                        : getListItemFollowingScrollIndex(actualItemsInDOM);
            }

//            System.out.println("nextScrollPlan: " + nextScrollPlan);

            // Fetch what's in DOM
            List<T> batchOfListItems = getAllAsideItemsInDOM(providerType, listItemsToGet, hover);
//            System.out.println("firstInBatch: " + batchOfListItems.getFirst().getMainText());
//            System.out.println("lastInBatch: " + batchOfListItems.getLast().getMainText());

            boolean fulfilledLimit = ((foundItems.size() + batchOfListItems.size()) == limit);
            if (fulfilledLimit) {
                foundItems.addAll(batchOfListItems);
                break;
            }

            // If we have collected more than expected, then this is the last run
            boolean isLastRun = ((foundItems.size() + batchOfListItems.size()) > displayCounter);

            // If we have more items than expected, we need to get rid of some
            if (isLastRun) {
                // Items read twice must be removed from the last asideListItems
                int removeFromLastBatch = (foundItems.size() + batchOfListItems.size()) - displayCounter;
                batchOfListItems.subList(0, removeFromLastBatch).clear();  // Removes from the top
            }

//            IntStream.range(0, batchOfListItems.size())
//                    .forEach(d -> System.out.println(d + ": " + batchOfListItems.get(d).getMainText()));

            foundItems.addAll(batchOfListItems);

            // Calculate how much we must scroll in order to load new items to DOM
            if (!isLastRun) {
                actions().makeScroll("//div[@data-qa-id='aside-list']/div", nextScrollPlan);
            }
        }

        return foundItems;
    }

    private int getExpectedOverviewListItems() {
        return switch (Navigate.getLevelFromUrl()) {
            case "project" -> 10;
            case "company" ->
                 actions().countHowManyElements("//div[@data-qa-id='aside-list'] //div[@class='q-virtual-scroll__content'] //div[@data-qa-id='list-item']");
            default -> throw new IllegalStateException("Unexpected level: " + Navigate.getLevelFromUrl());
        };
    }

    /**
     * Dynamic listitems, like Device, change in height depending on what information to show (Warning, Change_status, etc)
     */
    public int getFirstScrollIndexForDynamicListItems(int actualItemsInDOM) {
        boolean isHeadless = BrowserHooks.isHeadless();

        int totalHeight = actions().getCombinedHeightOfElements("//div[@data-qa-id='aside-list']/div/div[@class='q-virtual-scroll__content']/div", 8);

        int averageHeight = (totalHeight / actualItemsInDOM) - 1;   // vid uncommitted lästes firstBatchLast och secondBatchFirst in som samma

        return (isHeadless)
                ? (actualItemsInDOM * averageHeight) + (8 * averageHeight)
                : (actualItemsInDOM * averageHeight) + (6 * averageHeight);
    }

    public int getListItemFirstScrollIndex(int actualItemsInDOM) {
        boolean isHeadless = BrowserHooks.isHeadless();

        int totalHeight = actions().getCombinedHeightOfElements("//div[@data-qa-id='aside-list']/div/div[@class='q-virtual-scroll__content']/div", 8);

        int averageHeight = totalHeight / actualItemsInDOM;

        return (isHeadless)
                ? (actualItemsInDOM * averageHeight) + (8 * averageHeight)
                : (actualItemsInDOM * averageHeight) + (6 * averageHeight);
        /*  // to be used for fixing scrolling on qa_computer
                return switch (browser) {
            case "chrome_headless" -> switch (os) {
                case "macOS" -> 12;
                case "Linux" -> 13;
                default -> throw new IllegalStateException("Unexpected os: " + os);
            };
            case "chrome" -> 10;
            default -> throw new IllegalStateException("Unexpected browser: " + browser);
        };
         */
    }

    public int getListItemFollowingScrollIndex(int actualItemsInDOM) {
        int totalHeight = actions().getCombinedHeightOfElements("//div[@data-qa-id='aside-list']/div/div[@class='q-virtual-scroll__content']/div", 8);
        int averageHeight = (totalHeight / actualItemsInDOM) - 1;   // the '-1' was needed to not scroll too much in Projects and Users.

        return (actualItemsInDOM * averageHeight) - (2 * averageHeight);
    }

    private int getTableRowFirstScrollIndex(int actualRowsInDOM) {
        boolean isHeadless = BrowserHooks.isHeadless();

        int totalHeight = actions().getCombinedHeightOfElements("//table/tbody[@class='q-virtual-scroll__content']/tr", 0);
        int averageHeight = totalHeight / actualRowsInDOM;

        return (isHeadless)
                ? (actualRowsInDOM * averageHeight) + (19 * averageHeight)
                : (actualRowsInDOM * averageHeight) + (6 * averageHeight);
    }

    private int getTableRowFollowingScrollIndex(int actualRowsInDOM) {
        int totalHeight = actions().getCombinedHeightOfElements("//table/tbody[@class='q-virtual-scroll__content']/tr", 0);
        int averageHeight = totalHeight / actualRowsInDOM;

        return (actualRowsInDOM * averageHeight);
    }

    private <T extends AsideItem> List<T> getAllAsideItemsInDOM(ProviderType providerType, final int getThisManyListItems, boolean hoverOnListItem) {
        if (getThisManyListItems < 1) {
            throw new IllegalArgumentException("getThisManyListItems cannot be less than 1, but is: " + getThisManyListItems);
        }

        List<T> collectedListItems = new ArrayList<>();

        // If DOM has 38 items, then this will run 38 times
        // NB. This is not really accurate if hoverOnListItem=true
        for (int i = 1; i <= getThisManyListItems; i++) {
            System.out.println("******************* " + i + "/" + getThisManyListItems + " *************************");
            String listItemPath = COMPACT_ITEM_LIST + "["+i+"]";

            T asideItem = switch (providerType) {
                case OVERVIEW -> (T) getOverviewListItem(listItemPath);
                case PROJECT -> (T) getProjectListItem(listItemPath);
                case USER -> (T) getUserListItem(listItemPath);
                case MEASURING_POINT -> (T) getMeasuringPointListItem(listItemPath);
                case BLAST -> (T) getBlastListItem(listItemPath);
                case DEVICE, DEVICE_INCL_BANNER -> (T) getDeviceListItem(listItemPath, hoverOnListItem);
                case DATA_REPORT -> (T) getDataReportListItem(listItemPath);
                case MESSAGE_RULE -> (T) getMessageRuleListItem(listItemPath);
                case SCHEDULED_REPORT -> (T) getScheduledReportListItem(listItemPath);
                case COMMENT, BILLING_REPORT ->
                        throw new IllegalArgumentException("Not yet supported provider: " + providerType);
            };

            System.out.println("mainText: " + i + ": " + asideItem.getMainText());
            collectedListItems.add(asideItem);

            System.out.println("******************* end *************************");
        }

        return collectedListItems;
    }

    private Table getAsideTable(ProviderType providerType, String asidePath, final int limit) {
        Table asideTable = new Table();

        String tablePath = asidePath + " //table";

        // Get header row
        Table.TableRow headerRow = getAsideTableHeaderRow(tablePath);
        asideTable.setHeader(headerRow);

        List<Table.TableRow> tableContent = getAsideTableRows(limit, headerRow, providerType, tablePath);
        asideTable.setContent(tableContent);

        return asideTable;
    }

    private Table.TableRow getAsideTableHeaderRow(String tablePath) {
        Table.TableRow headerRow = new Table.TableRow();

        String headerRowPath = tablePath + "/thead/tr";

        int columnCount = actions().countHowManyElements(headerRowPath + "/th");

        for (int c = 1; c <= columnCount; c++) {
            String headerPath = headerRowPath + "/th["+c+"]";

            // todo: Finns det risk att olika providerTypes har olika header?
            String headerText = actions().findOneElementsText(headerPath);
            // Remove the sorting arrow
            if (headerText.contains("\narrow_upward")) {
                headerText = headerText.replace("\narrow_upward", "");
            }

            headerRow.addContent(headerText);
        }

        return headerRow;
    }

    private List<Table.TableRow> getAsideTableRows(final int limit, Table.TableRow headers, ProviderType providerType, String tablePath) {
        List<Table.TableRow> tableRows = new ArrayList<>();

        String tableRowsPath = tablePath + "/tbody[@class='q-virtual-scroll__content']/tr";

        // Get the counter to how many items we should fetch
        int displayCounter = getExpectedAsideContentCount(MEDIUM, providerType);
//        System.out.println("displayCounter: " + displayCounter);

        // No use continue if there are no rows
        if (displayCounter == 0) {
            return tableRows;
        }

        // Check how many items it the DOM
        int actualRowsInDOM = actions().countHowManyVisibleElements(tableRowsPath);
//        System.out.println("actualRowsInDOM: " + actualRowsInDOM);
//
        boolean needsScroll = displayCounter > actualRowsInDOM;
//        System.out.println("needsScroll: " + needsScroll);

        // Do not scroll if we only want a portion of the rows in the DOM
        if (limit != -1) {
            if (limit < actualRowsInDOM) {
                needsScroll = false;
            }
        }
//        System.out.println("needsScroll: " + needsScroll);

        // No use scrolling if all listitems are showing
        if (!needsScroll) {
            int listItemsToGet;
            if (limit == -1) {
                listItemsToGet = actualRowsInDOM;
            } else if (actualRowsInDOM < limit) {
                listItemsToGet = actualRowsInDOM;
            } else {
                listItemsToGet = limit;
            }

//            System.out.println("listItemsToGet no scroll: " + listItemsToGet);
            return getAllAsideTableContentRowsInDOM(headers, providerType, tableRowsPath, listItemsToGet);
        }

        while (tableRows.size() < displayCounter) {
            int listItemsToGet =  (limit == -1)
                    ? actualRowsInDOM
                    : limit - tableRows.size();
//            System.out.println("listItemsToGet1: " + listItemsToGet);

            // Safety valve, as we cannot have listItemsToGet being more than actualItemsInDOM
            if ((limit - tableRows.size()) > actualRowsInDOM) {
                listItemsToGet = actualRowsInDOM;
            }
//            System.out.println("listItemsToGet2: " + listItemsToGet);

//            System.out.println("tableRows isEmpty: " + tableRows.isEmpty());
            int nextScrollPlan;
            nextScrollPlan = (tableRows.isEmpty())
                    ? getTableRowFirstScrollIndex(actualRowsInDOM)
                    : getTableRowFollowingScrollIndex(actualRowsInDOM);

            // Fetch what's in DOM
            List<Table.TableRow> batchOfTableRows = getAllAsideTableContentRowsInDOM(headers, providerType, tableRowsPath, listItemsToGet);

            boolean fulfilledLimit = ((tableRows.size() + batchOfTableRows.size()) == limit);
            if (fulfilledLimit) {
                tableRows.addAll(batchOfTableRows);
                break;
            }

            // If we have collected more than expected, then this is the last run
            boolean isLastRun = ((tableRows.size() + batchOfTableRows.size()) > displayCounter);

            // If we have more items than expected, we need to get rid of some
            if (isLastRun) {
                // Items read twice must be removed from the last asideListItems
                int removeFromLastBatch = (tableRows.size() + batchOfTableRows.size()) - displayCounter;
                batchOfTableRows.subList(0, removeFromLastBatch).clear();  // Removes from the top
            }

            tableRows.addAll(batchOfTableRows);

            // Calculate how much we must scroll in order to load new items to DOM
            if (!isLastRun) {
                actions().makeScroll("//div[@data-qa-id='aside-list']/div/div" ,nextScrollPlan);
            }
        }

        return tableRows;
    }

    private List<Table.TableRow> getAllAsideTableContentRowsInDOM(Table.TableRow headers, ProviderType providerType, String tableRowsPath, int listItemsToGet) {
        validateNoSpinner();

        List<Table.TableRow> tableRows = new ArrayList<>();

        for (int r = 1; r <= listItemsToGet; r++) {
            String rowPath = tableRowsPath + "["+r+"]";

            Table.TableRow tableContentRow = getAsideTableRow(headers, providerType, rowPath);
            tableRows.add(tableContentRow);

        }
        return tableRows;
    }


    /**
     * Tables varies depending on ProviderType.
     * E.g., some has first column with icons, some has icon + name in first column.
     */
    private Table.TableRow getAsideTableRow(Table.TableRow headers, ProviderType providerType, String itemRowPath) {
        Table.TableRow tableRow = new Table.TableRow();

        int columnsInHeaderCount = headers.getObjects().size();

        switch (providerType) {
            case PROJECT, USER, MESSAGE_RULE, SCHEDULED_REPORT -> {
                for (int c = 1; c <= columnsInHeaderCount; c++) {
                    String cellPath = itemRowPath + "/td[position()="+c+"]";

                    // For these providers first column merge icon+projectId
                    if (c == 1) {
                        Table.TableCell tableCell = new Table.TableCell();

                        // First get the icon
                        Icon leftIcon = completeGetIcon(cellPath + " //i/parent::*");
                        tableCell.addCellIcon(leftIcon);

                        // Then get the text
                        String cellText = actions().findOneElementsText(cellPath + " //span");
                        tableCell.addCellText(cellText);

                        tableRow.addContent(tableCell);

                        // The last column is the button
                    } else if (c == columnsInHeaderCount) {
                        Button rightButton = getButton(cellPath + "/button");
                        tableRow.addContent(rightButton);

                    } else {
                        String cellText = actions().findOneElementsText(cellPath);
                        tableRow.addContent(cellText);
                    }
                }
            }
            case DEVICE, DEVICE_INCL_BANNER -> {
                for (int c = 1; c <= columnsInHeaderCount; c++) {
                    String cellPath = itemRowPath + "/td["+c+"]";

                    // Get the previously stored header,
                    String headerText = headers.getStringAtPosition(c - 1);

                    // There is always an //i and //div(checkbox) but only one of them are 'style="display: none;"'
                    if (c == 1) {

                        // If there is an icon directly under /td, then we're at project level and then there cannot be a checkbox
                        boolean isProjectView = actions().elementExistAndVisible(cellPath + "/i", false,0);

                        if (isProjectView) {

                            Icon leftIcon = completeGetIcon(cellPath + "/i[position()=1]/parent::*");
                            tableRow.addContent(leftIcon);

                        } else {

                            boolean iconPresent = actions().elementExistAndVisible(cellPath + "/div[1]", false, 0);
                            boolean checkboxPresent = actions().elementExistAndVisible(cellPath + "/div[2]", false, 0);

                            if (iconPresent) {
                                Icon leftIcon = completeGetIcon(cellPath + " //i[position()=1]/parent::*");
                                tableRow.addContent(leftIcon);
                            } else if (checkboxPresent) {
                                Icon checkbox = completeGetIcon(cellPath + "/div[2]");
                                tableRow.addContent(checkbox);
                            } else {
                                throw new IllegalStateException("Either icon or checkbox have to be present.");
                            }
                        }

                    } else if (headerText.equals("Monitoring")) {
                        // Currently all devices have monStatusIcon...
                        Icon monStatusIcon = completeGetIcon(cellPath + " //i[position()=1]/parent::*");
                        tableRow.addContent(monStatusIcon);

                    } else if (headerText.equals("Status")) {

                        boolean hasBannerText = actions().elementExistAndVisible(cellPath + "/div", false, 0);
                        List<String> banners = (hasBannerText)
                                ? actions().findManyElementsTexts(cellPath + "/div")
                                : null;
                        tableRow.addContent(banners);

                    } else if (c == columnsInHeaderCount) {

                        boolean hasSettingsButton = actions().elementExistAndVisible(cellPath + "/button", false, 0);
                        Button settingsButton = (hasSettingsButton)
                                ? getButton(cellPath + "/button")
                                : null;
                        tableRow.addContent(settingsButton);
                    } else {
                        String cellText = actions().findOneElementsText(cellPath);
                        tableRow.addContent(cellText);
                    }
                }
            }
            case MEASURING_POINT -> {
                for (int c = 1; c <= columnsInHeaderCount; c++) {
                    String cellPath = itemRowPath + "/td[position()="+c+"]";

                    // There is always an //i and //div(checkbox) but only one of them are 'style="display: none;"'
                    if (c == 1) {

                        boolean iconPresent = actions().elementExistAndVisible(cellPath + "/div[1]", false, 0);
                        boolean checkboxPresent = actions().elementExistAndVisible(cellPath + "/div[2]", false, 0);

                        if (iconPresent) {
                            Icon leftIcon = completeGetIcon(cellPath + " //i[position()=1]/parent::*");
                            tableRow.addContent(leftIcon);
                        } else if (checkboxPresent) {
                            Icon checkbox = completeGetIcon(cellPath + "/div[2]");
                            tableRow.addContent(checkbox);
                        } else {
                            throw new IllegalStateException("Either icon or checkbox have to be present.");
                        }

                    } else if (c == columnsInHeaderCount) {
                        Button settingsButton = getButton(cellPath + "/button");
                        tableRow.addContent(settingsButton);
                    } else {
                        String cellText = actions().findOneElementsText(cellPath);
                        tableRow.addContent(cellText);
                    }
                }
            }

            case BLAST -> {
                for (int c = 1; c <= columnsInHeaderCount; c++) {
                    String cellPath = itemRowPath + "/td[position()="+c+"]";

                    if (c == 1) {
                        Icon leftIcon = completeGetIcon(cellPath + " //i[position()=1]/parent::*");
                        tableRow.addContent(leftIcon);

                    } else if (c == columnsInHeaderCount) {
                        Button settingsButton = getButton(cellPath + "/button");
                        tableRow.addContent(settingsButton);
                    } else {
                        String cellText = actions().findOneElementsText(cellPath);
                        tableRow.addContent(cellText);
                    }
                }
            }

            case DATA_REPORT -> {
                for (int c = 1; c <= columnsInHeaderCount; c++) {
                    String cellPath = itemRowPath + "/td[position()=" + c + "]";
                    String headerText = headers.getStringAtPosition(c - 1);

                    // First column is text
                    if (c == 1) {
                        Icon leftIcon = completeGetIcon(cellPath + " //i[position()=1]/parent::*");
                        tableRow.addContent(leftIcon);

                        // Report status-icon share column with "MP not active during time span"-message
                        // Status column can have four "states"
                        // - Empty
                        // - Message: "MP not active during time span"
                        // - Icon: Saved or Shared
                        // - Message + Icon
                    } else if (headerText.equals("Status")) {
                        Table.TableCell tableCell = new Table.TableCell();

                        // First get the icon (if any)
                        boolean hasStatusIcon = actions().elementExistAndVisible(cellPath + " //i", false, 0);

                        if (hasStatusIcon) {
                            // For some reason Status column in DataReport has visible icon without set attributes = visible in DOM but not visible on screen
                            boolean visibleForReal = !actions().findOneElementsAttribute(cellPath + " //i", "class").equals("q-icon");

                            if (visibleForReal) {
                                Icon statusIcon = completeGetIcon(cellPath);
                                tableCell.addCellIcon(statusIcon);
                            }
                        }

                        // Then get the message (if any)
                        boolean hasMessage = actions().elementExistAndVisible(cellPath + "/div", false, 0);
                        if (hasMessage) {
                            String messageBanner = actions().findOneElementsText(cellPath + "/div");
                            tableCell.addCellText(messageBanner);
                        }

                        tableRow.addContent(tableCell);

                    // last column is always a button
                    } else if (c == columnsInHeaderCount) {
                        Button settingsButton = getButton(cellPath + "/button");
                        tableRow.addContent(settingsButton);
                    } else {
                        String cellText = actions().findOneElementsText(cellPath);
                        tableRow.addContent(cellText);
                    }
                }
            }
            case OVERVIEW, COMMENT -> throw new IllegalArgumentException(providerType.name() + " not permitted for table.");
        }

        int createdColumns = tableRow.getObjects().size();

        // Validate that expected columns was mapped to the tableRow
        if (columnsInHeaderCount != createdColumns) {
            throw new IllegalStateException("columnsInHeaderCount/createdColumns: " + columnsInHeaderCount + "/" + createdColumns);
        }
        return tableRow;
    }

    private OverviewItem getOverviewListItem(String listItemPath) {
        OverviewItem asideItem = new OverviewItem();

        // Left icon
        String leftIconPath = "(" + listItemPath + "/div)[1]";
        Icon leftIcon = completeGetIcon(leftIconPath);
        asideItem.setLeftIcon(leftIcon);

        // Main text
        String mainTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-body1')]";
        String mainText = getListItemMainText(mainTextPath);
        asideItem.setMainText(mainText);

        // Optional subText icon
        String subTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-subtitle2')]";

        String subText = getListItemSubText(subTextPath + "/div");
        asideItem.setSubText(subText);

        // Not all overview list items have plus button
        boolean hasPlusButton = actions().elementExistAndVisible(listItemPath + "/div[3]/div", false, 0);
        if (hasPlusButton) {
            Button plusButton = getButton(listItemPath + "/div[3]/div");
            asideItem.setRightButton(plusButton);
        }

        return asideItem;
    }

    private ProjectItem getProjectListItem(String listItemPath) {
        ProjectItem asideItem = new ProjectItem();

        // Left icon
        String leftIconPath = "(" + listItemPath + "/div)[1]";
        Icon leftIcon = completeGetIcon(leftIconPath);
        asideItem.setLeftIcon(leftIcon);

        // Main text
        String mainTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-body1')]";
        String mainText = getListItemMainText(mainTextPath);
        asideItem.setMainText(mainText);

        // Optional subText icon
        String subTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-subtitle2')]";

        String subText = getListItemSubText(subTextPath + "/div");
        asideItem.setSubText(subText);

        // By default the button is not visible until listitem is hovered upon
        boolean buttonIsVisible = actions().elementExistAndVisible(listItemPath + "/div[3]/a", false, 0);
        if (buttonIsVisible) {
            Button rightButton = getButton(listItemPath + "/div[3]/a");
            asideItem.setRightButton(rightButton);
        }

        return asideItem;
    }

    private UserItem getUserListItem(String listItemPath) {
        UserItem asideItem = new UserItem();

        // Left icon
        String leftIconPath = "(" + listItemPath + "/div)[1]";
        Icon leftIcon = completeGetIcon(leftIconPath);
        asideItem.setLeftIcon(leftIcon);

        // Main text
        String mainTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-body1')]";
        String mainText = getListItemMainText(mainTextPath);
        asideItem.setMainText(mainText);

        // Optional subText icon
        String subTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-subtitle2')]";

        String subText = getListItemSubText(subTextPath + "/div");
        asideItem.setSubText(subText);

        // By default the button is not visible until listitem is hovered upon
        boolean buttonIsVisible = actions().elementExistAndVisible(listItemPath + "/div[3]/a", false, 0);
        if (buttonIsVisible) {
            Button rightButton = getButton(listItemPath + "/div[3]/a");
            asideItem.setRightButton(rightButton);
        }

        return asideItem;
    }

    private MeasuringPointItem getMeasuringPointListItem(String listItemPath) {
        MeasuringPointItem asideItem = new MeasuringPointItem();

        // Left icon
        String leftIconPath = "(" + listItemPath + "/div)[1]";
        Icon leftIcon = completeGetIcon(leftIconPath);
        asideItem.setLeftIcon(leftIcon);

        // Main text
        String mainTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-body1')]";
        String mainText = getListItemMainText(mainTextPath);
        asideItem.setMainText(mainText);

        // Optional subText icon
        String subTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-subtitle2')]";

        String subText = getListItemSubText(subTextPath + "/div");
        asideItem.setSubText(subText);

        Button rightButton = getButton(listItemPath + "/div[3]/button");
        asideItem.setRightButton(rightButton);

        return asideItem;
    }

    private BlastItem getBlastListItem(String listItemPath) {
        BlastItem asideItem = new BlastItem();

        // Left icon
        String leftIconPath = "(" + listItemPath + "/div)[1]";
        Icon leftIcon = completeGetIcon(leftIconPath);
        asideItem.setLeftIcon(leftIcon);

        // Main text
        String mainTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-body1')]";
        String mainText = getListItemMainText(mainTextPath);
        asideItem.setMainText(mainText);

        // Optional subText icon
        String subTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-subtitle2')]";

        String subText = getListItemSubText(subTextPath + "/div");
        asideItem.setSubText(subText);

        Button rightButton = getButton(listItemPath + "/div[3]/button");
        asideItem.setRightButton(rightButton);

        return asideItem;
    }
    private DeviceItem getDeviceListItem(String listItemPath, boolean hoverOnListItem) {
        DeviceItem asideItem = new DeviceItem();

        String listItemLeftPartPath = listItemPath + "/div[1]";

        // Left icon
        Icon leftIcon = completeGetIcon(listItemLeftPartPath);
        asideItem.setLeftIcon(leftIcon);

        String listItemMiddlePartPath = listItemPath + "/div[2]/a/div[2]";

        // Main text
        String mainText = getListItemMainText(listItemMiddlePartPath + "/div[1]/span");
        asideItem.setMainText(mainText);

        // Only communicating devices has subIcon and subText
        boolean hasSubTextIcon = actions().elementExistAndVisible(listItemMiddlePartPath + "/div[2]/i", false, 0);

        if (hasSubTextIcon) {

            Icon subTextIcon = completeGetIcon(listItemMiddlePartPath + "/div[2]/i/parent::div");
            asideItem.setListItemIcon(subTextIcon);

            // A logger in unboxing state has no subText, i.e., no last_read data
            String subText = (actions().elementExistAndVisible(listItemMiddlePartPath + "/div[2]/div", false, 0))
                    ? getListItemSubText(listItemMiddlePartPath + "/div[2]/div")
                    : null;
            asideItem.setSubText(subText);

            // todo: favourite star

            // a div[3] without div-children have no banners
            boolean hasBanners = actions().elementExistAndVisible(listItemMiddlePartPath + "/div[3]/div", false, 0);
            if (hasBanners) {
                List<Banner> banners = getAsideBanners(listItemMiddlePartPath + "/div[3]");
                asideItem.setBanners(banners);
            }
        }

        String listItemRightPartPath = listItemPath + "/div[3]/a";

        // todo: This will likely break when reinventing scroll function
        // By default the button is not visible until listitem is hovered upon
//        actions().hoverAboveElement(listItemPath);

        if (hoverOnListItem) {
            actions().hoverAboveElement(listItemPath);
        }

        boolean buttonIsVisible = actions().elementExistAndVisible(listItemRightPartPath, false, 0);
        if (buttonIsVisible) {
            Button rightButton = getButton(listItemRightPartPath);
            asideItem.setRightButton(rightButton);
        }

        return asideItem;
    }

    private DataReportItem getDataReportListItem(String listItemPath) {
//        boolean noSpinnerPresent = actions().elementDoNotExist("//*[@class='q-spinner q-spinner-mat']", 120);
        validateNoSpinner();

        DataReportItem asideItem = new DataReportItem();

        // Left icon
        String leftIconPath = "(" + listItemPath + "/div)[1]";
        Icon leftIcon = completeGetIcon(leftIconPath);
        asideItem.setLeftIcon(leftIcon);

        // Main text
        String mainTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-body1')]";
        String mainText = getListItemMainText(mainTextPath);
        asideItem.setMainText(mainText);

        // Optional subText icon
        String subTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-subtitle2')]";

        // NB. no save/share icon exist for reports with SPINNER, ABORTED or non-shared reports
        boolean hasSubTextIcon = actions().elementExistAndVisible(subTextPath + "/div/i", false, 0);
        if (hasSubTextIcon) {
            Icon subTextIcon = completeGetIcon(subTextPath + "/div");
            asideItem.setListItemIcon(subTextIcon);
        }

        String subText = getListItemSubText(subTextPath + "/div/span");
        asideItem.setSubText(subText);

        Button rightButton = getButton(listItemPath + "/div[3]/button");
        asideItem.setRightButton(rightButton);

        return asideItem;
    }

    private ScheduledReportItem getScheduledReportListItem(String listItemPath) {
        System.out.println("listItemPath: " + listItemPath);
        ScheduledReportItem asideItem = new ScheduledReportItem();

        // Left icon
        String leftIconPath = "(" + listItemPath + "/div)[1]";
        Icon leftIcon = completeGetIcon(leftIconPath);
        asideItem.setLeftIcon(leftIcon);

        // Main text
        String mainTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-body1')]";
        String mainText = getListItemMainText(mainTextPath);
        asideItem.setMainText(mainText);

        // Subtext
        String subTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-subtitle2')]";
        String subText = getListItemSubText(subTextPath + "/div");
        asideItem.setSubText(subText);

        // By default the button is not visible until listitem is hovered upon
        boolean buttonIsVisible = actions().elementExistAndVisible(listItemPath + "/div[3]/a", false, 0);
        if (buttonIsVisible) {
            Button rightButton = getButton(listItemPath + "/div[3]/a");
            asideItem.setRightButton(rightButton);
        }

        return asideItem;
    }

    private MessageRuleItem getMessageRuleListItem(String listItemPath) {
        MessageRuleItem asideItem = new MessageRuleItem();

        // Left icon
        String leftIconPath = "(" + listItemPath + "/div)[1]";
        Icon leftIcon = completeGetIcon(leftIconPath);
        asideItem.setLeftIcon(leftIcon);

        // Main text
        String mainTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-body1')]";
        String mainText = getListItemMainText(mainTextPath);
        asideItem.setMainText(mainText);

        // Optional subText icon
        String subTextPath = "(" + listItemPath + "/div)[2] //div[contains(@class,'text-subtitle2')]";

        Icon subTextIcon = completeGetIcon(subTextPath);
        asideItem.setListItemIcon(subTextIcon);

        String subText = getListItemSubText(subTextPath + "/div");
        asideItem.setSubText(subText);

        // Only inactive mr listitems have
        boolean hasBanner = actions().elementExistAndVisible(listItemPath + "/div[2]/a/div[2]/div[3]", false, 0);
        if (hasBanner) {
            List<Banner> banners = getAsideBanners(listItemPath + "/div[2]/a/div[2]/div[3]");
            asideItem.setBanners(banners);
        }

        // By default the button is not visible until listitem is hovered upon
        boolean buttonIsVisible = actions().elementExistAndVisible(listItemPath + "/div[3]/a", false, 0);
        if (buttonIsVisible) {
            Button rightButton = getButton(listItemPath + "/div[3]/a");
            asideItem.setRightButton(rightButton);
        }

        return asideItem;
    }

    /**
     * @return mainText for compact list for:
     * Account: Overview, Project, Users
     * Project: Overview, Blasts, Devices, Data reports, Message Rules, Users
     */
    private String getListItemMainText(String listItemPath) {
        return actions().findOneElementsText(listItemPath);
    }

    private String getListItemSubText(String subTextPath) {
        return (actions().elementExistAndVisible(subTextPath, false, 0))
                ? actions().findOneElementsText(subTextPath)
                : null;
    }

    public String getEmptyListText() {
        return actions().findOneElementsText("//*[@role='listitem'][@class='q-item q-item-type row no-wrap fit column list']");
    }

    public String getEmptyListBySearchText() {
        return actions().findOneElementsText("//div[@class='q-item q-item-type row no-wrap fit column list']/div[1]");
    }


    // todo: flytta 'actions().click("//div[@role='menu'] //div[text()='" + menuSelection + "']");' till MenuPO.
    // När vi har klickat på menyknappen är jobbet klart i ListPO. Sen ska vi anropa en metod i MenuPO som tar hand om alla olika varianter av menyval.
    /**
     * Works for COMPACT list.
     */
    public void clickOnListItemMenuAndSelect(String name, String menuSelection) {

        validateNoSpinner();

        if (actions().getAsideSize().equals(COMPACT)) {
            // Click on the three button icon
            actions().makeClick("//*[contains(text(), '" + name + "')]/ancestor::div[@data-qa-id='list-item'] //*[contains(text(), 'more_vert')]");
            // Select
            actions().makeClick("//div[@role='menu'] //div[text()='" + menuSelection + "']");
        }

        if (actions().getAsideSize().equals(MEDIUM) || (actions().getAsideSize().equals(FULL))) {
            // todo: to be implemented
        }
    }

    /**
     * Checks that no data report is in creation in Aside or Create_report-button.
     * Does not support Billing report creation.
     * Throws TimeoutException if element is not invisible after 120 seconds.
     */
    public void validateNoSpinner() {
        actions().elementDoNotExist("//*[@class='q-spinner q-spinner-mat']", 180);
    }

    // todo: denna kan krocka med andra element med samma text
    public void clickOnTopListItemMenuAndSelect(String menuSelection) {
        // todo: bör nedan ligga i metod i MenuPO?
        // Click on the menu buttons
        actions().makeClick("//div[@data-qa-id='list-item'] //*[contains(text(), 'more_vert')]");

        // Select menu item with same text as menuSelection
        actions().makeClick("//div[@role='menu'] //div[text()='" + menuSelection + "']");
    }

    public void selectAllColumnGroupsForAsideTable() {
        clickOnAListHeaderIcon("select-columns-button");

        List<String> controllerCheckboxesNames = getControllerCheckboxes();
        controllerCheckboxesNames.forEach(this::selectColumn);

        clickColumnSettingsButton("Apply");
    }

    /**
     * @return The top level checkboxes in 'Table column settings' for devices
     */
    private List<String> getControllerCheckboxes() {
        List<String> controllerCheckboxesNames = new ArrayList<>();
        int groupsOfCheckboxes = actions().countHowManyElements("//div[@role='dialog'] //div[@role='listitem']");
        // Select all control-checkboxes
        for (int i = 1; i <= groupsOfCheckboxes; i++) {
            String controllerCheckboxName = actions().findOneElementsText("(//div[@role='dialog'] //div[@role='listitem'])[" + i + "] //div");
            controllerCheckboxesNames.add(controllerCheckboxName);
        }
        return controllerCheckboxesNames;
    }

    /**
     * Table column settings.
     * Sets only one column (except default ones)
     */
    public void selectThisColumnForAsideTable(String columnName) {
        clickOnAListHeaderIcon("select-columns-button");

        // The first checkbox in a group of checkboxes are used to control the other checkboxes in bulk.
        resetCheckboxes();

        // Select the column
        selectColumn(columnName);
        // Click Apply
        clickColumnSettingsButton("Apply");
    }

    public void clickColumnSettingsButton(String button) {
        switch (button) {
            case "close" -> actions().makeClick("//div[@class='q-card'] //i[text()='close']");
            case "Apply", "Restore default" -> actions().makeClick("//div[@class='q-card'] //span[text()='"+button+"']");
        }
    }

    /**
     * Table column settings.
     * Sets many columns (plus default ones)
     */
    public void selectTheseColumnsForAsideTable(List<String> columnNames) {
        clickOnAListHeaderIcon("select-columns-button");

        resetCheckboxes();

        columnNames.forEach(this::selectColumn);

        // Click Apply
        clickColumnSettingsButton("Apply");
    }

    public void selectColumn(String columnName) {
        // Select the column
        actions().makeClick("//div[@class='q-card'] //div[text()='"+columnName+"']");
    }

    /**
     * The first checkbox in a group of checkboxes are used to control the other checkboxes in bulk.
     */
    private void resetCheckboxes() {
        // How many groups of checkboxes are there
        int groupsOfCheckboxes = actions().countHowManyElements("//div[@role='dialog'] //div[@role='listitem']");

        for (int i = 1; i <= groupsOfCheckboxes; i++) {
            String controllerCheckboxPath = "(//div[@role='dialog'] //div[@role='listitem'])[" + i + "] //div";
            String controllerCheckboxState = actions().findOneElementsAttribute(controllerCheckboxPath, "aria-checked");
            switch (controllerCheckboxState) {
                case "true" -> actions().makeClick(controllerCheckboxPath); // click once to set controllerCheckbox to false
                case "false" -> {}  // do not do anything, checkbox is not checked
                case "mixed" -> {
                    actions().makeClick(controllerCheckboxPath);  // click once to set controllerCheckbox to true
                    actions().makeClick(controllerCheckboxPath);  // click once to set controllerCheckbox to false
                }
            }
        }
    }

    private void resetCheckboxes(int groupsOfCheckboxes) {
        for (int i = 1; i <= groupsOfCheckboxes; i++) {
            String controllerCheckboxPath = "(//div[@role='dialog'] //div[@role='listitem'])[" + i + "] //div";
            String controllerCheckboxState = actions().findOneElementsAttribute(controllerCheckboxPath, "aria-checked");
            switch (controllerCheckboxState) {
                case "true" -> actions().makeClick(controllerCheckboxPath); // click once to set controllerCheckbox to false
                case "false" -> {}  // do not do anything, checkbox is not checked
                case "mixed" -> {
                    actions().makeClick(controllerCheckboxPath);  // click once to set controllerCheckbox to true
                    actions().makeClick(controllerCheckboxPath);  // click once to set controllerCheckbox to false
                }
            }
        }
    }

    // todo: belongs to MenuPO
    public List<String> getMenuOptions() {
        String firstListItemMenuButtonPath = "//div[contains(@data-qa-id, 'items-list')] //i[contains(text(), 'more_vert')]";
        // Open the first list items menu
        actions().makeClick(firstListItemMenuButtonPath);

        List<String> menuOptions = actions().findManyElementsTexts("//div[@class='q-list'] //div[@class='q-item__label q-pr-lg text-body1']");
        // Close the first list items menu
        actions().makeClick(firstListItemMenuButtonPath);

        return menuOptions;
    }

    // usable from both account and project overview
    public void clickOnOverviewListPlus(ProviderType providerType) {
        switch (providerType) {
            case PROJECT -> clickListItemPlus("Projects");
            case MEASURING_POINT -> clickListItemPlus("Measuring points");
            case COMMENT -> clickListItemPlus("Comments");
        }
    }

    public void clickListItemPlus(final String textToLookFor) {
        actions().makeClick(LEFT_MENU_AREA + " //div /span[contains(text(),'" + textToLookFor + "')]/parent::div/parent::div/parent::div/parent::div/parent::div //button //i");
    }

    public void clickOnThisAsideItem(String listItemName) {
        actions().makeClick("//*[contains(text(), '" + listItemName + "')]");
        PlaywrightActions.sleep(1);
    }

    public void openCompactListItemMenu(String name) {
        String listItemMenuPath = "//*[contains(text(), '" + name +"')]/ancestor::div[@data-qa-id='list-item']//button";
        actions().makeClick(listItemMenuPath);
    }

    public void makeSearchInAside(String searchValue) {
        System.out.println("Searching for: " + searchValue);
        PlaywrightActions.sleep(2);

        boolean searchFieldAlreadyOpen = actions().elementExistAndVisible("//button[@data-qa-id='cancel-search-button']", false, 0);

        if (searchFieldAlreadyOpen) {
            clickOnAListHeaderIcon("cancel-search-button");
        }

        // Click search button
        clickOnAListHeaderIcon("search-button");        // Clear field and input searchValue
        inputSearchPhrase(searchValue);
        // Give list a bit of time to finish the search
        PlaywrightActions.sleep();
    }

    public void closeSearchInAside() {
        System.out.println("Closing search...");
        clickOnAListHeaderIcon("cancel-search-button");
    }

    public void inputSearchPhrase(String searchValue) {
        // Clear field and input searchValue
        actions().clearAndType(INPUT_SEARCH_FIELD, searchValue);
    }

    public void clickOnAListHeaderIcon(String iconToClick) {
        actions().makeClick("//button[@data-qa-id='"+iconToClick+"']");
        PlaywrightActions.sleep();
    }

    /**
     * Ticks all checkboxes
     */
    public void createBulkActionForAll(ProviderType providerType) {
        AsideSize asideSize = actions().getAsideSize();
        // Tick the first checkbox
        String firstIcon = (asideSize.equals(COMPACT)
                ? "((//div[@data-qa-id='list-item'])[1] //i)[1]"
                : "((//tr[contains(@class, 'cursor-pointer')])[1] //i)[1]");  // MEDIUM/FULL
        actions().makeClick(firstIcon);

        // Then tick the 'select all' box
        actions().makeClick("//div[@role='checkbox' and @aria-checked='mixed']");

        if (providerType.equals(MEASURING_POINT)) { // Click on Edit Time Frame icon
            actions().makeClick("//i[contains(text(), 'schedule')]");
        }

        if (providerType.equals(DEVICE)) {  // Click on Project icon
            actions().makeClick("//div[@role='listitem'] //i[@class='q-icon text-infra-secondary icon-projects']");
        }
    }

    /**
     * Ticks x checkboxes starting from the top
     */
    public void selectTheseForBulkAction(int listItemsToCheck) {
        AsideSize asideSize = actions().getAsideSize();

        // If a checkbox has been ticked, then structure of DOM changes, therefore 'nextIconPath'
        for (int i = 1; i <= listItemsToCheck; i++) {
            String firstIconPath = (asideSize.equals(COMPACT)
                    ? "((//div[@data-qa-id='list-item'])[1] //i)[1]"
                    : "((//tr[contains(@class, 'cursor-pointer')])[1] //i)[1]");  // MEDIUM/FULL
            String nextIconPath = (asideSize.equals(COMPACT)
                    ? "(//div[@data-qa-id='list-item'])["+i+"] //div[@role='checkbox']"
                    : "(//tr[contains(@class, 'cursor-pointer')])["+i+"] //div[@role='checkbox']");   // MEDIUM/FULL

            // Tick the first checkbox, and then all the rest
            String iconPath = (i == 1)
                    ? firstIconPath
                    : nextIconPath;

            // Else previous checkbox lingers on at page
            actions().moveMouseSlightly("//div[@data-qa-id='aside-header-menu']");

            actions().makeClick(iconPath);
        }
    }

    public void makeSearch(String text) {
        actions().clearAndType("//label[@data-qa-id='free-text-search-field'] //input", text);

        int visibleItems = getDisplayCounter();

        if (visibleItems == 0) {
            throw new IllegalStateException("No item had text '" + text + "'");
        }
        if (visibleItems > 1) {
            throw new IllegalStateException("More than one item had text '" + text + "'");
        }
    }

    /**
     * Method can either checkbox or un-checkbox
     */
    public void makeCheckbox(boolean firstInAsideToBeChecked) {
        AsideSize asideSize = actions().getAsideSize();

        String clickTarget = switch (asideSize) {
            case COMPACT -> firstInAsideToBeChecked
                    ? "//div[@data-qa-id='list-item'] //i"
                    : "//div[@data-qa-id='list-item'] //div[@role='checkbox']";

            case MEDIUM,FULL -> firstInAsideToBeChecked
                    ? "//tbody //tr //i"
                    : "//tbody //tr //div[@role='checkbox']";
        };

        actions().makeClick(clickTarget);
    }

    public Aside getAside() {
        ProviderType providerType = actions().getProviderTypeFromUrl();
        return getAside(providerType, -1, false);
    }

    public Aside getAside(ProviderType providerType) {
        if (!providerType.equals(OVERVIEW)) {
            throw new IllegalArgumentException("Only OVERVIEW accepted as providerType: " + providerType);
        }
        return getAside(providerType, -1, false);
    }

    public Aside getAside(final int limitListItemsOrTableRows) {
        ProviderType providerType = actions().getProviderTypeFromUrl();
        return getAside(providerType, limitListItemsOrTableRows, false);
    }

    public Aside getAside(final boolean hoverOnListItem) {
        ProviderType providerType = actions().getProviderTypeFromUrl();
        return getAside(providerType, -1, hoverOnListItem);
    }

    public Aside getAside(final int limitListItemsOrTableRows, final boolean hoverOnListItem) {
        ProviderType providerType = actions().getProviderTypeFromUrl();
        return getAside(providerType, limitListItemsOrTableRows, hoverOnListItem);
    }

    /**
     * Dynamic getter of all aside components.
     * The caller cannot know if Aside contains List<AsideItem> or Table.
     * @param limitListItemsOrTableRows To be used for limiting the scope of capturing aside elements
     * @param hoverOnListItem If scrolling works, then hovering above an aside element break the scroll counting.
     */
    public Aside getAside(final ProviderType providerType, final int limitListItemsOrTableRows, final boolean hoverOnListItem) {
        if (providerType.equals(ProviderType.COMMENT)) {
            throw new IllegalArgumentException("Comment should be OVERVIEW for getAside to work");
        }
        String asidePath = "//div[contains(@class,'side-panel')] //aside";

        Aside aside = new Aside();

        if (!actions().getAsideSize().equals(FULL)) {
            Button collapse = getButton(asidePath + " //button");
            aside.setCollapse(collapse);

            // A collapsed aside is impossible to view.
            if (collapse.getIcon().getType().equals(ARROW_RIGHT)) {
                return aside;
            }
        }

        Aside.AsideMenu menu = getAsideMenu(asidePath);
        aside.setMenu(menu);

        Aside.AsideHeader header = getAsideHeader(providerType, asidePath);
        aside.setHeader(header);

        Aside.AsideAction action = getAsideAction(providerType, asidePath);
        aside.setAction(action);

        if (actions().getAsideSize().equals(COMPACT)) {

            List<AsideItem> asideItems = getAsideItems(providerType, limitListItemsOrTableRows, hoverOnListItem);
            aside.setAsideItems(asideItems);

        } else if (actions().getAsideSize().equals(MEDIUM) || actions().getAsideSize().equals(FULL)) {

            Table tableContent = getAsideTable(providerType, asidePath, limitListItemsOrTableRows);
            aside.setTable(tableContent);

        }

        Aside.AsideFooter footer = getAsideFooter(asidePath);
        aside.setFooter(footer);

        return aside;
    }

    private Aside.AsideFooter getAsideFooter(String asidePath) {
        Aside.AsideFooter footer = new Aside.AsideFooter();

        String asideFooterPath = asidePath + " //div[@data-qa-id='aside-footer']";

        if (!actions().elementExistAndVisible(asideFooterPath, false, 0)) {
            return null;
        }

        String text = actions().findOneElementsText(asideFooterPath + "/descendant::*[last()]");  // devices = div[last()], users = span[last()]...
        footer.setText(text);

        return footer;
    }

    /**
     * When AsideAction is initiated, then all leftIcons are remodeled to leftCheckbox
     */
    private Aside.AsideAction getAsideAction(ProviderType providerType, String asidePath) {
        Aside.AsideAction action = new Aside.AsideAction();

        String asideActionPath = asidePath + " //div[@data-qa-id='aside-header-action']";

        int divChildren = actions().elementExistAndVisible(asideActionPath + "/div", false, 0)
                ? actions().countHowManyElements(asideActionPath + "/div")
                : 0;

        boolean hasAction = divChildren > 0;
        if (!hasAction) {
            return null;
        }

        switch (providerType) {
            // If action exist for DEVICE, then at least one device is checkbox'ed
            case DEVICE, DEVICE_INCL_BANNER -> {
                Checkbox checkbox = getCheckbox(asideActionPath + " //div[@role='checkbox']");
                action.setCheckbox(checkbox);

                String summaryText = actions().findOneElementsText(asideActionPath + " //div[contains(@class,'text-subtitle2 ')]");
                action.setSummaryText(summaryText);

                Button createProjectButton = getButton(asideActionPath + " //button");
                action.setCreateProjectButton(createProjectButton);
            }
            case MEASURING_POINT -> {
                Checkbox checkbox = getCheckbox(asideActionPath + " //div[@role='checkbox']");
                action.setCheckbox(checkbox);

                String summaryText = actions().findOneElementsText(asideActionPath + " //div[contains(@class,'text-subtitle2 ')]");
                action.setSummaryText(summaryText);

                Button createDataReporttButton = getButton(asideActionPath + "/descendant::button[1]");
                action.setCreateProjectButton(createDataReporttButton);

                Button editTimeFrameButton = getButton(asideActionPath + "/descendant::button[2]");
                action.setEditTimeFrameButton(editTimeFrameButton);
            }

            case BLAST -> {
                Icon loadLatestIconIcon = completeGetIcon(asideActionPath + "/div/div[2]");
                action.setLoadLatestIcon(loadLatestIconIcon);

                String loadLatestText = actions().findOneElementsText(asideActionPath + " //*[contains(@class,'text-body1')]");
                action.setLoadLatestText(loadLatestText);
            }
            default -> {    // For the ProviderTypes that do not have ActionHeader with content
                return null;
            }
        }

        return action;
    }

    private Aside.AsideHeader getAsideHeader(ProviderType providerType, String asidePath) {
        AsideSize asideSize = actions().getAsideSize();

        String asideHeaderPath = asidePath + " //div[@data-qa-id='aside-header']";

        Aside.AsideHeader asideHeader = new Aside.AsideHeader();

        // If there is a search input field, there should also be a back button, and no search button
        boolean hasSearchField = actions().elementExistAndVisible(asideHeaderPath + " //label[@data-qa-id='free-text-search-field']", false, 0);
        if (hasSearchField) {

            // If bulk action is active, then there is no back button
            boolean hasBackButton = actions().elementExistAndVisible(asideHeaderPath + " //button[@data-qa-id='cancel-search-button']", false, 0);
            if (hasBackButton) {
                Button back = getButton(asideHeaderPath + " //button[@data-qa-id='cancel-search-button']");
                asideHeader.setBackButton(back);
            }

            // Get the text value from the free-text search field input in the aside header
            String searchFieldValue = actions().findOneElementsValueAttribute(
                    asideHeaderPath + " //label[@data-qa-id='free-text-search-field'] //input"
            );

            // If the field is empty, use the default placeholder from the 5th list item input
            String valueToSet = searchFieldValue.isEmpty()
                    ? actions().findOneElementsAttribute("(//aside //div[@role='listitem'])[5] //input", "placeholder")
                    : searchFieldValue;
            // Set the search value in the aside header
            asideHeader.setSearchValue(valueToSet);

            asideHeader.setFilterButton(getButton("//button[@data-qa-id='filter-button']"));
            asideHeader.setMenuButton(getButton("//button[@data-qa-id='more-menu-button']"));

        } else {
            switch (providerType) {
                case OVERVIEW -> {
                    asideHeader.setRefreshButton(getButton("//button[@data-qa-id='reload-button']"));
                }
                case PROJECT, USER, BLAST, DATA_REPORT, MESSAGE_RULE -> {
                    asideHeader.setSearchButton(getButton("//button[@data-qa-id='search-button']"));
                    asideHeader.setFilterButton(getButton("//button[@data-qa-id='filter-button']"));
                    asideHeader.setRefreshButton(getButton("//button[@data-qa-id='reload-button']"));
                    asideHeader.setMenuButton(getButton("//button[@data-qa-id='more-menu-button']"));
                }
                case MEASURING_POINT -> {
                    asideHeader.setSearchButton(getButton("//button[@data-qa-id='search-button']"));

                    if (asideSize == MEDIUM || asideSize == FULL) {
                        asideHeader.setSortButton(getButton("//button[@data-qa-id='select-columns-button']"));
                    }

                    asideHeader.setFilterButton(getButton("//button[@data-qa-id='filter-button']"));
                    asideHeader.setRefreshButton(getButton("//button[@data-qa-id='reload-button']"));
                    asideHeader.setMenuButton(getButton("//button[@data-qa-id='more-menu-button']"));
                }
                case DEVICE, DEVICE_INCL_BANNER -> {
                    asideHeader.setSearchButton(getButton("//button[@data-qa-id='search-button']"));


                    if (asideSize == COMPACT) {
                        asideHeader.setSortButton(getButton("//button[@data-qa-id='sort-button']"));
                    } else if (asideSize == MEDIUM || asideSize == FULL) {
                        asideHeader.setSortButton(getButton("//button[@data-qa-id='select-columns-button']"));
                    }

                    asideHeader.setFilterButton(getButton("//button[@data-qa-id='filter-button']"));
                    asideHeader.setWarningButton(getButton("//button[@data-qa-id='show-warnings-button']"));
                    asideHeader.setRefreshButton(getButton("//button[@data-qa-id='reload-button']"));
                    asideHeader.setMenuButton(getButton("//button[@data-qa-id='more-menu-button']"));
                }
            }
        }

        return asideHeader;
    }

    private Aside.AsideMenu getAsideMenu(String asidePath) {
        String asideMenuPath = asidePath + " //div[@data-qa-id='aside-header-menu']";

        Aside.AsideMenu menu = new Aside.AsideMenu();

        Icon providerTypeIcon = getIcon(asideMenuPath + " //i");
        menu.setProviderTypeIcon(providerTypeIcon);

        Dropdown dropdown = getDropdownByPath(asideMenuPath + " //label[@data-qa-id='main-menu-field']");
        menu.setDropdown(dropdown);

        boolean hasPlusButton = actions().elementExistAndVisible(asideMenuPath + " //button[@data-qa-id='create-new-entity']", false, 0);
        if (hasPlusButton) {
            Button plusButton = getButton(asideMenuPath + " //button[@data-qa-id='create-new-entity']");
            menu.setPlusButton(plusButton);
        }

        boolean hasCommittedText = actions().countHowManyElements(asideMenuPath + "/div") == 2;
        if (hasCommittedText) {
            NoticeItem commitNotice = getNoticeItem(asideMenuPath + "/div[2]/div");
            menu.setCommitNotice(commitNotice);
        }

        return menu;
    }

    // For each providerType that have selectable columns button
    public TableColumnSettingsPanel getTableColumnSettingsPanel() {
        TableColumnSettingsPanel panel = new TableColumnSettingsPanel();

        String panelPath = "//div[@class='q-card']";

        PanelHeader panelHeader = getPanelHeader("columnSettingsPanel");
        panel.setPanelHeader(panelHeader);

        List<TableColumnSettingsPanel.CheckboxGroup> checkboxGroups = new ArrayList<>();

        int checkboxGroupCount = actions().countHowManyElements(panelPath + "/div[2]/div");

        // Get all groups
        for (int g = 1; g <= checkboxGroupCount; g++) {
            TableColumnSettingsPanel.CheckboxGroup group = new TableColumnSettingsPanel.CheckboxGroup();

            String groupPath = panelPath + "/div[2]/div["+g+"]";

            // Get the checkbox that can control the others
            Checkbox controllerBox = getCheckbox(groupPath + "/div[1]/div[1]", true);
            group.setControllerBox(controllerBox);

            // Get all checkboxes
            List<Checkbox> checkboxes = new ArrayList<>();

            int checkboxCount = actions().countHowManyElements(groupPath + "/div[1]/div[2]/div");
            for (int c = 1; c <= checkboxCount; c++) {

                String checkboxPath = groupPath + "/div[1]/div[2]/div["+c+"]";

                Checkbox checkbox = getCheckbox(checkboxPath, true);
                checkboxes.add(checkbox);
            }
            group.setCheckboxes(checkboxes);

            checkboxGroups.add(group);
        }

        panel.setCheckboxGroups(checkboxGroups);

        return panel;
    }
}
