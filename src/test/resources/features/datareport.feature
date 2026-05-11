# language: en

Feature: Data report CRUD

  @automation
  @loginWithAdmin
  Scenario Outline: Create and update a rolling time interval report
    Given there is a project with a measuring point
    And I navigate to project 'data reports create'
    When I create a rolling '<time>' interval report
    Then the report can be updated
    Examples:
      | time     |
      | 24 hours |
      | 7 days   |
      | 28 days  |

  @automation
  @loginWithAdmin
  Scenario: Create a static time interval report
    Given there is a project with a measuring point
    And I navigate to project 'data reports create'
    When I create a static time interval report
    Then there is a temporary interval report in the list

  @automation
  @loginWithAdmin
  Scenario: Save an interval report
    Given there is a project with a temporary interval report
    And I am in project 'views'
    When I 'save the temporary interval' report
    Then the 'saved' report is found in list

  @automation
  @loginWithAdmin
  Scenario: Share a interval report
    Given there is a project with a saved interval report
    And I am in project 'views'
    When I 'share the saved' report
    Then the 'shared' report is found in list

  @automation
  @loginWithAdmin
Scenario Outline: Filter which data reports to show in list
    Given There is previously created data reports in state temporary, saved and shared
    And I am in project 'views'
    When Reports are filtered on '<filter_selection>'
    Then Only '<filter_selection>' reports are shown in the left list
    Examples:
      | filter_selection |
      | Temporary        |
      | Saved            |
      | Shared           |

  @automation
  @loginWithAdmin
  Scenario: Verify default headers for project Data reports
    Given there is a project with an interval report
    And I am in project 'views'
    When aside is 'FULL'
    Then these headers are default
      | Name | From | To | Status |
    When aside is 'MEDIUM'
    Then these headers are default
      | Name | From | To | Status |
    And no other headers can be selected

  @automation
  @loginWithAdmin
  Scenario: Delete all previously created Data reports
    Given There is previously created data reports in state temporary, saved and shared
    And I am in project 'views'
    When I delete all data reports
    Then there are no reports in aside

# SSD-2872
  @automation
  @loginWithAdmin
  Scenario: Save data report button deactivated if no input
    Given there is a project with a temporary interval report
    And I navigate to project 'data reports'
    When I select menu option 'Save'
    Then save button should be 'disabled'

  # SSD-2645
  @automation
  @loginWithClient
  Scenario: Validate subUser cannot create a data report from measuring point menu
    Given there is a project with an mp and a client
    When I navigate to project 'measuring points'
    Then listitem menu has these options
      |Center map on measuring point|Measuring point settings|

#    Transfer below to new feature file DataReport_vibration/noise_report

  @automation
  @loginWithAdmin
  Scenario: Validate no copy possible
    Given a vibration report project
    When I open the vibration report
    Then meatball menu gives
      |Update|Rename|Share|Export|Delete|

  @automation
  @loginWithAdmin
  Scenario: Open pre created Vibration report
    Given a vibration report project
    When I open the vibration report
    Then Vmax and Vper is as expected

  @automation
  @loginWithAdmin
  Scenario: Create Vibration report
    Given a vibration report project
    When I navigate to project 'measuring points'
    And I search for 'C22-1'
    And I select menu option 'Create vibration report'
    And I select vibration report date '2025-12-04', time '00:00', duration '7 days'
    Then I am redirected to 'vibration report'

  @automation
  @loginWithAdmin
  Scenario: Create Vibration report from aside - validator
    Given a vibration report project
    And I navigate to project measuring point 'vibration-report'
    And I set Vper threshold for each timeslot
    When I navigate to project 'measuring points'
    And I search for 'C22-1'
    Then listitem menu has these options
      |Center map on measuring point |Measuring point settings|Device details|Create data report|Create vibration report|
    And I search for 'C22-2'
    Then listitem menu has these options
      |Center map on measuring point |Measuring point settings|Device details|Create data report|

  @automation
  @loginWithAdmin
  Scenario: Add Vibration report agenda to mp
    Given a Project with an MP that has no Agenda
    When I navigate to project measuring point 'vibration-report'
    Then I set Vper threshold for each timeslot

  @noProdEnv
  @automation
  @loginWithAdmin
  Scenario: Validate max and Vper calculation
    Given a vibration report project
    When I open the vibration report
    Then max- and vper calculations are correct
