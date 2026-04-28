package com.example.playwright.helpers;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.List;

public class PlaywrightActions {

    private static final int TIME_TO_WAIT = 3;
    private static final int SLEEP = 1;

    private final Page page;

    public PlaywrightActions(Page page) {
        this.page = page;
    }

//  **********************************************************

    /**
     * Clicks on a locator with logging.
     */
    public void click(String path) {
        System.out.print("\nTrying to click on " + path + "\n");
        Locator locator = findOneLocator(path);
        locator.click();
    }

    // Not tested
    public final void appendText(String path, String inputValue) {
        System.out.println("\nTrying to find input field '" + path + "' and add '" + inputValue + "'");
        Locator locator = findOneLocator(path);
        locator.pressSequentially(inputValue);
    }

    public final void clearAndType(String path, String inputValue) {
        System.out.println("\nTrying to clear input field '" + path + "' and type '" + inputValue + "'");
        Locator locator = findOneLocator(path);
        locator.fill(inputValue);
    }

    /**
     * Returns text content of a locator.
     */
    public String getText(String path) {
        Locator locator = findOneLocator(path);
        return locator.textContent();
    }

    public String findOneElementsAttribute(final String path, String attributeName) {
        System.out.println("\nTrying to find value of attribute '" + attributeName + "' in '" + path + "'");
        Locator locator = findOneLocator(path);
        return locator.getAttribute(attributeName);
    }

    public final boolean elementExistAndVisible(final String path, boolean failTestIfNotFound) {
        return elementExistAndVisible(path, failTestIfNotFound, 0);
    }

    public final boolean elementExistAndVisible(final String path, boolean failTestIfNotFound, int timeoutSeconds) {
        System.out.println("\nTrying to assert if '" + path + "' is visible.");
        int waitTimeMillis = (timeoutSeconds == 0)
                ? 100
                : timeoutSeconds * 1000;

        return processElementExist(path, failTestIfNotFound, waitTimeMillis);
    }

    public final List<String> findManyElementsTexts(String path) {
        return page.locator(path).allTextContents();
    }

    //  ************************* Processors *********************************

    private Locator findOneLocator(String path) {
       return findOneLocator(path, 0);
    }

    /**
     * Automatically waits until the element is:
     * attached to DOM
     * visible
     * stable (not moving)
     * enabled
     * receiving events
     */
    private Locator findOneLocator(String path, int timeoutSeconds) {
        Locator locator = page.locator(path);

        int waitTimeMillis = (timeoutSeconds == 0)
                ? 100
                : timeoutSeconds * 1000;

        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(waitTimeMillis));
            return locator;

        } catch (PlaywrightException e) {
            throw new PlaywrightException("Unable to find element. Perhaps it's hidden?", e);
        }
    }

    private boolean processElementExist(String path, boolean failTestIfNotFound, int waitTimeMillis) {
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

}
