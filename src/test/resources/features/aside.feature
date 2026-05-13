# language: en

Feature: Aside

# Test general filter functionality for account level provider objects.
  @automation
  @loginWithAdmin
  Scenario Outline: Filter on Company level
    Given I am in account '<Provider>'
    And this filter '<filter>' is active
    When I change to filter '<this filter>'
    Then this filter '<expected filter>' is active
    Examples:
      | Provider | filter                 | this filter           | expected filter |
      | projects | Active only            | All projects          | All projects |
      | devices  | Communicating devices  | Communicating devices | |
      | users    | All                    | Client+               | Client+ |

# Test general filter functionality for project level provider objects (project id hardcoded in glue file).
  @automation
  @loginWithAdmin
  Scenario Outline: Filter on project level
    Given there is a maxed out project
    When I am in project '<Provider>'
    And this filter '<filter>' is active
    When I change to filter '<this filter>'
    Then this filter '<new filter>' is active
    Examples:
      | Provider          | filter                 | this filter                | new filter |
      | measuring points  |                        | Active                   | Active    |
      | blasts            | All blasts             | Blasts with loaded data  | Blasts with loaded data    |
      | devices           | Communicating devices  | Communicating devices    |  |
      | views             | All reports            | Temporary                | Temporary    |
      | message_rules     | All message rules      | SMS                      | SMS    |
      | users             | All                    | Client+                  | Client+    |

# Test device specific filter functionality for account level provider objects.
  @automation
  @loginWithAdmin
  Scenario: Multiple filters in Company level Device
    When I set these filters
      | Monitoring On | Low battery | C20 |
    Then These filters are active
      | Communicating devices | Monitoring On | Low battery | C20 |
    And That the device filters are '4' can be counted using
      | Filter size | Filter listItem counter | Filter button counter|

  # SSD-2550
  @automation
  @loginWithAdmin
  Scenario: Empty account list due to search yields message
    When I do not get search hit in account 'projects' I get 'No project could be found'
    When I do not get search hit in account 'devices' I get 'No device could be found'
    When I do not get search hit in account 'users' I get 'No user could be found'

  # SSD-2550
  @automation
  @loginWithAdmin
  Scenario: Empty project list due to search yields message
    Given there is a maxed out project
    When I do not get search hit in project 'measure_points' I get 'No measuring point could be found'
    When I do not get search hit in project 'blasts' I get 'No blast could be found'
    When I do not get search hit in project 'devices' I get 'No device could be found'
    When I do not get search hit in project 'views' I get 'No data report could be found'
    When I do not get search hit in project 'message_rules' I get 'No message rule could be found'
    When I do not get search hit in project 'users' I get 'No user could be found'

#   SSD-2550
  @automation
  @loginWithAdmin
  Scenario: Empty project device list due to search yields message
    Given there is a maxed out project
    When I navigate to project measuring point 'create'
    And search for a device that do not exist
    Then I get menu message 'No device could be found'

    #   SSD-2550
  @automation
  @loginWithAdmin
  Scenario: Empty project list due to none created or added yields message - admin
    Given there is a project without blast standard
    Then I go to project 'measure_points' I get 'No measuring point created Create new'
    Then I go to project 'devices' I get 'Create a measuring point to add a device to the project Create measuring point'
    Then I go to project 'views' I get 'No data report created Create new'
    Then I go to project 'message_rules' I get 'No message rule created Create new'
    Then I go to project 'users' I get 'No user added Add user'
    Then I go to project 'blasts' I get 'No blast created Create new'

    #  SSD-2078
  @automation
  @loginWithAdmin
  Scenario: Validate select project checkbox 1
    Given there is a project
    And I am in account 'projects'
    When this filter 'Active only' is active
    And I navigate to project 'overview'
    Then project dropdown menu 'shall not' have Show active only checkbox

  #  SSD-2078
  @automation
  @loginWithAdmin
  Scenario: Validate select project checkbox 2
    Given there is a project
    And I am in account 'projects'
    When I change to filter 'All projects'
    And I navigate to project 'overview'
    Then project dropdown menu 'shall' have Show active only checkbox

  #    SSD-2987
  @automation
  @loginWithAdmin
  Scenario: Account Devices - Aside - Verify default table headers and selectable
    When I am in account 'devices'
    When aside is 'MEDIUM'
    Then these headers are default
      | Type | Serial number | Monitoring | Last read | Battery | Voltage | Status |
    And I click on 'Select columns' button
    And these headers are selectable
      | Description | Calibration date | Battery type | Scheduled hours | Firmware version | Time zone | Notes | Battery | Last read | Voltage  | GSM signal | Humidity | Temperature | Read only | Free memory | Status  |

    #    SSD-2987
  @automation
  @loginWithAdmin
  Scenario: Project Devices - Aside - Verify default table headers and selectable
    Given there is a project with two measuring points and two blasts
    And I am in project 'devices'
    When aside is 'MEDIUM'
    Then these headers are default
      | Type | Serial number | Monitoring | Last read | Battery | Voltage | Status |
    And I click on 'Select columns' button
    And these headers are selectable
      | Description | Calibration date | Battery type | Scheduled hours | Firmware version | Time zone | Notes | Battery | Last read | Voltage  | GSM signal | Humidity | Temperature | Read only | Free memory | Status  |

  # SSD-2578, SSD-3201, -4268
  @noStageEnv
  @automation
  @loginWithAdmin
  Scenario: Verify default headers for project measuring points
    Given there is a project with two measuring points and two blasts
    And I am in project 'measuring points'
    And aside is 'FULL'
    When I click on 'Select columns' button
    Then these 'columns' are 'default'
      | Description | Trigger level | Interval time | Record time | Active from | Active to | Price | Device | Serial number | Standard |
    And these 'groups' are 'selectable'
      | Information | Sensor | Logger | Blast settings |
    And these 'headers' are 'selectable'
      | Description | Trigger level | Interval time | Record time | Active from | Active to | dBCorr | Tare | Price | Device | Serial number | Standard | Calibration date | Logger type | Logger serial number | Logger battery | Battery | Last read | Device status |Guide value (V10) | Uncorrected velocity |  Alarm | Alert |

      #  SSD-2922, SSD-2912 (Proj ect bulk),
  @automation
  @loginWithAdmin
  Scenario: Account - Aside Header - List - Validate icons and default input value
    When I am in account 'overview'
    Then list header contains icons
      | REFRESH |
    When I am in account 'projects'
    Then list header contains icons
      | SEARCH | FILTER | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find your project'
      | ARROW_BACK | FILTER | MENU |
    When I am in account 'devices'
    Then list header contains icons
      | SEARCH | SORT | FILTER | WARNING | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find your device...'
      | ARROW_BACK | FILTER | MENU |
    When I am in account 'users'
    Then list header contains icons
      | SEARCH | FILTER | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find user'
      | ARROW_BACK | FILTER | MENU |

  #  SSD-2922, SSD-2992 (Mp bulk)
  @automation
  @loginWithAdmin
  Scenario: Project - Aside Header - List - Validate icons and default input value
    Given there is a maxed out project
    When I navigate to project 'overview'
    Then list header contains icons
      | REFRESH |
    When I navigate to project 'measuring points'
    Then list header contains icons
      | SEARCH | FILTER | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find measuring point'
      | ARROW_BACK | FILTER | MENU |
    When I navigate to project 'blasts'
    Then list header contains icons
      | SEARCH | FILTER | REFRESH | MENU |
    And blast list header also has Load latest data
    When I click Search icon header contains icon and default text 'Find blast'
      | ARROW_BACK | FILTER | MENU |
    When I navigate to project 'devices'
    Then list header contains icons
      | SEARCH | SORT | FILTER | WARNING | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find your device...'
      | ARROW_BACK | FILTER | MENU |
    When I navigate to project 'data reports'
    Then list header contains icons
      | SEARCH | FILTER | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find data report'
      | ARROW_BACK | FILTER | MENU |
    When I navigate to project 'message rules'
    Then list header contains icons
      | SEARCH | FILTER | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find message rule'
      | ARROW_BACK | FILTER | MENU |
    When I navigate to project 'users'
    Then list header contains icons
      | SEARCH | FILTER | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find user'
      | ARROW_BACK | FILTER | MENU |

    #  SSD-2922, SSD-2992 (Mp bulk)
  @automation
  @loginWithAdmin
  Scenario: Project - Aside Header - Table - Validate icons and default input value
    Given there is a maxed out project
    When I navigate to project 'measuring points'
    And aside is 'MEDIUM'
    Then list header contains icons
      | SEARCH | COLUMNS_SELECT | FILTER | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find measuring point'
      | ARROW_BACK | COLUMNS_SELECT | FILTER | MENU |
    When I navigate to project 'blasts'
    And aside is 'MEDIUM'
    Then list header contains icons
      | SEARCH | FILTER | REFRESH | MENU |
    And blast list header also has Load latest data
    When I click Search icon header contains icon and default text 'Find blast'
      | ARROW_BACK | FILTER | MENU |
    When I navigate to project 'devices'
    And aside is 'MEDIUM'
    Then list header contains icons
      | SEARCH | COLUMNS_SELECT | FILTER | WARNING | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find your device...'
      | ARROW_BACK | COLUMNS_SELECT | FILTER | MENU |
    When I navigate to project 'data reports'
    And aside is 'MEDIUM'
    Then list header contains icons
      | SEARCH | FILTER | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find data report'
      | ARROW_BACK | FILTER | MENU |
    When I navigate to project 'message rules'
    And aside is 'MEDIUM'
    Then list header contains icons
      | SEARCH | FILTER | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find message rule'
      | ARROW_BACK | FILTER | MENU |
    When I navigate to project 'users'
    And aside is 'MEDIUM'
    Then list header contains icons
      | SEARCH | FILTER | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find user'
      | ARROW_BACK | FILTER | MENU |

  #  SSD-2922, SSD-2912 (Project bulk),
  @automation
  @loginWithAdmin
  Scenario: Account - Aside Header - Table - Validate icons and default input value
    When I am in account 'projects'
    And aside is 'MEDIUM'
    Then list header contains icons
      | SEARCH | FILTER | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find your project'
      | ARROW_BACK | FILTER | MENU |
    When I am in account 'devices'
    And aside is 'MEDIUM'
    Then list header contains icons
      | SEARCH | COLUMNS_SELECT | FILTER | WARNING | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find your device...'
      | ARROW_BACK | COLUMNS_SELECT | FILTER | MENU |
    When I am in account 'users'
    And aside is 'MEDIUM'
    Then list header contains icons
      | SEARCH | FILTER | REFRESH | MENU |
    When I click Search icon header contains icon and default text 'Find user'
      | ARROW_BACK | FILTER | MENU |

  # SSD-2912 Bulk action, Project
  @NoStageEnv # Checkbox do not work as in Test or Prod
  @automation
  @loginWithAdmin
  Scenario: Bulk action - account devices - Validate list and table behaviour
    Given I am in account 'devices'
    And aside is 'COMPACT'
    When I check a 'device' checkbox icon
    Then aside header contains icons and default text 'Find your device...'
      | FILTER | MENU |
    And bulk action checkbox is 'mixed' and text is '1 selected' and icons are
      | PROJECT |
    When aside is 'MEDIUM'
    And I check a 'device' checkbox icon
    Then aside header contains icons and default text 'Find your device...'
      | FILTER | COLUMNS_SELECT | MENU |
    And bulk action checkbox is 'mixed' and text is '1 selected' and icons are
      | PROJECT |

  # SSD-2992 Bulk action, MP
  @NoStageEnv # Checkbox do not work as in Test or Prod
  @automation
  @loginWithAdmin
  Scenario: Bulk action - project mp - Validate list and table behaviour
    Given there is a project with several measuring points
    And I navigate to project 'measuring points'
    When I check a 'measuring point' checkbox icon
    Then aside header contains icons and default text 'Find measuring point'
      | FILTER | MENU |
    And bulk action checkbox is 'mixed' and text is '1 selected' and icons are
    | SCHEDULE | REPORTS |
    When aside is 'MEDIUM'
    And I check a 'measuring point' checkbox icon
    Then aside header contains icons and default text 'Find measuring point'
      | COLUMNS_SELECT | FILTER | MENU |
    And bulk action checkbox is 'mixed' and text is '1 selected' and icons are
      | SCHEDULE | REPORTS |

  # SSD-3016
  @automation
  @loginWithAdmin
  Scenario: Assert active filter do not block selected items for bulk action
    Given I am in account 'devices'
    When I select these for bulk action
      | C22 | C50 |
    When I set filter 'C50'
    And click on bulk action 'Create project' icon
    Then these devices will be included in Create measuring points list
      | C22 | C50 |

  # SSD-3016
  @automation
  @loginWithAdmin
  Scenario: Assert no search result do not block selected items for bulk action
    Given I am in account 'devices'
    When I select these for bulk action
      | C22 | C50 |
    And I input search 'phrase without match'
    And click on bulk action 'Create project' icon
    Then these devices will be included in Create measuring points list
      | C22 | C50 |

    #   SSD-3037, -3105
  @automation
  @loginWithAdmin
  Scenario: Aside - Monitoring devices - same sorting in list and table
    Given I am in account 'devices'
    When aside is 'COMPACT'
    Then sorting devices is 'ascending' on 'Last read'
    When aside is 'FULL'
    Then sorting devices is 'ascending' on 'Last read'

  # SSD-3138, SSD-2299
  @automation
  @loginWithAdmin
  Scenario: All communicating devices in aside has cogwheel
    Given I am in account 'devices'
    Then cogwheel is visible in COMPACT 'on hover'
    And cogwheel is visible in MEDIUM 'permanently'

  #  SSD-3266, SSD-3206, -3236
  @automation
  @loginWithAdmin
  Scenario: Aside and panels commit messages - C22 - uncommitted
    When I create uncommitted change for C22
    Then aside menu show 'uncommitted' change
    And 'C22' list item show 'uncommitted' change
    And settings panel show 'uncommitted' change
    And I clear 'C22' of any change

  #  SSD-3266, SSD-3206, -3236
  @automation
  @loginWithAdmin
  Scenario: Aside and panels commit messages - C22 - committed
    When I create committed change for C22
    Then aside menu show 'no' change
    And 'C22' list item show 'committed' change
    And details panel show 'committed' change
    And I clear 'C22' of any change

  #  SSD-3266, SSD-3206, -3236
  @automation
  @loginWithAdmin
  Scenario: Aside and panels commit messages - C22 - discarded
    When I discard change for 'C22'
    Then aside menu show 'no' change
    And details panel show 'no' change
    And 'C22' list item show 'no' change

  #  SSD-3266, SSD-3206, -3236
  @automation
  @loginWithAdmin
  Scenario: Aside and panels commit messages - S50 - uncommitted
    When I create uncommitted change for S50
    Then aside menu show 'uncommitted' change
    And 'D10' list item show 'uncommitted' change
    And details panel show 'uncommitted' change
    And I clear 'D10' of any change

  #  SSD-3266, SSD-3206, -3236
  @automation
  @loginWithAdmin
  Scenario: Aside and panels commit messages - S50 - committed
    When I create committed change for S50
    Then aside menu show 'no' change
    And 'D10' list item show 'committed' change
    And details panel show 'committed' change
    And I clear 'D10' of any change

  #  SSD-3266, SSD-3206, -3236
  @automation
  @loginWithAdmin
  Scenario: Aside and panels commit messages - S50 - discarded
    When I discard change for 'S50'
    Then aside menu show 'no' change
    And details panel show 'no' change
    And 'D10' list item show 'no' change

  @automation
  @loginWithAdmin
  Scenario: Check icon status from compact list account level
    Then projects has correct status
    And communicating devices in account has correct status
    And users in account has correct status

      # SSD-3468
  @automation
  @loginWithAdmin
  Scenario: Overview listitems has counter
    Given I am in account 'overview'
    Then aside 'Projects' has counter
    Then aside 'Devices' has counter
    Then aside 'Users' has counter

  @automation
  @loginWithAdmin
  Scenario Outline: Check aside contents from Project level - non blast
    Given there is a maxed out project
    Then I see all project measuring points in '<asideSize>' aside
    And I see all project monitoring devices in '<asideSize>' aside
    And I see all project users in '<asideSize>' aside
    And I see all project message rules in '<asideSize>' aside
    And I see all project data reports in '<asideSize>' aside
    Examples:
      | asideSize |
      | COMPACT |
      | FULL |

  @automation
  @loginWithAdmin
  Scenario Outline: Check list contents from Company level
    Then I see all account projects in '<asideSize>' aside
    And I see all account communicating devices in '<asideSize>' aside
    And I see all account users in '<asideSize>' aside
    Examples:
      | asideSize |
      | COMPACT |
      | FULL |