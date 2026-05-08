package com.example.playwright.helpers;

import com.example.playwright.helpers.enums.AsideSize;
import com.example.playwright.helpers.enums.ProviderType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class PlaywrightActions {

    private static final int TIME_TO_WAIT = 3;
    private static final int SLEEP = 1;
    private static final int MILLISECONDS_IN_SECOND = 1000;

    private final Page page;

    public PlaywrightActions(Page page) {
        this.page = page;
    }

    /**
     * Accesses browser local storage and gets the value from "SIDEBAR_SIZE".
     * @return The enum that has ie. "__q_strn|compact" as localStorageValue
     */
    public AsideSize getAsideSize() {
        String sidebarSizeValue = getLocalStorageItem("SIDEBAR_SIZE");

        return Optional.ofNullable(sidebarSizeValue)
                .map(AsideSize::fromLocalStorageValue)
                .orElseThrow(
                        () -> new IllegalStateException("List size not found in local storage."));
    }

    /**
     * Returns the value of a localStorage item by key.
     */
    private String getLocalStorageItem(String key) {
        return (String) page.evaluate(
                "key => window.localStorage.getItem(key)",
                key
        );
    }

    /**
     *
     * @param toValue Enum in String, ie. "COMPACT"
     */
    public void setAsideSize(String toValue) {

        // todo: varför sova här?
        try {
            Thread.sleep(2000);
        } catch (InterruptedException exception) {
            System.err.println(exception.getMessage());
        }

        AsideSize currentAsideSize = getAsideSize();
        AsideSize wantedAsideSize = AsideSize.getAsideSizeByName(toValue);
        System.out.println("currentasideSize/wantedasideSize: " + currentAsideSize + "/" + wantedAsideSize);

        // Only set the list size if it's not already set.
        if (!wantedAsideSize.equals(currentAsideSize)) {
            setLocalStorageItem("SIDEBAR_SIZE", wantedAsideSize.getLocalStorageValue());

            // Refresh browser to load new settings
            refreshBrowser();
        }
        // Give DOM time to load Med/Full aside
        sleep(1);
    }

    public void refreshBrowser() {
        Navigate.refreshBrowser();
    }

//  **********************************************************

    /**
     * Clicks on a locator with logging.
     */
    public void makeClick(String path) {
        System.out.print("\nTrying to click on " + path + "\n");
        Locator locator = getFirstLocator(path);
        locator.click();
    }

    // Not tested
    public final void appendText(String path, String inputValue) {
        System.out.println("\nTrying to find input field '" + path + "' and add '" + inputValue + "'");
        Locator locator = getFirstLocator(path);
        locator.pressSequentially(inputValue);
    }

    public final void clearAndType(String path, String inputValue) {
        System.out.println("\nTrying to clear input field '" + path + "' and type '" + inputValue + "'");
        Locator locator = getFirstLocator(path);
        locator.fill(inputValue);
    }

    public String findOneElementsText(String path, int timeToWait) {
        Locator locator = getFirstLocator(path);
        return locator.textContent();
    }

    /**
     * Returns text content of a locator.
     */
    public String findOneElementsText(String path) {
        Locator locator = getFirstLocator(path);
        return locator.textContent();
    }

    public String findOneElementsAttribute(final String path, String attributeName) {
        System.out.println("\nTrying to find value of attribute '" + attributeName + "' in '" + path + "'");
        Locator locator = getFirstLocator(path);
        String attributeValue = locator.getAttribute(attributeName);
        if (attributeValue == null) {
            throw new IllegalStateException("Could not find attribute '" + attributeName + "' in '" + path + "'");
        }
        return attributeValue;
    }

    public boolean elementHasAttribute(String path, String attributeName) {
        String attribute = findOneElementsAttribute(path, attributeName);
        return (attribute != null); // todo: kommer vi hit, eller har ovan metodanrop redan kastat motsv nosuchelementexception?
    }

    // todo: missvisande namn.
    /*
    Bygga in en if-sats i findOneElementsAttribute som säger typ
    Om Locator inte i DOM och attributeName = "Value"
    hämta då med JavaScript
     */
    public String findOneElementsValueAttribute(String path) {
        return findOneElementsAttribute(path, "value");
    }

    // todo: missvisande namn?
    public List<String> findManyElementsAttribute(String path, String attributeName) {
        Locator elements = page.locator(path).first();

        return elements.all()
                .stream()
                .map(element -> element.getAttribute(attributeName))
                .filter(Objects::nonNull)
                .toList();
    }

    public final boolean elementExistAndVisible(final String path) {
        return elementExistAndVisible(path, true);
    }

    public final boolean elementExistAndVisible(final String path, boolean failTestIfNotFound) {
        return elementExistAndVisible(path, failTestIfNotFound, 0);
    }

    public final boolean elementExistAndVisible(final String path, boolean failTestIfNotFound, int timeoutSeconds) {
        System.out.println("\nTrying to assert if '" + path + "' is visible.");
        int waitTimeMillis = (timeoutSeconds == 0)
//                ? 100
                ? 50
                : timeoutSeconds * 1000;

        return processElementExistAndVisible(path, failTestIfNotFound, waitTimeMillis);
    }

    public final List<String> findManyElementsTexts(String path) {
        return page.locator(path).allTextContents();
    }

    public int countHowManyVisibleElements(String path) {
        System.out.println("\nTrying to count how many visible elements from'" + path + "'");
        return countHowManyElements(path);
    }

    public int countHowManyElements(String path) {
        Locator elements = page.locator(path);
        return elements.count();
    }

    public String findOneElementsCssValue(String elementPath, String color) {
        System.out.println("\nTrying to find css value of attribute '" + color + "' in '" + elementPath + "'");

        Locator element = getFirstLocator(elementPath);
        Object result = element.evaluate(" (el, prop) => getComputedStyle(el)[prop] ", color);

        return (result != null)
                ? result.toString()
                : null;
    }

    public final void makeClickOnAllElements(String path) {
        makeClickOnSelectedElements(path);
    }

    public final void makeClickOnSomeElements(String path, int elementsToClick) {
        makeClickOnSelectedElements(path, elementsToClick);
    }

    private void makeClickOnSelectedElements(String path) {
        Locator elements = page.locator(path);
        int count = elements.count();

        makeClickOnSelectedElements(path, count);
    }

    private void makeClickOnSelectedElements(String path, int elementsToClick) {
        List<Integer> clickElements = IntStream.rangeClosed(1, elementsToClick)
                .boxed()
                .toList();

        makeClickOnSelectedElements(path, clickElements);
    }

    private void makeClickOnSelectedElements(String path, List<Integer> elementsToClick) {
        Locator elements = page.locator(path);

        int count = elements.count();

        for (Integer index : elementsToClick) {
            if (index < count) {
                elements.nth(index).click();
            }
        }
    }

    public void hoverAboveElement(String path) {
        Locator element = getFirstLocator(path);
        element.hover();
    }

    public void simulateKey(String path, String key) {
        String keyToUse = switch (key.toLowerCase()) {
            case "tab" -> "Tab";
            case "escape" -> "Escape";
            case "arrowdown" -> "ArrowDown";
            case "backspace" -> "Backspace";
            default -> throw new IllegalArgumentException(key);
        };
        Locator element = getFirstLocator(path);
        element.press(keyToUse);
    }

    public String getComputedStyle(String elementPath, String property) {
        Locator element = page.locator(elementPath).first();
        return (String) element.evaluate(
                "(el, prop) => window.getComputedStyle(el).getPropertyValue(prop)",
                property
        );
    }

    //  ************************* Processors *********************************

    /**
     * @return True if element is not present, or present but not visible.
     */
    public final boolean elementDoNotExist(final String xpath, int timeToWait) {
        System.out.println("\nTrying to assert that '" + xpath + "' is NOT present.");

        Instant startTime = Instant.now();

        try {
            boolean hidden = page.locator("xpath=" + xpath).first().isHidden(
                    new Locator.IsHiddenOptions()
                            .setTimeout(timeToWait * 1000)
            );

            printElapsedTime(startTime, false);
            return hidden;

        } catch (PlaywrightException e) {
            printElapsedTime(startTime, true);

            throw new PlaywrightException(
                    "The element was expected to be invisible, but wasn't.",
                    e
            );
        }
    }

    private Locator getFirstLocator(String path) {
       return getFirstLocator(path, 0);
    }

    /*
    Första
    Alla
    Incremental (1, 2, 3...)
    Vissa (1, 3, 7)
     */

//    private Locator getLocator(String path) {
//
//    }

    /**
     * Automatically waits until the element is:
     * attached to DOM
     * visible
     * stable (not moving)
     * enabled
     * receiving events.
     * NB. The method only returns the first element, so this method cannot be used to get many elements.
     */
    private Locator getFirstLocator(String path, int timeoutSeconds) {
        Locator locator = page.locator(path).first();

        int waitTimeMillis = (timeoutSeconds == 0)
                ? 100
                : timeoutSeconds * 1000;

        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
//                    .setTimeout(timeoutSeconds * 1000));
                    .setTimeout(waitTimeMillis));
            return locator;

        } catch (PlaywrightException e) {
            throw new PlaywrightException("Unable to find element. Perhaps it's hidden?", e);
        }
    }

    private boolean processElementExistAndVisible(String path, boolean failTestIfNotFound, int waitTimeMillis) {
        Locator locator = page.locator(path).first();

        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(waitTimeMillis));

        } catch (TimeoutError e) {
            if (failTestIfNotFound) {
                throw new TimeoutError("Unable to find element. Perhaps it's hidden?", e);
            } else {
                System.out.println(" -> As expected, we did not find the element");
                System.out.println(" -> Returning false and continuing test");
                return false;
            }
        } catch (PlaywrightException e) {
            throw new PlaywrightException("Unable to find element. Perhaps it's hidden?", e);
        }
        return true;
    }

    /**
     * Method used to limit access to getMethods, as many getMethods require that the xPath ends with a specific node.
     * @param lastNodeInXpath E.g, "div"
     * @param elementPath E.g, "//form //table/thead/tr/th[1]/div"
     */
    public boolean verifyLastNodeInXpath(String lastNodeInXpath, String elementPath) {
        String tagName = getTagName(elementPath);

        if (!tagName.equals(lastNodeInXpath)) {
            throw new IllegalArgumentException("elementPath " + lastNodeInXpath + " have to end with: " + lastNodeInXpath);
        }
        return true;
    }

    /**
     * Returns the tag name of the element (lowercase).
     */
    public String getTagName(String path) {
        Object result = getFirstLocator(path).first().evaluate("el => el.tagName.toLowerCase()");
        return (result != null)
                ? result.toString()
                : null;
    }

//  ************************** Helpers *******************************

    public void validateXpathLastElement(String expectedLastElement, String pathToValidate) {
        validateXpathLastElements(List.of(expectedLastElement), pathToValidate);
    }

    public void validateXpathLastElements(List<String> expectedLastElementsPath, String xPathToValidate) {
        String tag = getLastElementTag(xPathToValidate);

        if (!expectedLastElementsPath.contains(tag)) {
            String errorMsg = expectedLastElementsPath.size() == 1
                    ? "Only <" + expectedLastElementsPath.getFirst() + "> accepted as last element in '" + xPathToValidate + "'"
                    : "Only any of " + expectedLastElementsPath + " accepted as last element in '" + xPathToValidate + "'";

            throw new IllegalArgumentException(errorMsg);
        }
    }

    private String getLastElementTag(String xpath) {
        // Remove spaces around xpath for consistent parsing
        xpath = xpath.trim();

        // Remove all predicates safely (even nested ones)
        StringBuilder cleanXPath = new StringBuilder();
        int bracketLevel = 0;
        for (char c : xpath.toCharArray()) {
            if (c == '[') {
                bracketLevel++;
            } else if (c == ']') {
                bracketLevel--;
            } else if (bracketLevel == 0) {
                cleanXPath.append(c);
            }
        }

        // Split and find last segment
        String[] segments = cleanXPath.toString().split("/");

        for (int i = segments.length - 1; i >= 0; i--) {
            String segment = segments[i].trim();
            if (!segment.isEmpty()) {
                // Handle axes
                if (segment.contains("::")) {
                    segment = segment.substring(segment.indexOf("::") + 2).trim();
                }
                return segment.equals("*") ? "*" : segment;
            }
        }

        throw new IllegalArgumentException("Invalid XPath expression: " + xpath);
    }

//  ************************** Javascript interaction *******************************


    /**
     * Clicks an element using JavaScript.
     */
    public void makeJavaScriptClick(String path) {
        System.out.println(" -> Retry with JavaScript click.");

        page.evaluate("selector => document.querySelector(path)?.click()", path);
    }

    /**
     * Sets a localStorage item.
     */
    public void setLocalStorageItem(String key, Object value) {
        page.evaluate("([key, value]) => window.localStorage.setItem(key, value)", List.of(key, value));
    }


//  **********************************************************

    public static void sleep() {
        sleep(SLEEP);
    }

    public static void sleep(long l) {
        if (l == 0) {
            return;
        }

        try {
            Thread.sleep(l * 1000);
        } catch (InterruptedException ie) {
            System.err.println(ie.getMessage());
        }
    }

    //    ***************************Loggers**************************************************************

    private void printElapsedTime(Instant startTime, boolean foundElement) {
        Instant finishTime = Instant.now();
        long elapsedSeconds = getElapsedSeconds(startTime, finishTime);
        if (foundElement) {
            System.out.println(" -> found after: " + elapsedSeconds + " seconds");
        } else {
            System.out.println(" -> did NOT find element! Waited for: " + elapsedSeconds + " seconds");
        }
    }

    private long getElapsedSeconds(Instant start, Instant finish) {
        long elapsedMillis = Duration.between(start, finish).toMillis();
        return elapsedMillis / MILLISECONDS_IN_SECOND;
    }

    public ProviderType getProviderTypeFromUrl() {
        String currentUrl = getCurrentUrl();
        return ProviderType.getProviderTypeFromCurrentUrl(currentUrl);
    }

    public String getCurrentUrl() {
        return page.url();
    }

    public void hoverAboveElementAndClickOnTheNoMoreHiddenElement(String hoverAbovePath, String clickOnThisPath) {
        System.out.printf(
                "%nTrying to hover above element '%s' and to click on '%s'%n",
                hoverAbovePath,
                clickOnThisPath
        );

        Locator hoverElement = getFirstLocator(hoverAbovePath);
        Locator elementToClick = getFirstLocator(clickOnThisPath);

        hoverElement.hover();
        elementToClick.click();
    }

    public BoundingBox readElementPosition(String path) {
        Locator element = getFirstLocator(path);
        return element.boundingBox();
    }

    // Click at absolute viewport coordinates (x, y)
    public void clickAtCoordinates(int x, int y) {
        System.out.println("\nTrying to click at screen coordinates: (" + x + ", " + y + ")");
        page.mouse().click(x, y);
    }


    /**
     * Moves the mouse to an element, then slightly offsets it by (1,1).
     *
     * @param path selector for the element
     */
    public void moveMouseSlightly(String path) {
        Locator element = page.locator(path).first();

        // Ensure element is visible and stable
        element.waitFor();

        // Get element bounding box
        BoundingBox box = element.boundingBox();
        if (box == null) {
            throw new RuntimeException("Element has no bounding box (not visible)");
        }

        // Move to element center first
        double startX = box.x + box.width / 2;
        double startY = box.y + box.height / 2;

        page.mouse().move(startX, startY);

        // Move slightly (1px right, 1px down)
        page.mouse().move(startX + 1, startY + 1);
    }

    // Scrolls inside a specific element by a given number of pixels
    public void makeScroll(String path, int scrollPixels) {
        System.out.println("Making " + scrollPixels + " px scroll on '" + path + "'-> ");

        // Locate the scrollable element
        Locator scrollContainer = page.locator(path).first();

        // Execute scroll داخل the element
        scrollContainer.evaluate(
                "(el, pixels) => el.scrollTop = el.scrollTop + pixels",
                scrollPixels
        );

        // Small wait (Playwright way)
        sleep(1);
    }

    // Calculates the combined height of all matching elements, including padding per element
    public int getCombinedHeightOfElements(String path, boolean includedBottomMargin) {
        int totalHeight = 0;

        Locator elements = page.locator(path);
        int count = elements.count();

        for (int i = 0; i < count; i++) {
            Locator element = elements.nth(i);

            int height = calculateElementHeight(element, includedBottomMargin);
            totalHeight += height;
        }

        return totalHeight;
    }

    private int calculateElementHeight(Locator element, boolean includedBottomMargin) {
        Double height = element.boundingBox() != null
                ? element.boundingBox().height
                : 0;

        int bottomMarginHeight = (includedBottomMargin)
                ? Integer.parseInt(element.evaluate(
                "el => getComputedStyle(el).marginBottom"
                    ).toString().replace("px", ""))
                : 0;

        return height.intValue() + bottomMarginHeight;
    }

    public int getElementHeight(String path, boolean includedBottomMargin) {
        Locator element = page.locator(path);
        return calculateElementHeight(element, includedBottomMargin);
    }

    public void scrollElementToTop(String path) {
        Locator element = page.locator(path).first();
        element.waitFor(); // ensure it's attached & visible
        element.evaluate("el => el.scrollTop = 0");
    }

    public void scrollElementToBottom(String path) {
        Locator element = page.locator(path).first();
        element.waitFor(); // ensure element is ready
        element.evaluate("el => el.scrollTop = el.scrollHeight");
    }

}
