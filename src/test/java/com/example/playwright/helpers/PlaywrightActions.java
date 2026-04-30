package com.example.playwright.helpers;

import com.example.playwright.helpers.enums.AsideSize;
import com.example.playwright.helpers.enums.ProviderType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
        Locator locator = getLocator(path);
        locator.click();
    }

    // Not tested
    public final void appendText(String path, String inputValue) {
        System.out.println("\nTrying to find input field '" + path + "' and add '" + inputValue + "'");
        Locator locator = getLocator(path);
        locator.pressSequentially(inputValue);
    }

    public final void clearAndType(String path, String inputValue) {
        System.out.println("\nTrying to clear input field '" + path + "' and type '" + inputValue + "'");
        Locator locator = getLocator(path);
        locator.fill(inputValue);
    }

    public String findOneElementsText(String path, int timeToWait) {
        Locator locator = getLocator(path);
        return locator.textContent();
    }

    /**
     * Returns text content of a locator.
     */
    public String findOneElementsText(String path) {
        Locator locator = getLocator(path);
        return locator.textContent();
    }

    public String findOneElementsAttribute(final String path, String attributeName) {
        System.out.println("\nTrying to find value of attribute '" + attributeName + "' in '" + path + "'");
        Locator locator = getLocator(path);
        return locator.getAttribute(attributeName);
    }

    // todo: missvisande namn.
    /*
    Bygga in en if-sats i findOneElementsAttribute som säger typ
    Om Locator inte i DOM och attributeName = "Value"
    hämta då med JavaScript
     */
    public String findOneElementsValueAttribute(String path) {
//        System.out.print("\nTrying to find element " + path + " not in DOM and get the text.\n");
//        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
//        WebElement webElement = findOneElement(path, 0);
//        return (String) jsExecutor.executeScript("return arguments[0].value;", webElement);
        return findOneElementsAttribute(path, "value");
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
                ? 100
                : timeoutSeconds * 1000;

        return processElementExistAndVisible(path, failTestIfNotFound, waitTimeMillis);
    }

    public final List<String> findManyElementsTexts(String path) {
        return page.locator(path).allTextContents();
    }

    public final void makeClickOnAllElements(String path) {
        Locator elements = getLocator(path);
        int count = elements.count();
        makeClickOnSomeElements(path, count);
    }

    public void makeClickOnSomeElements(String path, int elementsToClick) {
        Locator elements = getLocator(path);
        int count = elements.count();
        System.out.println("count: " + count);

        for(int i = 0; i < elementsToClick; i++) {
            elements.nth(i).click();
        }
    }

    public int countHowManyElements(String path) {
        Locator elements = getLocator(path);
        return elements.count();
    }

    public int countHowManyVisibleElements(String path) {
        System.out.println("\nTrying to count how many visible elements from'" + path + "'");
        return countHowManyElements(path);
    }

    /**
     * @return True if element is not present, or present but not visible.
     */
    public final boolean elementDoNotExist(final String xpath, int timeToWait) {
        System.out.println("\nTrying to assert that '" + xpath + "' is NOT present.");

        Instant startTime = Instant.now();

        try {
            boolean hidden = page.locator("xpath=" + xpath).isHidden(
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

    //  ************************* Processors *********************************

    private Locator getLocator(String path) {
       return getLocator(path, 0);
    }

    /**
     * Automatically waits until the element is:
     * attached to DOM
     * visible
     * stable (not moving)
     * enabled
     * receiving events
     */
    private Locator getLocator(String path, int timeoutSeconds) {
        Locator locator = page.locator(path);

        int waitTimeMillis = (timeoutSeconds == 0)
                ? 100
                : timeoutSeconds * 1000;

        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(timeoutSeconds * 1000));
            return locator;

        } catch (PlaywrightException e) {
            throw new PlaywrightException("Unable to find element. Perhaps it's hidden?", e);
        }
    }

    private boolean processElementExistAndVisible(String path, boolean failTestIfNotFound, int waitTimeMillis) {
        Locator locator = page.locator(path);

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
        Object result = getLocator(path).evaluate("el => el.tagName.toLowerCase()");
        return result != null ? result.toString() : null;
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
    public void makeJavaScriptClick(String selector) {
        System.out.println(" -> Retry with JavaScript click.");

        page.evaluate(
                "selector => document.querySelector(selector)?.click()",
                selector
        );
    }

    /**
     * Sets a localStorage item.
     */
    public void setLocalStorageItem(String key, Object value) {
        page.evaluate(
                "([key, value]) => window.localStorage.setItem(key, value)",
                List.of(key, value)
        );
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


}
