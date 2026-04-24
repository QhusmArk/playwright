Feature: Multithreading assertion

Feature: Multithreading assertion

#  @admin @user
#  Scenario: Assert thread one
#    Given multithreading is required
#    When the test execution starts
#    Then this scenario records its thread
##    And the browser opens "https://playwright.dev/"
#    And the browser opens the web client
#    And the Playwright homepage is visible
#    And the current user is printed
#    And the user is stored in the browser
#    And the user in the browser should match the current user

  @admin
  Scenario: Assert thread two
    Given multithreading is required
    When the test execution starts
    Then this scenario records its thread
    And the browser opens "https://example.com/"
#    And the browser opens the web client
    And the current user is printed
    And the user is stored in the browser
    And the user in the browser should match the current user

  @user
  Scenario: Assert thread three
    Given multithreading is required
    When the test execution starts
    Then this scenario records its thread
#    And the browser opens "https://example.com/"
    And the browser opens the web client
    And the user logs in
    And the current user is printed
    And the user is stored in the browser
    And the user in the browser should match the current user