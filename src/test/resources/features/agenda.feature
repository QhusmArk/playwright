# language: en

Feature: Agenda

  ##### Project settings #####

  @automation
  @setUpSeleniumWithAdmin
  Scenario: Validate that an agenda can be created
    Given there is a project
    And I navigate to project 'overview'
    When I create agenda 'a-agenda-name'
    And timeslot 'daytime' is created with start time '08:00', end time '16:00' and days
      | Mon | Tue | Wed | Thu | Fri |
    And timeslot 'evening' is created with start time '17:00', end time '22:00' and days
      | Mon | Tue | Wed | Thu | Fri |
    And timeslot 'weekend' is created with start time '10:00', end time '16:00' and days
      | Sat | Sun |
    Then each created timeslots time is visible
      | 08:00- 16:00 | 10:00- 16:00 | 17:00- 22:00 |

  @automation
  @setUpSeleniumWithAdmin
  Scenario: It should not be possible to create agendas with the same name
    Given there is a project with an Agenda
    When An attempt to create an agenda with the same name as already existing
    Then toast 'Agenda with this Name and Project already exists.' is displayed

  @automation
  @setUpSeleniumWithAdmin
  Scenario: In project settings agenda name and time shall be displayed
    Given there is a project with an Agenda
    Then Name and time is displayed

  @automation
  @setUpSeleniumWithAdmin
  Scenario: Only agendas that belong to the project should be visible in project settings
    Given there is a project with an Agenda
    Then only the project agendas are visible

  @automation
  @setUpSeleniumWithAdmin
  Scenario: Agenda can be renamed
    Given there is a project with an Agenda
    Then I can rename the agenda

  @automation
  @setUpSeleniumWithAdmin
  Scenario: Inform that when a agenda is deleted it will be removed from all measuring points
    Given there is a Project with an MP that has an Agenda
    When I delete the agenda
    Then the agenda is also deleted from the measuring point

      #  SSD-3147 Copy Agenda
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Copy agenda from other project - success and fail
    Given there is a project
    And I navigate to project 'agendas'
    When I click on 'Copy agenda' button
    And I try to copy an agenda
    Then the agenda is copied to my project
    When I try to copy same agenda again
    Then I get message 'Agenda with this Name and Project already exists.'

  #  SSD-3181, Copy Agenda
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Copy agenda from other project - popup
    Given there is a project with an Agenda and a client
    And I navigate to project 'agendas'
    When I click on 'Copy agenda' button
    Then the copy agenda popup show only active projects
    And I find 'test-auto-project' in copy agenda popup
    And I find 'Agenda NO:1' in copy agenda popup

  ##### Timeslots #####

  @automation
  @setUpSeleniumWithAdmin
  Scenario: It should not be possible to create timeslots with the same name
    Given there is a project with an Agenda
    When An attempt to create a timeslot with the same name as already existing
    Then The message that contains 'Label already exists' is displayed

  @automation
  @setUpSeleniumWithAdmin
  Scenario: It should not be possible to create overlapping timeslots
    Given there is a project with an Agenda
    When An attempt to create a timeslot that is overlapping
    Then The message that contains 'overlaps with' is displayed

  @automation
  @setUpSeleniumWithAdmin
  Scenario: It should be possible to update a timeslot
    Given there is a project with an Agenda
    When timeslot 'Helg' is updated to start '12:00' and stop '13:00'
    Then timeslot 'Helg' has duration '12:00' and stop '13:00'

  @automation
  @setUpSeleniumWithAdmin
  Scenario: It should be possible to delete a timeslot
    Given there is a project with an Agenda
    When a timeslot is deleted
    Then There is one timeslot less than before

  #  B25-146
  @automation
  @setUpSeleniumWithAdmin
  Scenario Outline: Agenda - Creation of timeslot do not overlap
    Given there is a project with an empty agenda
    Then I will not get '<error>'
    Examples:
    |error|
    |ts_2 overlaps with ts_1|
    |ts_1 overlaps with ts_2|

    ##### Measuring points #####

  @automation
  @setUpSeleniumWithAdmin
  Scenario: It should be possible to add an agenda to a measuring point
    Given There is a Project with an MP that has no Agenda
    When The agenda is added to a measuring point
    Then Validate that the measuring point has a connected agenda

  @automation
  @setUpSeleniumWithAdmin
  Scenario: When a timeslot is deleted from the agenda then it also should be removed from the measuring point
    Given there is a Project with an MP that has an Agenda
    And The same number of timeslots should be in the measuring point as in the agenda
    When a timeslot is deleted
    Then The same number of timeslots should be in the measuring point as in the agenda

  @automation
  @setUpSeleniumWithAdmin
  Scenario: Copy agenda settings from another Measuring point
    Given There is a project that has one MP with Agenda and one MP with no Agenda
    When Other Measuring Point copy Agenda settings from previous Measuring Point
    Then Validate that both MPs has the same Agenda

  #  SSD-3182, -3147
  @automation
  @setUpSeleniumWithClient
  Scenario: Validate subUser cannot see agenda button
    Given there is a project with an Agenda and a client
    When I navigate to project 'settings agendas'
    Then these buttons are not present
      |Create agenda|Copy agenda|




