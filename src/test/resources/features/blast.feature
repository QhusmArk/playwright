# language: en

# Otestat:
# Blast journal

Feature: Blast

#SSD-1012
#  Acceptance Criterias:
#  “Load latest 48 h of data” shall generate a Transient Report for the previous 48 h including all MP in the project.
#  If a transient has been recorded during the 48 h the Blast marker shall be highlighted.
#  No grouping of Blasts shall occur.

  @automation
  @loginWithAdmin
  Scenario: Create a blast in a project
    Given there is a project
    When I am at project blast 'create'
    Then I can create a blast

  @automation
  @loginWithAdmin
  Scenario: Copy a blast event in a project
    Given There is a project with a blast
    When I am at project blast 'create'
    Then I can copy a blast

  @automation
  @loginWithAdmin
  Scenario: Search for a blast event in a project
    Given there is a project with two measuring points and two blasts
    When I navigate to project 'blasts'
    Then blast 'Blast_two' can be found in aside

  @automation
  @loginWithAdmin
  Scenario: Edit and delete a blast
    Given There is a project with a blast
    When I am at project blast 'settings general'
    Then I can change the blasts name
    And I can delete the blast

  @automation
  @loginWithAdmin
  Scenario: Verify default headers for project blasts
    Given there is a project with two measuring points and two blasts
    And I am in project 'blasts'
    When aside is 'FULL'
    Then these headers are default
      | Name | Description | Type | Date and time | Rel. VCorr | Created by |
    When aside is 'MEDIUM'
    Then these headers are default
      | Name | Description | Type | Date and time | Rel. VCorr | Created by |
    And no other headers can be selected

    # SSD-2446
  @automation
  @loginWithBlaster
  Scenario: Show create blast link for blasters
    Given there is a project with a blaster
    When I am in project 'blasts'
    Then create new blast link should be visible

  #  SSD-2099
  @automation
  @loginWithAdmin
  Scenario: Validate list item Blast
    Given there is a project with Blast Standard, blasts and saved interval report
    When I navigate to project 'blasts'
    And listitem menu has these options
      |Center map on blast|Blast settings|
    When I select menu option 'Center map on blast'
    Then map is centered on the 'blast'
    When I select menu option 'Blast settings'
    Then my endpoint is 'settings'