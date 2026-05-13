# language: en

Feature: Authentication

  @automation
  @loginWithAdmin
  Scenario: Log in
    Given I am at login page
    When I use wrong password for login
    Then I get error messages

  @automation
  @loginWithAdmin
  Scenario: Request password reset
    Given I am at passreset page
    When I enter my email address
    Then I can request a password reset

  @automation
  @loginWithAdmin
  Scenario: Log out
    When I log out
    Then the session is closed

#    SSD-2438
  @automation
  @loginWithAdmin
  Scenario: Terms-of-Service
    When I am at login page
    Then 'Terms of Service' link leads to 'https://www.sigicom.com/terms-of-service/'
    And 'Contact support' link leads to 'mailto:support@sigicom.com'
    And 'Sigicom' link leads to 'http://www.sigicom.com'
    And 'Forgot password?' link leads to '/passreset/'




