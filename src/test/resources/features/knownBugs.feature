Feature: Bug regression tests
#  All tests here should succeed, i.e., have Then-step that will fail when bug is fixed.
#  Tests that, within reasonable workload, cannot be altered to succeed, shall be put into quarantiedE2ETests.feature


#   https://sigicom.atlassian.net/browse/SSD-2073
#  The bug is not expected to be fixed as of Aug 27th 2025
  @automation
  @loginWithAdmin
  Scenario: Mp without Guide Value has no data in Blast Journal
    Given there is a guide value project
    When 'blast journal' view is opened
    Then the mp without guide value is added to table, but not showing any data

#    Known bugs,
#       - labels are present when no value
#       - Guide value is shown when no value toggle are OFF
#    When fixed, move to system-test


#  https://sigicom.atlassian.net/browse/SSD-3013
  @automation
  @loginWithAdmin
  Scenario: Different aside icon status for Blast icon
    Given there is a project with two measuring points and two blasts
    And I am in project 'blasts'
    Then all blast icons has 'primary' color in 'COMPACT'
    When aside is 'MEDIUM'
    Then all blast icons has 'disabled' color in 'MEDIUM'

#    SSD-3185, Agenda list has this.project
#  Merge with "Scenario: Copy agenda from other project - popup" when fixed
  @automation
  @loginWithAdmin
  Scenario: Copy agenda list has current project
    Given there is a project with an Agenda and a client
    When I navigate to project 'overview'
    And click on aside 'Project settings'
    And click on panel 'Agendas'
    And I click on 'Copy agenda' button
    Then I see my project in the copy agenda popup

#    https://sigicom.atlassian.net/browse/SSD-2228, Usage not calculated correct
  @automation
  @loginWithAdmin
  Scenario: Account Device Billing Report - October 31 day usage
    When I create an Account Device Billing Report by api
    Then Account Device Billing Report usage is '96.88 %'

#    https://sigicom.atlassian.net/browse/SSD-2228, Usage not calculated correct
  @automation
  @loginWithAdmin
  Scenario: Measuring Point Billing Report - October 31 day usage
    Given there is a project with a measuring point
    When I create a Project Measuring Points Billing Report by api
    Then Project Measuring Point Billing Report usage is '96.88 %'