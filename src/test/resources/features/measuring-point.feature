# language: en

Feature: Measuring Points CRUD

  @automation
  @loginWithAdmin
  Scenario: Create a measuring point in the client
    Given there is a project
    And I navigate to project 'overview'
    When I create a Measuring Point from overview plus button
    Then The measuring point should be visible in the list
    And The measuring point should be visible on the map

  @automation
  @loginWithAdmin
  Scenario: Edit and delete a measuring point
    Given there is a project with a measuring point
    When I navigate to project measuring point 'settings general'
    Then I can change the measuring points name
    And I can delete the measuring point

  # SSD-2019, SSD-3000
  @noStageEnv
  @automation
  @loginWithAdmin
  Scenario: Mp-price can be set on measuring point on creation
    Given there is a project
    When I navigate to project measuring point 'create'
    And I create a Measuring Point using 'mp_name', '7' and 'C22'
    And I navigate to project measuring point 'settings general'
    Then Mp price is '7'

  # SSD-2417
  @automation
  @loginWithAdmin
  Scenario: Go-to-device button at MP Connected device view
    Given there is a project with several measuring points
    And I click on Go to device
    Then I am redirected back to 'settings/devices'

    # SSD-2434
  @automation
  @loginWithAdmin
  Scenario: Testing Mp menu - Measuring point settings
    Given there is a project with a S50
    And I navigate to project 'measuring points'
    When I select menu option 'Measuring point settings'
    Then I am redirected to 'settings'
    And the panel closes with 'X'

  # SSD-2434
  @automation
  @loginWithAdmin
  Scenario: Testing Mp menu - Center map on measuring point
    Given there is a project with a S50
    And I navigate to project 'measuring points'
    When I select menu option 'Center map on measuring point'
    Then map is centered on the 'measuring point'

  # SSD-2446
  @automation
  @loginWithClient
  Scenario: Don’t show create mp link for clients
    Given there is a project with a client
    And I am in project 'measuring points'
    Then create new mp link should not be visible

  # SSD-2306
  @automation
  @loginWithAdmin
  Scenario: Devices should be searchable if tab is used to get to input field
    Given there is a project
    And I navigate to project 'overview'
    When I click on Create MP from Aside Header
    And tab to device name
    Then a panel with all devices should appear

  # SSD-2545
  @automation
  @loginWithAdmin
  Scenario: Changed redirect after MP create
    Given there is a project
    And I navigate to project measuring point 'create'
    When I create a Measuring Point
    Then the panel has an 'X' button
    And when I click on X then I am redirected to 'settings'

  # SSD-2645, SSD-2960
  @automation
  @loginWithAdmin
  Scenario: Create a data report from measuring point menu
    Given there is a project with a measuring point
    When I navigate to project 'measuring points'
    And listitem menu has these options
      |Center map on measuring point|Measuring point settings|Device details|Create data report|
    And I select menu option 'Create data report'
    Then I am redirected to a new temporary data report
    And report duration is 'now minus seven days' to 'today 23:59'

  #    SSD-2992 Bulk action, MP
  @automation
  @loginWithAdmin
  Scenario: Bulk actions - project mp - Validate Edit Time Frame default options
    Given there is a project with several measuring points
    And I navigate to project 'measuring points'
    When I select all 'measure_points' for bulk action
    And the panel has '3' measuring points, time frame is now and all toggles are OFF

  #    SSD-2992 Bulk action, MP
  @automation
  @loginWithAdmin
  Scenario: Mp Bulk actions - Change mp date by Edit Time Frame
    Given there is a project with several measuring points
    And I navigate to project 'measuring points'
    And I select all 'measure_points' for bulk action
    When I click toggle 'Until further notice' and 'not save'
    When I click toggle 'Off' and 'save'
    Then all measuring points time_frame and connected device date is '2038-01-01'

    #  SSD-3020
  @automation
  @loginWithAdmin
  Scenario: Validate new measuring point get now time
    Given there is a project
    And I navigate to project measuring point 'create'
    Then time frame for the new mp is 'today' and 'now'

    # SSD-3116
  @automation
  @loginWithAdmin
  Scenario: Bulk action - create data report from measuring point list
    Given there is a project with a C22 and interval report
    And I am in project 'measuring points'
    When I select these and make 'Create data report' bulk action
      | C22-1 |
    Then I am redirected to a new temporary data report
    And report duration is 'now minus seven days' to 'today 23:59'
    When I close the report
    Then I am redirected to 'measuring points'

      #  SSD-3278
  @automation
  @loginWithAdmin
  Scenario: Map marker - new location by input of coordinates
    Given there is a project with a measuring point
    And I navigate to project 'measuring points'
    And the mp is visible on map
    When I type a new mp location that is outside current zoom level
    Then the map is updated to show the new location

        #  SSD-3278
  @automation
  @loginWithAdmin
  Scenario: Map marker - new location by input
    Given there is a project with a measuring point
    And I am at measuring point 'coordinates'
    When I set location by input
    And mp will have new location

    #  SSD-3278, refactor to Map.feature?
  @automation
  @loginWithAdmin
  Scenario: Map marker - new location by Pin_on_map
    Given there is a project with a measuring point
    And I am at measuring point 'coordinates'
    And the mp is visible on map
    Then I can use Pin-on-map to change location
    And mp will have new location

      # SSD-2019
  @automation
  @loginAsClient
  Scenario: SubUsers should not see project- or mp-price settings
    Given there is a project with an mp and a client
    Then neither project- nor mp price settings are available

    #SSD-3361
  @automation
  @loginWithAdmin
  Scenario: Active channels list
    Given a C50 report with custom_Ln and dBCorr
    When I navigate to project measuring point 'active channels'
    Then channel wrappers are
      |LAS, LCS, LAeq, LCeq|LAF, LCF, LAeq, LCeq|LCP, LCeq|LAeq-rolling, LCeq-rolling|LAeq-accumulated, LCeq-accumulated|Statistics|
    And channel toggles are 24