package com.example.playwright.pageObjects;

import java.util.*;

// todo: implementera denna så jag inte har menyinteraktion över hela AutoTest...
public class MenuPO extends CommonPO {

    // .../project/10523/measure_points/create
//    public List<String> getMpConnectedDevicesMenu() {
//        List<String> foundItems = new ArrayList<>();
//        String itemsListCounter = actions().findOneElementsAttribute("//div[@role='menu'] //div[contains(@data-qa-id, 'items-list')]", "data-qa-id");
//        int expectedItems = getCountFromDataQaIdItemsList(itemsListCounter);
//
//        String deviceListInCreateMp = "//div[@role='menu'] //div[contains(@data-qa-id, 'items-list')] //div[@data-qa-id='list-item']";
//        int itemsInDOM = actions().countHowManyElements(deviceListInCreateMp);
//        int scrollToIndex = calculateScrollPosition(-1, itemsInDOM);
//
//        System.out.println("expectedItems: " + expectedItems);
//        System.out.println("itemsInDOM: " + itemsInDOM);
//        System.out.println("scrollToIndex: " + scrollToIndex);
//
//        // Make as many runs as it takes to collect all items
//        int runs = (int) Math.ceil((double) expectedItems / itemsInDOM);
//        System.out.println("runs: " + runs);
//        for (int run = 1; run <= runs; run++) {
//            // Get all items in DOM
//            foundItems.addAll(getAllDeviceListItemsInDOM(itemsInDOM));
//
//            // Do not make scroll the last run
//            if (run != runs) {
//                scrollToIndex = calculateScrollPosition(scrollToIndex, itemsInDOM);
//                System.out.println("new scrollToIndex: " + scrollToIndex);
//                actions().makeAsideScroll("#q-portal--menu--105 > div > div > div:nth-child(2) > div > div > div.q-virtual-scroll__content", scrollToIndex);
//            } else {
//                System.out.println("This is run "+run+" i.e., the last run.");
//            }
//        }
//
//        if (foundItems.size() > expectedItems) {
//            removeItemsAddedTwice(foundItems, expectedItems, itemsInDOM);
//        }
//
//        assertEquals(expectedItems, foundItems.size(), "We did not find as many items as expected.");
//        return foundItems;
//    }

//    private Collection<String> getAllDeviceListItemsInDOM(int itemsInDOM) {
//        List<String> collectedListItems = new ArrayList<>();
//
//        // If DOM has 38 items, then this will run 38 times
//        for (int i = 1; i <= itemsInDOM; i++) {
//            System.out.println("*******************" + i + "*************************");
//            String listItemPath = "(//div[@role='menu'] //div[contains(@data-qa-id, 'items-list')] //div[@data-qa-id='list-item'])["+i+"] //span";
//            collectedListItems.add(actions().findOneElementsText(listItemPath));
//        }
//
//        return collectedListItems;
//    }

//    private int getCountFromDataQaIdItemsList(String textWithCounter) {
//        int countStartsHere = textWithCounter.indexOf("counter-") + 8;
//        String count = textWithCounter.substring(countStartsHere);
//        return Integer.parseInt(count);
//    }

    /**
     * Copied from AsidePO
     * Different browsers fits different amount of ListItems in the list/table.
     * This + the screen size determine on where to scroll on the first run.
     */
//    private int calculateScrollPosition(int scrollPosition, int itemsInDOM) {
//        System.out.println("Browser: " + DeviceProperties.getBrowser());
//        // Startposition
//        if (scrollPosition == -1) {
//            return switch (Settings.getBrowser()) {
//                case "chrome_headless" -> 10;   // after bulkaction
//                case "chrome" -> 10;
//                default -> throw new IllegalStateException("Unexpected browser: " + Settings.getBrowser());
//            };
//        } else {
//            return scrollPosition + itemsInDOM;
//        }
//    }

    /**
     * If itemsInDOM is not equally divisible with expectedItems there will be some items loaded twice.
     * E.g., if itemsInDOM = 3, and expectedItems = 10, then loaded items will be {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Eight", "Nine", "Ten"}.
     * The dual read "Eight" and "Nine" has to be removed.
     * @param foundItems    How many items we stored from Aside
     * @param expectedItems How many items we assumed to find in Aside
     * @param itemsInDOM    How many items currently loaded into DOM
     */
    private void removeItemsAddedTwice(List<String> foundItems, int expectedItems, int itemsInDOM) {
        int numberOfItemsReadTwice = foundItems.size() - expectedItems;
        System.out.println("numberOfItemsReadTwice: " + numberOfItemsReadTwice);

        int numberOfItemsInTheEndReadOnce = expectedItems % itemsInDOM;
        System.out.println("numberOfItemsInTheEndReadOnce: " + numberOfItemsInTheEndReadOnce);

        int indexOfFirstItemReadTwice = foundItems.size() - (numberOfItemsReadTwice + numberOfItemsInTheEndReadOnce);
        System.out.println("indexOfFirstItemReadTwice: " + indexOfFirstItemReadTwice);

        for (int i = 0; i < numberOfItemsReadTwice; i++) {
            foundItems.remove(indexOfFirstItemReadTwice);  // Always remove at the firstDuplicateIndex since the list shrinks
        }
    }

    // .../project/10523/settings/agendas
    public Set<Map<String, String>> getCopyAgendaListItems() {
        Set<Map<String, String>> projectsAndAgendaNames = new HashSet<>();

        int items = actions().countHowManyElements("//div[@role='menu'] //div[@data-qa-id='list-item']");

        if (items == 0) {
            return projectsAndAgendaNames;
        }

        for (int item = 1; item <= items; item++) {
            Map<String, String> agendaMap = new HashMap<>();

            String projectName = actions().findOneElementsText("(//div[@role='menu'] //div[@data-qa-id='list-item'])["+item+"] //span");
            String agendaName = actions().findOneElementsText("((//div[@role='menu'] //div[@data-qa-id='list-item'])["+item+"] //div)[last()]");

            agendaMap.put("pName", projectName);
            agendaMap.put("aName", agendaName);

            projectsAndAgendaNames.add(agendaMap);
        }
        return projectsAndAgendaNames;
    }


    public Map<String, String> getAndClickOnTopMenuItem() {
        Map<String, String> agendaMap = getTopMenuItem();

        actions().makeClick("(//div[@role='menu'] //div[@data-qa-id='list-item'])[1]");
        return agendaMap;
    }

    // .../settings/agendas and clicked 'Copy agenda' button
    public Map<String, String> getTopMenuItem() {
        Map<String, String> agendaMap = new HashMap<>();
        String projectName = actions().findOneElementsText("(//div[@role='menu'] //div[@data-qa-id='list-item'])[1] //span");
        String agendaName = actions().findOneElementsText("((//div[@role='menu'] //div[@data-qa-id='list-item'])[1] //div)[last()]");
        agendaMap.put("pName", projectName);
        agendaMap.put("aName", agendaName);
        return agendaMap;
    }


}
