# language: en

Feature: Devices

  @automation
  @setUpSeleniumWithAdmin
  Scenario: Show all devices used in the project
    Given there is a project with two measuring points and two blasts
    When I navigate to project 'devices'
    Then Left menu contains 'all' devices

  @automation
  @setUpSeleniumWithAdmin
  Scenario: This type has this details
    When I navigate to the 'C22' details
    Then details for 'C22' shall show icons
      | MONITORING_ON | COMMUNICATION | BATTERY | GSM | PROJECT | CLIPBOARD | HUMIDITY | TEMPERATURE | MEMORY | STAR |
    When I navigate to the 'D10' details
    Then details for 'D10' shall show icons
      | MONITORING_ON | COMMUNICATION | BATTERY | GSM | PROJECT | CLIPBOARD | TEMPERATURE | MEMORY | STAR |

  @automation
  @setUpSeleniumWithAdmin
  Scenario: Search for a device by it's serial
    Given there is a project with two measuring points and two blasts
    And I navigate to project 'devices'
    When I search for a Device
    Then Left menu contains 'searched' devices

  @automation
  @setUpSeleniumWithAdmin
  Scenario: Check time value on account devices
    Given I am in account 'devices'
    Then the list only contains communicating devices with one of the following texts
      | second | minute | hour | day | month | year |

  # SSD-1470
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Validate that legacy sensor standard is showing
    When I navigate to 'S50' mon_settings
    Then the standard is visible

  # SSD-2220
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Validate chip on Device map properties dropdown
    Given I am in account 'devices'
    Then the map properties dropdown have a chip

  #  SSD-2175
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Device description should be searchable
    When I am in account 'devices'
    And I make a search with 'QAs device'
    Then the list should show device with that description

  #  SSD-2175
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Device notes should be searchable
    When I am in account 'devices'
    And I make a search with '@Jenkins'
    Then the list should show device with that note

  #  SSD-2467
  @manually
  @setUpSeleniumWithAdmin
  Scenario: POINT devices exist in project
    Given there is a project with POINT connected to a mp
    When I navigate to project 'devices'
    Then I see the POINT in the list

  #  SSD-2154
  @noStageEnv
  @automation
  @setUpSeleniumWithAdmin
  Scenario: C50 recording time can be set to 1-20 seconds
    Given I am in account 'C50' monitoring settings
    Then I can save record time in range 1-20 seconds

  # SSD-2756
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Device connection history - Non cable logger - Can navigate from device details
    Given I navigate to the 'C22' details
    When I click on 'Connection history'
    Then I am redirected to '/status/projects'

  # SSD-2756
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Device connection history - Cable logger - Cannot navigate from device details
    Given I navigate to the 'D10' details
    And tooltip say 'Device connection history can\'t be displayed for this logger type'
    When I click on 'Connection history'
    Then I remain at '/details'

  # SSD-1969
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Validate trigger state and value when change of std
    Given I am in account 'C22' monitoring settings
    When I change standard for 'C22'
    Then all triggers are Not set:ON

  # SSD-1969
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Validate trigger state and value when change of std and back to original
    Given I am in account 'C22' monitoring settings
    When I change standard for 'C22' and then back to original standard
    Then all triggers are Not set:ON

  #  SSD-1820 , -2618
  @noStageEnv
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Validate all creator loggers has fw_version in table
    When I am in account 'devices'
    And aside is 'MEDIUM'
    When I select columns
      | Firmware version | Last read |
    When I set these filters
      | C20 | C22 | C50 | POINT |
    Then each device has value in column 'Firmware version'

  # SSD-2756, SSD-2970, SSD-3007, SSD-3110,
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Device connection history - Show project name
    When I navigate to the 'D10' details
    Then there is a project name in 'D10' connection history
    When I navigate to the 'C22' details
    Then there is a project name in 'C22' connection history
    When I navigate to the 'POINT' details
    Then there is a project name in 'POINT' connection history

  # SSD-3111
  @noStageEnv
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Device connection history - Connected monitoring devices
    When I navigate to the 'D10' details
    Then this sensor is listed as a connected sensor to 'D10'
      |S50|V10|V12|
    When I navigate to the 'POINT' details
    And this sensor is listed as a connected sensor to 'POINT'
      |VS12|

  # SSD-3011
  @automation
  @setUpSeleniumWithAdmin
  Scenario Outline: Filter which devices to show in aside
    Given I am in account 'devices'
    And aside is '<AsideSize>'
    Then this filter make aside only contain devices with this banner
      | Low battery:Battery | Low GSM signal:GSM signal | Communication:Communication |
    Examples:
      | AsideSize |
      | COMPACT |
      | FULL |

  # Test that monitors known devices to see if Description is changed outside QA control
  @noStageEnv
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Connected devices to QA_computer
    When I navigate to device details, description is '🚫 SW_QAs device'
      | C22 | POINT | D10 | C50 | C12_LOGGER | C20 |

# SSD-3039
#  Loggers without proper data. Either no 'Last comm' in compact view, or no data in .../details
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Filter on device type
    When Filter and validate on one of the following devices
      | A10 | C10_SENSOR | C12_SENSOR | C20 | C22 | C50 | D10 | MASTER | MINI | POINT | S10 | S50 | S51 | V10 | V11 | V12 | V12R | VS10 | VS12 | X20A | X20BP | X20CO | X20DM2 | X20H | X20H2S | X20NH3 | X20NO | X20NO2 |X20O2 | X20R | X20SR | X20WMT | X20WXT |

  # SSD-1969
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Validate input of trigger values is implemented on all triggers
    Given I am in account 'C22' monitoring settings
    When I change standard for 'C22'
    And I set a value to 'C22' top trigger
    Then all triggers should have same value

  # SSD-3133
  @automation
  @setUpSeleniumWithAdmin
  Scenario Outline: Bulk action - account devices - Validate devices is included
    Given I am in account 'devices'
    And aside is '<AsideSize>'
    When I select these for bulk action
      | C22 | C50 |
    And click on bulk action 'Create project' icon
    Then these devices will be included in Create measuring points list
      | C22 | C50 |
    Examples:
      | AsideSize |
      | COMPACT |
      | FULL |

  # SSD-3143
  @automation
  @setUpSeleniumWithAdmin
  Scenario Outline: Clicking on device listItem should redirect to details
    Given I am in account 'devices'
    When I click on '<device>', I am redirected to 'details'
      | COMPACT | FULL |
    Examples:
      | device |
      | C22 |
      | V10 |

    # SSD-2897, -2103
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Firmware version for legacy loggers
    Given I am in account 'devices'
    And aside is 'MEDIUM'
    Then active legacy loggers has firmware version

  # SSD-1631
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Calibration date for legacy sensors
    Given I am in account 'devices'
    And aside is 'MEDIUM'
    Then active legacy sensors has calibration date

      # SSD-2756, -3168
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Device connection history - Verify measuring points, columns and sorting
    Given there is a project with an outdated mp
    And I am in account 'C22' Connection History
    Then the 'connected history' header are
      | Project name | Measuring point name | Connected device date |
    And list contains old or current connected devices

  #  SSD-3288
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Several standards show non valid channels
    Given I navigate to 'C22' mon_settings
    Then only changeable channels are displayed

  #   SSD-3401
  @automation
  @setUpSeleniumWithAdmin
  Scenario Outline: C50 - Remote override
    Given I navigate to 'C50' remote override
    Then I can save '<Command>'
    Examples:
      | Command |
      | Remote Reboot |
      | Remote Firmware Upgrade |
      | Remote Shut Down |
      | Remote Update GPS Position |

  #   SSD-3401
  @automation
  @setUpSeleniumWithAdmin
  Scenario Outline: C22 - Remote override
    Given I navigate to 'C22' remote override
    Then I can save '<Command>'
    Examples:
      | Command |
      | Remote Reboot |
      | Remote Firmware Upgrade (Scheduled) |
      | Remote Firmware Upgrade (at Monitoring Off) |
      | Remote Firmware Upgrade (Immediate) |
      | Remote Firmware Upgrade (Cancel) |
      | Remote Shut Down |
      | Remote Update GPS Position |

#    Will fail for D10 fw 1.6
  #   SSD-3401
  @automation
  @setUpSeleniumWithAdmin
  Scenario Outline: D10 - Remote override
    Given I navigate to 'D10' remote override
    Then I can save '<Command>'
    Examples:
      | Command |
      | Server messages on |
      | Server messages off |
      | Force reboot |
      | S50 clear agenda |
      | S50 Connect 2x/hour |
      | S50 Connect 4x/hour |
      | Firmware upgrade |

#  Connected to jenkins job http://10.33.12.192:8080/view/cronJobs/job/POST_new_calibration_date_to_S50/
#  SSD-3307
  @automation
  @noStageEnv
  Scenario: Validate calibration date service
    Then the calibration date is yesterdays date

#    SSD-3424
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Rolling Window for C50
    Given I navigate to 'C50' mon_settings
    Then selected interval time regulates which rolling_window that can be selected

  # SSD-3039, -3007
  @automation
  @setUpSeleniumWithAdmin
  Scenario: All devices has details
    Given I am in account 'devices'
    Then all devices in the list have details

  # B26-75
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Verify POINT Connected sensor header
    When I navigate to the 'POINT' details
    Then connected sensor header is 'Connected monitoring devices'

  @automation
  @setUpSeleniumWithAdmin
  Scenario Outline: Presets and standards
    When I navigate to '<type>' mon_settings
    Then selectable '<content>' for '<type>' are
    Examples:
    |type|content|
    |C50|31-132dB LAeq, LAFmax;26-123dB LAeq, LAFmax;31-132dB LAeq, LASmax;26-123dB LAeq, LASmax;36-132dB LCeq, LCFmax;27-123dB LCeq, LCFmax;36-132dB LCeq, LCSmax;27-123dB LCeq, LCSmax;39-135dB LCeq, LCPeak;34-126dB LCeq, LCPeak|
    |C22|(1A) SS 4604866 Spräng 250mm/s 5-300Hz;(1B) SS 4604866 Spräng 25mm/s 5-300Hz;(3) SS 25211 Schakt 25mm/s 5-150Hz;(5) SS 25211 Schakt 25mm/s 2-150Hz;(7) SS 4604861 Komfort 20mm/s 1-80Hz;(8) SS 4604861 Komfort 700mm/s² 1-80Hz;(9) DIN 4150-2 KB 20mm/s 1-80Hz;(15) ÖNORM S9012 700mm/s² 1-80Hz;(16A) Acceleration 125m/s² 5-300Hz;(16B) Acceleration 12.5m/s² 5-300Hz;(17) ISO 10816-2 200mm/s 5-500Hz;(18A) DIN4150-3 Anlage 250mm/s 1-315Hz;(18B) DIN4150-3 Anlage 25mm/s 1-315Hz;(20A) NS 8141:2001 Byggverk 250mm/s 5-300Hz;(20B) NS 8141:2001 Byggverk 25mm/s 5-300Hz;(22) NS 8176 Komfort 20mm/s 1-80Hz;(23A) NS 8141:2022 Byggverk 250mm/s 2-400Hz;(23B) NS 8141:2022 Byggverk 25mm/s 2-400Hz;(25A) NS 8141:2013 Byggverk 250mm/s 3-400Hz;(25B) NS 8141:2013 Byggverk 25mm/s 3-400Hz;(27) ISO 2631-2 20mm/s 1-80Hz;(28A) SN 640312a 250mm/s 5-150Hz;(28B) SN 640312a 25mm/s 5-150Hz;(30A) BS 7385 250mm/s 1-300Hz;(30B) BS 7385 25mm/s 1-300Hz;(33) ANSI S2.71 0.8 in/s 1-80Hz;(35) AS 2187.2-2006 250mm/s 2-250Hz;(38A) ÖNORM S9020 250mm/s 1-315Hz;(38B) ÖNORM S9020 25mm/s 1-315Hz;(40) Arrêté du 1994 250mm/s 1-150Hz;(41) ICPE-Circ86 25mm/s 1-150Hz;(42A) IN 1226 250mm/s 1-150Hz;(42B) IN 1226 25mm/s 1-150Hz;(44) OfM 9/1997 50-117dB 1-80Hz;(45) Turkey Mining and Quarry 250mm/s 2-250Hz;(46A) SBR-A:2010 250mm/s 1-100Hz;(46B) SBR-A:2010 25mm/s 1-100Hz;(47) SBR-B 20mm/s 1-80Hz;(48) Toronto bylaw 514 250mm/s 2-250Hz;(49) Toronto bylaw 514 250mm/s 1-100Hz;(51A) ISEE Seismograph 10 in/s 2-250Hz;(51B) ISEE Seismograph 1 in/s 2-250Hz;(53A) Geophone 250mm/s 5-500Hz;(53B) Geophone 25mm/s 5-500Hz;(55A) ISEE/USBM 250mm/s 2-250Hz;(55B) ISEE/USBM 25mm/s 2-250Hz;(57A) DIN4150-3 Anlage 10 in/s 1-315Hz;(57B) DIN4150-3 Anlage 1 in/s 1-315Hz;(58A) PN-B-02170 250mm/s 1-100Hz;(58B) PN-B-02170 25mm/s 1-100Hz;(59) FTA VdB 50-118 dB 1-80Hz;(60A) NCh 3577 250mm/s 1-315Hz;(60B) NCh 3577 25mm/s 1-315Hz;(70A) BS 6841 125m/s² (VDV);(70B) BS 6841 12.5m/s² (VDV);(71A) BS 7385&6841 250mm/s 1-300Hz;(71B) BS 7385&6841 25mm/s 1-300Hz;(72) Metro Vancouver 250mm/s 3-100Hz;(73) NP 2074:2015 250mm/s 2-80Hz;(74A) SBR-A:2017 struc. C1 250mm/s 1-100Hz;(74B) SBR-A:2017 struc. C2 25mm/s 1-100Hz;(75A) SBR-A:2017 adpt. C1 250mm/s 1-100Hz;(75B) SBR-A:2017 adpt. C2 25mm/s 1-100Hz|

  @automation
  @setUpSeleniumWithAdmin
  Scenario: Validate C50-gui-behaviour
    Then no agenda then no default timeslot
    And api preset matches mon.settings preset