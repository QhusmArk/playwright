Feature: Multithreading assertion

Feature: Multi scenarios

  @user
  Scenario: Make navigation
    When I navigate to account 'devices'
    Then I validate url contains 'devices'

  @loginWithAdmin
  Scenario: Make manual login
    When I navigate to account 'devices'