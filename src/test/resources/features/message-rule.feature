# language: en

Feature: Message Rules CRUD

  @automation
  @loginWithAdmin
  Scenario: Create a message rule
    Given there is a project with two measuring points and two blasts
    When I create a message rule
    Then I see the new message rule in aside

  @automation
  @loginWithAdmin
  Scenario: Edit and delete a message rule
    Given there is a project with a message rule
    When I am at project message rule 'settings general'
    Then I can change the message rule name
    And I can delete the message rule

  @automation
  @loginWithAdmin
  Scenario: Verify default headers for project message rules
    Given there is a project with a message rule
    And I am in project 'message_rules'
    When aside is 'FULL'
    Then these headers are default
      | Name | Measuring points | Recipients | Status | Start date | End date |
    When aside is 'MEDIUM'
    Then these headers are default
      | Name | Measuring points | Recipients | Status |
    And no other headers can be selected

  #    SSD-2301
  @automation
  @loginWithAdmin
  Scenario: Message Rule with absolute values on MP with C50
    Given there is a project with a C50
    Then I can create a message rule with absolute value

  # SSD-3021, SSD-3130
  @automation
  @loginWithAdmin
  Scenario: Verify show projects user toggle
    Given there is a maxed out project
    And I am at project message rules settings 'recipients'
    Then there is a toggle "Show project's users" in "OFF"
    When I set toggle "Show project's users" to "ON"
    Then all users in the project are visible

  #  SSD-3153
  @automation
  @loginWithAdmin
  Scenario: Validate V10 and Guide Value label
    Given there is a guide value project
    And C22 are ready to trigger
    When I trigger a 'C22'
    Then I get three sms notifications

  #  SSD-3154, -3153
  @automation
  @loginWithAdmin
  Scenario: Verify labels in GUI and SMS
    Given there is a message rule with all labels 'ON'
    And C22 are ready to trigger
    When I trigger a 'C22'
    Then text message contain labels
      | Company: Sigicom AB| Project: test-auto-project | Notification: MR_labels ON | Time: | Sensor: | Value: | Guide value: | Time: | Standard: |

    #    SSD-3539
  @automation
  @loginWithAdmin
  Scenario: Display mp description in message rule
    Given there is a project with several measuring points
    When I navigate to project message rules 'create'
    Then mp description is visible

      # SSD-4015
  @automation
  @loginWithAdmin
  Scenario: Create - Validate only vib sensors for TRANS MR
    Given a project with vib and noise
    And I navigate to project message rules 'create'
    When I select 'E-mail transient report' for message rule
    Then I get a warning message

    #  SSD-4158
  @automation
  @loginWithAdmin
  Scenario: Creation of user and adding to existing message_rule
    Given a project with a message rule with recipient
    When I navigate to project 'users create'
    And I create a 'Client' from project with access to
      |MR_trigger|
    Then message rule recipients are 2