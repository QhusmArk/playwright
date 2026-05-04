# language: en

Feature: Data report details

  @automation
  @setUpSeleniumWithAdmin
  Scenario: Open transient from Measuring point view
    Given there is a project with an interval report
    When 'measuring report' view is opened
    And I click on the uppermost transient
    Then Transient view has Time Domain Analysis

  # SSD-3029
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Open report and verify measuring points and transients
    Given there is a guide value project
    When 'intervals chart' view is opened
    Then each measuring point has a graph panel in intervals chart
    When 'intervals table' view is opened
    Then each measuring point has a column in intervals table
    When 'transients' view is opened
    Then each measuring point has a column in transients table
    When 'measuring report' view is opened
    Then there is a table with all transients in the measuring report
    And 'blasts' view is opened
    Then there is a table with all blasts in the blast report
    And 'blast journal' view is opened
    Then each measuring point has a row in blast journal table

  #  SSD-2234
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Interval report shows standards with letter appendix (1A or 1B)
    Given there is a project with mp that have measured with standard with letter appendix
    When 'intervals chart' view is opened
    Then the interval graph header contains '1A' or '1B'

  # SSD-2851
  @automation
  @setUpSeleniumWithAdmin
  Scenario: No resultant in TDA if unit has RMS
    Given there is a project with an all data report for std 59
    When 'transients' view is opened
    And I select the first transient
    Then the header has not 'Resultant'

  @automation
  @setUpSeleniumWithAdmin
  Scenario: Frequency domain analysis - default operator
    Given there is a project with a 'C22' and transient report
    When 'transients' view is opened
    And I select the first transient
    Then frequency domain analysis show 'PPV-ZX' as operator
    And when I disable MP graph settings Show Transient ppvzx
    And 'transients' view is opened
    And I update the transient report
    And I select the first transient
    Then frequency domain analysis show 'FFT' as operator

  # SSD-3029
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Validate transient table view parts
    Given there is a project with a 'V12' and transient report
    When 'transients' view is opened
    Then table meta data 'Sensor serial no.' has 'sensorType #sensorSerial'

    #  SSD-2331
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Creator - first and last sample
    Given there is a project with a 'C22' and transient report
    And 'transients' view is opened
    And I select the first transient
    When this operator is selected, first and last sample is present
      | FFT | Third octave |
    When this operator is selected, first and last sample is not present
      | PPV-ZX | SRS | Octaves |

  #  SSD-2331
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Legacy - first and last sample
    Given there is a project with a 'V12' and transient report
    And 'transients' view is opened
    And I select the first transient
    When this operator is selected, first and last sample is present
      | FFT |
    When this operator is selected, first and last sample is not present
      | PPV-ZX | SRS | Third octave | Octaves |

  #  See comment in https://sigicom.atlassian.net/browse/SSD-2477
  @noProdEnv
  @automation
  @setUpSeleniumWithAdmin
  Scenario: Open one transient of each sensor type
    Given a transient project
    Then it is possible to open a 'C22' transient
    And it is possible to open a 'C50' transient
    And it is possible to open a 'S50' transient
    And it is possible to open a 'V10' transient
    And it is possible to open a 'V12' transient
    And it is possible to open a 'A10' transient
#    And it is possible to open a 'C10' transient   // disabled until COMPACT are mapped correctly (see C12 #79049, Test_env)
#    And it is possible to open a 'VS12' transient  // disabled until https://sigicom.atlassian.net/browse/SSD-3982 is DONE