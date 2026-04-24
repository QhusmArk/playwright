Feature: Multithreading assertion

Feature: Multithreading assertion

  Scenario: Assert thread one
    Given multithreading is required
    When the test execution starts
    Then this scenario records its thread
    And the browser opens "https://playwright.dev/"
    And the Playwright homepage is visible
    And the current user is printed
    And the user is stored in the browser
    And the user in the browser should match the current user

  Scenario: Assert thread two
    Given multithreading is required
    When the test execution starts
    Then this scenario records its thread
    And the browser opens "https://example.com/"
    And the current user is printed
    And the user is stored in the browser
    And the user in the browser should match the current user