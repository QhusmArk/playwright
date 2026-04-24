Feature: Multithreading assertion

Feature: Multithreading assertion

#  @admin @user
#  Scenario: Assert thread one
#    Given multithreading is required
#    When the test execution starts
#    Then this scenario records its thread
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
    And the browser opens the web client
    And the current user is printed

  @user
  Scenario: Assert thread three
    Given multithreading is required
    When the test execution starts
    Then this scenario records its thread
    And the browser opens the web client
    And the user logs in
    And the current user is printed

  @user
  Scenario: Make navigation
    When I navigate to account 'devices'
    Then I validate url contains 'devices'

  @loginWithAdmin
  Scenario: Make manual login
    When I navigate to account 'devices'