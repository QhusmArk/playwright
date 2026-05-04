# language: en

#  If @setUpSeleniumWithAdmin is not included its because no Context is prereq.
#  If# @deleteProject is not included its because no Context was required.

Feature: Authentication

  @automation
  Scenario Outline: Log in
    Given I am at login page
    When I enter '<Username>' and '<Password>'
    And click Login
    Then my attempt to login was a <result>
    Examples:
      | Username                       | Password    | result   |
      | autotest_admin_qa_@sigicom.com | Sigicom2022 | success  |
      | autotest_admin_qa_@sigicom.com | sfsefse     | failure  |

  @automation
  Scenario Outline: Reset Password
    Given I am at passreset page
    When I enter my email address as '<Username>'
    Then I can request a password reset
#    And I get an e-mail
    Examples:
      | Username                       | result   |
      | autotest_admin_qa_@sigicom.com | success  |

  @automation
  @manualLoginWithAdmin
  Scenario: Log out
    When I log out
    Then the session is closed

#    SSD-2438
  @automation
  Scenario: Terms-of-Service
    When I am at login page
    Then 'Terms of Service' link leads to 'https://www.sigicom.com/terms-of-service/'
    And 'Contact support' link leads to 'mailto:support@sigicom.com'
    And 'Sigicom' link leads to 'http://www.sigicom.com/'
    And 'Forgot password?' link leads to '/passreset/'




