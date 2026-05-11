# language: en

Feature: Data report Data

  @automation
  @loginWithAdmin
  Scenario: Validate intervals in DataReport
    Given There is a project with a DataReport
    When 'intervals table' view is opened
    Then The display counter matches context

  @automation
  @loginWithAdmin
  Scenario: DataReport for projects with Blast Standard
    Given There is a Project with Blast Standard
    When 'intervals chart' view is opened
    Then The DataReport Blast Tab is clickable

  @automation
  @loginWithAdmin
  Scenario: DataReport for projects without Blast Standard
    Given There is a Project without Blast Standard
    When 'intervals chart' view is opened
    Then The DataReport Blast Tab is not present

  @automation
  @loginWithAdmin
  Scenario: Create a transient report
    Given there is a project with a 'V12' and transient report
    When 'transients' view is opened
    Then The DataReport Interval tab is not clickable

  # SSD-2636, SSD-2711
  @automation
  @loginWithAdmin
  Scenario: Display and compare dB correction for C50 transient table
    Given there is a project with two measuring point to same C50
    When I set dBCorr '-5' on mp 'C50-1'
    And create an 'transient' report by api
    Then transient table metadata for 'C50-1' should display 'dBCorr' and '-5 dB'
    And transient table measured values for 'C50-1' should be '-5' dB compared to 'C50-2'

  # SSD-2577, SSD-2801
  @automation
  @loginWithAdmin
  Scenario: Display and compare dB correction for C50 interval
    Given there is a project with two measuring point to same C50
    When I set dBCorr '-5' on mp 'C50-1'
    And create an 'interval' report by api
    Then interval chart meta data for 'C50-1' should display 'dBCorr' and '-5'
    Then interval table meta data for 'C50-1' should display 'dBCorr' and '-5 dB'
    And interval table measured values for 'C50-1' should be '-5' dB compared to 'C50-2'

# SSD-4258
  @automation
  @loginWithAdmin
  Scenario: dBCorr for C50 with CustomLn
    Given a C50 report with custom_Ln and dBCorr
    And 'intervals chart' view is opened
    Then the report contains channels
    |LAS|LAeq|L90|L50|L10|L12.5|



