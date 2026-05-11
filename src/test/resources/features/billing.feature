# language: en

Feature: Billing report

  @automation
  @loginWithAdmin
  Scenario Outline: Project Measuring Points Billing Report - Verify creation
    Given there is a project with two measuring points and two blasts
    When I navigate to 'Project' Billing Report create
    When I create a '<Period>' Project Measuring Points Billing Report
    Then the 'Project Measuring Points Billing Report' is created
    Examples:
      | Period       |
      | Last month   |
      | Year to date |
      | Custom       |

  @automation
  @loginWithAdmin
  Scenario: Account Devices Billing Report - Verify export formats
    When I navigate to 'Account' Billing Report create
    And I create an Account Devices Billing Report for 'Last month'
      |C22|C50|V12|
    Then I can export by
      | CSV | Excel |

  # SSD-2019
  @noStageEnv
  @automation
  @loginWithAdmin
  Scenario: Account Projects Billing Report - Mp-price should override Project-price
    Given there is a project with default_price and mp with price
    When I create an Account Projects Billing Report by api
    Then the mp price should be visible in the Account Projects Billing Report

# SSD-2019
  @noStageEnv
  @automation
  @loginWithAdmin
  Scenario: Account Projects Billing Report - Mp-price can be set on measuring point
    Given there is a project with an mp and a client
    And I navigate to project measuring point 'settings general'
    When I set price '7' on mp
    And I create an Account Projects Billing Report with 'Test-Auto-Project'
    Then price '7' should be visible in Account Projects Billing Report

# SSD-2019
  @noStageEnv
  @automation
  @loginWithAdmin
  Scenario: Account Projects Billing Report - Mp-price can be set on project
    Given there is a project with an mp and a client
    And I navigate to project 'settings general'
    When I set price '6' on project
    And I create an Account Projects Billing Report with 'Test-Auto-Project'
    Then price '6' should be visible in Account Projects Billing Report

  #  SSD-2277
  @automation
  @loginWithAdmin
  Scenario: Account Devices Billing Report - Validate only sensors
    When I navigate to 'Account' Billing Report create
    Then I can only select sensors for Account Devices Billing Report

    # SSD-3157
  @noStageEnv
  @automation
  @loginWithAdmin
  Scenario: Project Measuring Points Billing Report - Verify default headers with price
    Given there is a project with default_price and mp with price
    When I create a Project Measuring Points Billing Report by api
    Then these Project Measuring Points Billing Report headers are default
      | Report period | Project name | Project ID | Project maintainer | Customer | Customer contact | Project time frame | Measuring point | Measuring point description| Sensor type | Serial number | Active from | Active to | Days active | Price/Measuring point |

  @automation
  @loginWithAdmin
  Scenario Outline: Account Projects Billing Report - Verify creation possible
    When I navigate to 'Account' Billing Report create
    And I create an Account Projects Billing Report for '<Period>'
    Then the 'Account Projects Billing Report' is created
    Examples:
      | Period       |
      | Last month   |
      | Year to date |
      | Custom       |
#    Scenario: Abort

  @automation
  @loginWithAdmin
  Scenario Outline: Account Devices Billing Report - Verify creation possible
    When I navigate to 'Account' Billing Report create
    And I create an Account Devices Billing Report for '<Period>'
      |C22|
    Then the 'Account Devices Billing Report' is created
    Examples:
      | Period       |
      | Last month   |
      | Year to date |
      | Custom       |

  #    SSD-2240
  @noStageEnv
  @automation
  @loginWithAdmin
  Scenario: Account Devices Billing Report - Validate Days Active in last month
    When I navigate to 'Account' Billing Report create
    And I create an Account Devices Billing Report for 'Last month'
      |C22|C50|V12|
    Then no device should have more Days Active than last month had days

    #  SSD-3064 Project Billing Report changes
  @automation
  @loginWithAdmin
  Scenario: Account Projects Billing Report - Validate Days Active for one mp w two devices
    Given there is a project with a measuring point that has two devices
    When I use api to create an Account Projects Billing Report from '2023-11-01 00:00' to '2023-11-30 23:59'
    Then the measuring points has correctly calculated 'Days active'


    #  SSD-3064 Project Billing Report changes
  @automation
  @loginWithAdmin
  Scenario: Account Projects Billing Report - Validate Days Active for two mps w one device each
    Given there is a project for Account Projects Billing Report testing
    When I use api to create an Account Projects Billing Report from '2023-11-01 00:00' to '2023-11-30 23:59'
    Then the measuring points has correctly calculated 'Days active'

      # SSD-3150
  @automation
  @loginWithAdmin
  Scenario: Project Measuring Points Billing report - Project start
    Given there is a project
    When I navigate to 'Project' Billing Report create
    And select 'Custom' and open 'From' calendar and select 'Project start'