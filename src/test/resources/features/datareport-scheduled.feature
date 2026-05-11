# language: en

Feature: Data report - Scheduled report

#  @automation
#  @loginWithAdmin

  @loginWithAdmin
  @automation
  Scenario: Scheduled report — create - key availability
    Given there is a project
    And I am at project 'scheduled_reports create'
    When I verify report formats:
      | ReportType | Formats           |
      | Interval   | CSV;PDF;Excel     |
      | Transient  | CSV;PDF;Excel     |
      | Noise      | PDF               |
    And I verify report content:
      | ReportType | Format | Content                      |
      | Interval   | PDF    | Interval charts;Multi report    |
      | Interval   | CSV    | Table                        |
      | Interval   | Excel  | Table                        |
      | Transient  | PDF    | Table                        |
      | Transient  | CSV    | Table                        |
      | Transient  | Excel  | Table                        |
      | Noise      | PDF    | Table                        |
    And I verify schedule options:
      | Repeat | Send on                                  |
      | Daily    | Every day;Weekdays only                  |
      | Weekly   | Monday;Tuesday;Wednesday;Thursday;Friday;Saturday;Sunday |

  @loginWithAdmin
  @automation
  Scenario: Scheduled report — create - measuring point limits
    Given there is a project with 30 measuring points
    And I am at project 'scheduled_reports create'
    Then SDR have limit on 25 measuring points

  @loginWithAdmin
  @automation
  Scenario: SDR - aside
    Given an SDR project
    When I am at project 'scheduled_reports'
    And aside is 'COMPACT'
    Then I expect aside 'COMPACT' to show correct status colour
    When aside is 'MEDIUM'
    Then I expect aside 'MEDIUM' to show correct status colour