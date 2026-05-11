# language: en

Feature: Project CRUD

  @noUpgEnv
  @automation
  @loginWithAdmin
  Scenario: Create a project from the client
    When A project is created
    And I am in account 'projects'
    Then the project should be visible in aside

  @automation
  @loginWithAdmin
  Scenario: Verify default headers for account Projects
    Given I am in account 'projects'
    When aside is 'FULL'
    Then these headers are default
      | Project ID | Project name | Active from | Active to | Customer | Customer contact | Project maintainer |
    When aside is 'MEDIUM'
    Then these headers are default
      | Project ID | Project name | Active from | Active to | Customer |
    And no other headers can be selected

  # SSD-3126
  @automation
  @loginWithAdmin
  Scenario: Change project description
    Given there is a project
    And I am at project 'settings'
    When I change the project description
    Then the new project description will be visible in settings panel

  @automation
  @loginWithAdmin
  Scenario: Write a comment in a project
    Given there is a project
    And I navigate to project 'overview'
    When A comment is added
    Then in list there's 1 comment

  #  SSD-2275
  @automation
  @loginWithAdmin
  Scenario: Added or removed comment should impact Comment list and panel at once
    Given there is a project with a comment
    And I navigate to project 'overview'
    When I add a comment
    Then the new comment should be visible in Comment list and in Comments panel
    And when I remove the new comment the previous comment should appear in list and panel

    #  SSD-2293
  @automation
  @loginWithAdmin
  Scenario: No redirect after user is removed from project
    Given there is a project with a user connected to the project
    When I remove the project from the user
    Then I am to remain at user details page

    #    SSD-2401
  @noUpgEnv
  @automation
  @loginWithAdmin
  Scenario: Selected location search hit places marker on map
    When user has selected a search result in Find location
    Then a large pin should appear on the map

# SSD-2862
  @automation
  @loginWithClient
  Scenario: SubUser should not see create project plus button
    When I am in account 'overview'
    Then I shall not be able to see Create project button

  #  SSD-2985
  @noUpgEnv
  @automation
  @loginWithAdmin
  Scenario: Project creation logic - account overview - non-unique name and projectID
    Given there is a project
    When I start creating a project from account overview
    And for new 'project' I use 'non-unique' name and non-unique id
    Then 'Project ID *' has validation message 'Project with this name and project id already exists'

  #  SSD-2985
  @noUpgEnv
  @automation
  @loginWithAdmin
  Scenario: Project creation logic - account overview - unique project name, non-unique id
    Given there is a project
    When I start creating a project from account overview
    And for new 'project' I use 'unique' name and non-unique id
    Then toast 'Project with this Project id and Company already exists.' is displayed

  #  SSD-2985
  @automation
  @loginWithAdmin
  @noUpgEnv
  Scenario: Project creation logic - bulk action - non-unique name and projectID
    Given there is a project
    And I am in account 'devices'
    And I select these and make 'Create project' bulk action
      | C22 | C50 |
    And for new 'bulk project' I use 'non-unique' name and non-unique id
    Then 'Project ID *' has validation message 'Project with this project id already exists'

  #  SSD-2985
  @automation
  @loginWithAdmin
  @noUpgEnv
  Scenario: Project creation logic - bulk action - unique project name, non-unique id
    Given there is a project
    And I am in account 'devices'
    And I select these and make 'Create project' bulk action
      | C22 | C50 |
    And for new 'bulk project' I use 'unique' name and non-unique id
    Then notify message 'Some values are invalid, please correct them first!' is displayed

    #    SSD-2914 Bulk action, Project
  @automation
  @loginWithAdmin
  Scenario: Bulk action - account devices - create project
    Given I am in account 'devices'
    And I select these and make 'Create project' bulk action
      | C22 | C50 |
    And selected devices are listed in Create measuring points
      | C22 | C50 |
    When I create the bulk action project
    Then I am redirected to 'measure_points'
    And there are '2' 'measure_points' in aside

  # SSD-2197
  @automation
  @loginWithAdmin
  Scenario: Change project name
    Given there is a project
    And I am at project 'settings'
    When I rename the project
    Then the new project name will be visible in settings panel
    And project name and id are visible in header dropdown

    #  SSD-3952
  @automation
  @loginWithAdmin
  Scenario: Project time in project timezone
    Given a project in Istanbul timezone
    When I am in account 'projects'
    And aside is 'MEDIUM'
    Then show project 'aside time' in project.tz
    When I am at account 'projects details'
    Then show project 'project details time' in project.tz
    When I navigate to project 'settings'
    Then show project 'project settings time' in project.tz
    When I navigate to project 'settings general'
    Then show project 'project settings general time' in project.tz

