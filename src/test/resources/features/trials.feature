Feature: Devices

    # SSD-2756
#    @automation
    @setUpSeleniumWithAdmin
    Scenario: Device connection history - Non cable logger - Can navigate from device details
    Given I navigate to the 'C22' details
    When I click on 'Connection history'
    Then I am redirected to '/status/projects'

  @setUpSeleniumWithAdmin
  Scenario: Change user first name
    When I open UserProfile
    And I try to edit my first name
    Then toast 'User has been updated' is displayed
    And the name change request was a 'success'
