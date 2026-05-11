# language: en

Feature: Status

  # tests that status logic is implemented in IN Client
#  NB. This test is dependent on the GUI zoom-function. If these tests break a lot it might be a
#  good idea to validate that the automatic zoom works correctly.
#  SSD-2589
  @noStageEnv
    @automation
    @loginWithAdmin
  Scenario Outline: Test map status logic for Project, MP and Device(MP.Sensor) at project level
    Given There is a project called aaa-'<testCase>'
    And I navigate to project 'overview'
    Then I validate that project map status is correct
    And I validate that mp map status is correct
    And I validate that device map status is correct
    Examples:
      | testCase |
      | all_ok    |
      | old_device  |
      | old_mp      |
      | old_project |
      | mp_off      |

    # tests that status logic is implemented in IN Client
  #  SSD-2589
  @automation
  @loginWithAdmin
  Scenario Outline: Test aside status logic for MP and Device(MP.Sensor) at project level
    Given There is a project called aaa-'<testCase>'
    And I navigate to project 'overview'
    And I validate that mp list status is correct
    And I validate that device list status is correct
    Examples:
      | testCase |
      | all_ok    |
      | old_device  |
      | old_mp      |
      | old_project |
      | mp_off      |

# tests that status logic is implemented in IN Client
  @automation
  @loginWithAdmin
  Scenario Outline: Test map- and list status logic for Project at account level
    Given There is a project called aaa-'<testCase>'
    When I look for the project
    Then I validate that project map status is correct
    And I validate that project list status is correct
    Examples:
      | testCase |
      | all_ok    |
      | old_device  |
      | old_mp      |
      | old_project |
      | mp_off      |
      | project_off |

#    tests that the changed activity state is rendered in GUI
  @automation
  @loginWithAdmin
  Scenario: Account Level - Make Project inactive from map
    Given There is a project called aaa-'all_ok'
    And I am in account 'projects'
    When I set the project toggle to Inactive from project map icon
    Then The project should be inactive in List
    And The project should be inactive in Map

#    tests that the changed activity state is rendered in GUI
  @automation
  @loginWithAdmin
  Scenario: Account Level - Make Project inactive from details view
    Given There is a project called aaa-'all_ok'
    When I set the project toggle to Inactive from project list
    Then The project should be inactive in List
    And The project should be inactive in Map

#    tests that the changed activity state is rendered in GUI
  @automation
  @loginWithAdmin
  Scenario: Project Level - Make Measuring Point inactive from details view
    Given There is a project called aaa-'all_ok'
    When I set the mp toggle to OFF from mp detail panel
    Then The mp should be inactive in List
    And The mp should be inactive in Map

  #    SSD-3154, -3252
  @automation
  @loginWithAdmin
  Scenario: Verify no empty labels
    Given there is a message rule with all labels 'OFF'
    And C22 are ready to trigger
    When I trigger a 'C22'
    Then text message contain labels
      | Guide value: 25.0 |

