# language: en

Feature: Users

  @automation
#  @manualLoginWithAdmin
  @loginWithAdmin
  Scenario: Change user first name
    When I open UserProfile
    And I try to edit my first name
    Then toast 'User has been updated' is displayed
    And the name change request was a success

  @noUpgEnv
  @automation
#  @manualLoginWithAdmin
  @loginWithAdmin
  Scenario: Change user language
    When I open UserProfile
    And I choose a different language
    Then toast 'User has been updated' is displayed
    And my language shall not be the same as before

# SSD-2035
# SSD-2036, -4223, -2035
  @noUpgEnv
  @automation
  @loginWithAdmin
  Scenario: Calendar language should depend on users settings
    When user language is 'English'
    Then week starts with 'Sun'
    When user language is 'Swedish'
    Then week starts with 'Mån'
    When user language is 'French'
    Then week starts with 'Lun'
    When user language is 'German'
    Then week starts with 'Mon'
    When user language is 'Norwegian'
    Then week starts with 'Man'
    # reset user to English
    And user language is 'English'

  @automation
  @loginWithAdmin
  Scenario: Verify default headers for account Users
    When I am in account 'users'
    And aside is 'FULL'
    Then these headers are default
      | Name | Customer company | Role | E-mail | Mobile phone | Language | INFRA Net access |
    When aside is 'MEDIUM'
    Then these headers are default
      | Name | Customer company | Role | E-mail | Mobile phone | Language | INFRA Net access |
    And no other headers can be selected

  @automation
  @loginWithAdmin
  Scenario: Verify default headers for project Users
    Given there is a project with a client
    And I am in project 'users'
    When aside is 'FULL'
    Then these headers are default
      | Name | Customer company | Role | E-mail | Mobile phone | Language | INFRA Net access |
    When aside is 'MEDIUM'
    Then these headers are default
      | Name | Customer company | Role | E-mail | Mobile phone | Language | INFRA Net access |
    And no other headers can be selected

# SSD-2767, SSD-2957
  @automation
  @loginWithAdmin
  Scenario: Validate toggle in settings for subUsers
    Given there is a project with a client
    And I am in the clients settings page
    Then the list of active projects contains '1' active project
    When I set the project toggle to Inactive from project list
    And I am in the clients settings page
    Then the list of active projects contains '0' active project

  #  SSD-2841
  @automation
  @loginWithAdmin
  Scenario: Validate email requirement when creating user
    When I create a user without email
    Then notify message 'Some values are invalid, please correct them first!' is displayed

        # SSD-2798
  @automation
  @loginWithAdmin
  Scenario: Account level - Create and delete a user with email sent
    Given I am at account 'users create'
    When I create a user with email sent
    Then toast "Sent invite e-mail to 'qa_fname.lname@sigicom.com'" is displayed
    Then aside contain the new user
    And I can delete the new user

  # SSD-2798
  @automation
  @loginWithAdmin
  Scenario: Account level - Create and delete a user without email sent
    Given I am at account 'users create'
    When I create a user without email sent
    Then no toast is displayed

  # SSD-3030
  @automation
  @loginWithAdmin
  Scenario: Project level - User creation and redirection
    Given there is a project
    And I am at project 'users manage'
    When I click on '+ Create user' button
    Then I remain at 'project'
    And current project is pre-selected for access
    When I create a user without email sent
    Then I remain at 'project'
    When I delete the new user
    Then I remain at 'project'

    #   SSD-3274
  @automation
  @loginWithUser
  Scenario: User are only permitted to create subUsers
    When I navigate to account 'users create'
    Then dropdown contains 'role'
      |Client+|Client|Blaster|

    #  SSD-3272
  @automation
  @loginWithAdmin
  Scenario: Supported languages
    Given I navigate to account 'users create'
    Then dropdown contains 'language'
      |English|Swedish|French|German|Norwegian|
    Then dropdown contains 'role'
      |Administrator|User|Client+|Client|Blaster|

    # SSD-3033
  @automation
  @loginWithAdmin
  Scenario: Users create panel contains message rules list
    Given there is a project with an inactive message rule
    When I am at project 'users create'
    Then the create user panel has a list of projects
    And the create user panel has a list of message rules