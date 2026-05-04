package com.example.playwright.steps.testdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDataMonSettingsC22 {

    public static String getStandard(String domain, String testCase) {
        char firstLetter = testCase.charAt(0);

        // Validate domain upfront
        if (!domain.equals("api") && !domain.equals("gui")) {
            throw new IllegalStateException("Unexpected domain: " + domain);
        }

        // Shared logic for standard text
        return switch (firstLetter) {
            case 'A' -> (domain.equals("gui")) ? "(1A) SS 4604866 Spräng 250mm/s 5-300Hz" : "1A";
            case 'B', 'C' -> (domain.equals("gui")) ? "(30A) BS 7385 250mm/s 1-300Hz" : "30A";
            case 'D' -> (domain.equals("gui")) ? "(70A) BS 6841 125m/s² (VDV)" : "70A";
            default -> throw new IllegalStateException("Unexpected testCase: " + testCase);
        };
    }

    /**
     * In GUI it's spelled 'Line 1', but when sending that value to api, it's replaced with 'line1'
     */
    public static String getFrequencyWeighting(String domain, String testCase) {
        char firstLetter = testCase.charAt(0);

        // Validate domain upfront
        if (!domain.equals("api") && !domain.equals("gui")) {
            throw new IllegalStateException("Unexpected domain: " + domain);
        }

        // Shared logic for frequency weighting
        return switch (firstLetter) {
            case 'A', 'D' -> null;
            case 'B' -> "OFF";
            case 'C' -> (domain.equals("gui")) ? "Line 1" : "line1";
            default -> throw new IllegalStateException("Unexpected testCase: " + testCase);
        };
    }

    private static String[] getChannelNames(String testCase) {
        char firstLetter = testCase.charAt(0);
        return switch (firstLetter) {
            case 'A' -> new String[] {"V", "L", "T"};
            case 'B' -> new String[] {"V", "L", "T", "R"};
            case 'C' -> new String[] {"rV", "rL", "rT"};
            case 'D' -> new String[] {"VDV-V accu", "VDV-L accu", "VDV-T accu"};
            default -> throw new IllegalStateException("Unexpected testCase: " + testCase);
        };
    }

    private static String[] getChannelStates(String testCase) {
        char testSuite = testCase.charAt(0);
        int testNumber = Integer.parseInt(testCase.substring(1));

        switch (testSuite) {
            case 'A', 'C', 'D' -> {
                return switch (testNumber) {
                    case 1, 2  -> new String[] {"ON", "ON", "ON"};
                    case 3, 4, 7   -> new String[] {"ON", "OFF", "OFF"};
                    case 5, 6, 9, 11  -> new String[] {"OFF", "OFF", "OFF"};
                    default -> throw new IllegalStateException("Unexpected testNumber: " + testNumber);
                };
            }
            case 'B' -> {
                return switch (testNumber) {
                    case 1, 2  -> new String[] {"ON", "ON", "ON", "ON"};
                    case 3, 4, 7, 8   -> new String[] {"ON", "OFF", "OFF", "OFF"};
                    case 5, 6, 9, 10, 11  -> new String[] {"OFF", "OFF", "OFF", "OFF"};
                    default -> throw new IllegalStateException("Unexpected testNumber: " + testNumber);
                };
            }
            default -> throw new IllegalStateException("Unexpected testSuite: " + testSuite);
        }
    }

    /**
     * To prepare C22 before test, we cannot send null-values, because then C22 will keep old values. And we can never know which those values were.
     * @param testCase
     * @return
     */
    private static String[] getChannelValues(String testCase) {
        char testSuite = testCase.charAt(0);
        int testNumber = Integer.parseInt(testCase.substring(1));

        switch (testSuite) {
            case 'A', 'C' -> {
                return switch (testNumber) {
                    case 1, 3, 5  -> new String[] {"11.0", "11.0", "11.0"};
                    case 2, 4, 6   -> new String[] {"11.0", "22.0", "33.0"};
                    case 7  -> new String[] {"11.0", "11.0", null};
                    case 9  -> new String[] {"11.0", "22.0", null};
                    case 11  -> new String[] {null, null, null};
                    default -> throw new IllegalStateException("Unexpected testNumber: " + testNumber);
                };
            }
            case 'D' -> {
                return switch (testNumber) {
                    case 1, 3, 5  -> new String[] {"6.0", "6.0", "6.0"};
                    case 2, 4, 6   -> new String[] {"6.0", "7.0", "8.0"};
                    case 7  -> new String[] {"6.0", "6.0", null};
                    case 9  -> new String[] {"6.0", "7.0", null};
                    case 11  -> new String[] {null, null, null};
                    default -> throw new IllegalStateException("Unexpected testNumber: " + testNumber);
                };
            }
            case 'B' -> {
                return switch (testNumber) {
                    case 1, 3, 5  -> new String[] {"11.0", "11.0", "11.0", "11.0"};
                    case 2, 4, 6   -> new String[] {"11.0", "22.0", "33.0", "44.0"};
                    case 7  -> new String[] {"11.0", "11.0", "11.0", null};
                    case 8 -> new String[] {"11.0", "11.0", null, null};
                    case 9  -> new String[] {"11.0", "22.0", "33.0", null};
                    case 10  -> new String[] {"11.0", "22.0", null, null};
                    case 11  -> new String[] {null, null, null};
                    default -> throw new IllegalStateException("Unexpected testNumber: " + testNumber);
                };
            }
            default -> throw new IllegalStateException("Unexpected testSuite: " + testSuite);
        }
    }

    /**
     * NB. The channels/triggers returned in this list are to mimic how GUI presents the channels.
     * E.g., a channel with a trigger_state:OFF do not present a value to the user.
     * But if the user set trigger_state:ON, then a value will be shown.
     * Unless no new standard or freqWeighting has been selected. Then 'not set' will be shown.
     * @return a list with one map per channel, and where each map has name, state and value.
     */
    public static List<Map<String, String>> getTestTriggers(String testcase) {
        List<Map<String, String>> standardMap = new ArrayList<>();

        String[] triggerNames = getChannelNames(testcase);
        String[] triggerValues = getChannelValues(testcase);
        String[] triggerStates = getChannelStates(testcase);

        if (!(triggerNames.length == triggerValues.length  && triggerValues.length == triggerStates.length)) {
            throw new IllegalStateException("Trigger name/value/states are not of equal length.");
        }

        for (int i = 0; i < triggerNames.length; i++) {
            Map<String, String> triggerMap = new HashMap<>();
            triggerMap.put("name", triggerNames[i]);
            triggerMap.put("value", triggerValues[i]);
            triggerMap.put("state", triggerStates[i]);
            standardMap.add(triggerMap);
        }

        return standardMap;
    }
}
