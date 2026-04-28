package com.example.helpers;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Randomizer {

    /**
     * Orders the test cases to maximize the number of consecutive matches where
     * the "ExpRes" of one test case equals the "PreReq" of the next test case.
     *
     * @param testCases A list of test cases represented as maps with "PreReq" and "ExpRes" keys.
     * @return A reordered list of test cases.
     */
    public static List<Map<String, String>> orderTestCases(List<Map<String, String>> testCases) {
        if (testCases == null || testCases.isEmpty()) {
            throw new IllegalArgumentException("The list of test cases must not be null or empty.");
        }

        // Result list to hold the ordered test cases
        List<Map<String, String>> orderedTestCases = new ArrayList<>();

        // Start with the first test case (arbitrary choice)
        Map<String, String> currentTestCase = testCases.remove(0);
        orderedTestCases.add(currentTestCase);

        while (!testCases.isEmpty()) {
            boolean matchFound = false;

            // Search for the next test case where "PreReq" matches the current "ExpRes"
            for (int i = 0; i < testCases.size(); i++) {
                Map<String, String> nextTestCase = testCases.get(i);
                if (currentTestCase.get("ExpRes").equals(nextTestCase.get("PreReq"))) {
                    // Match found, add it to the ordered list
                    orderedTestCases.add(nextTestCase);
                    currentTestCase = nextTestCase; // Update current test case
                    testCases.remove(i); // Remove from remaining test cases
                    matchFound = true;
                    break;
                }
            }

            // If no match was found, add an arbitrary remaining test case to continue
            if (!matchFound) {
                currentTestCase = testCases.remove(0);
                orderedTestCases.add(currentTestCase);
            }
        }

        return orderedTestCases;
    }

    /**
     * Generates a list of all combinations of test cases (excluding identical pairs).
     *
     * @param testCases A list of test cases.
     * @return A list of maps where each map represents a combination of test cases.
     */
    public static List<Map<String, String>> generateAllCombinationsExcludingIdentical(List<String> testCases) {
        List<Map<String, String>> combinations = new ArrayList<>();

        if (testCases == null || testCases.isEmpty()) {
            throw new IllegalArgumentException("The list must not be null or empty.");
        }

        for (int i = 0; i < testCases.size(); i++) {
            for (int j = 0; j < testCases.size(); j++) {
                if (!testCases.get(i).equals(testCases.get(j))) {
                    // Create a map for each valid combination
                    Map<String, String> combination = new HashMap<>();
                    combination.put("PreReq", testCases.get(i));
                    combination.put("ExpRes", testCases.get(j));
                    combinations.add(combination);
                }
            }
        }

        return combinations;
    }



    /**
     * Splits the input string using a semicolon as a delimiter and returns the part based on the index.
     *
     * @param stringToSplit The input string to be split.
     * @param partIndex The index of the part to return (1-based).
     * @return The requested part of the string, or an error message if the index is invalid.
     */
    public static String splitCsvString(String stringToSplit, int partIndex, String delimiter) {
        if (stringToSplit == null || stringToSplit.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty.");
        }

        // Split the string by the semicolon delimiter
        String[] parts = stringToSplit.split(delimiter);

        // Check if the partIndex is valid
        if (partIndex < 1 || partIndex > parts.length) {
            throw new IllegalArgumentException("Invalid index. Must be between 1 and " + parts.length);
        }

        // Use a switch statement to return the corresponding part
        return switch (partIndex) {
            case 1 -> parts[0].trim();
            case 2 -> parts[1].trim();
            case 3 -> parts[2].trim();
            case 4 -> parts[3].trim();
            case 5 -> parts[4].trim();
            case 6 -> parts[5].trim();
            case 7 -> parts[6].trim();
            default ->
                // This shouldn't happen due to the range check above
                    throw new IllegalArgumentException("Unexpected error: Invalid index.");
        };
    }

    /**
     * @param part  1 or 2
     * @param text  The text with 1 + delimiter + 2, e.g., "Skanska, Client"
     */
    public static String splitString(int part, String text, String delimiter) {
        if (text.contains(delimiter)) {
            return switch (part) {
                case 1 -> text.substring(0, text.indexOf(delimiter));
                case 2 -> text.substring(text.indexOf(delimiter) + delimiter.length());
                default -> throw new IllegalArgumentException("Unexpected part: " + part);
            };
        } else {
            throw new IllegalStateException("Unexpected delimiter to this text: " + delimiter);
        }
    }

    /**
     * Helper method that splits a String based on delimiter
     */
    public static List<String> parse(String s) {
        return Arrays.stream(s.split(";"))
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .collect(Collectors.toList());
    }

    public static int randomInt(int lowRange, int highRange, int notThisNumber) {
        if (lowRange >= highRange) {
            throw new IllegalArgumentException("lowRange must be less than highRange");
        }
        if (lowRange == notThisNumber && highRange == notThisNumber) {
            throw new IllegalArgumentException("notThisNumber is the only number in the range");
        }

        Random random = new Random();
        int randomNumber;
        do {
            randomNumber = random.nextInt(highRange - lowRange + 1) + lowRange;
        } while (randomNumber == notThisNumber);

        return randomNumber;
    }

    public static String randomString(final int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString().toLowerCase();
    }

    public static int randomInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static Double randomDouble(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();

        return  r.nextDouble((max - min) + 1) + min;
    }

    public static boolean randomBoolean() {
        Random rd = new Random();
        return rd.nextBoolean();
    }

    /**
     * @param minimumValue Lowest acceptable number
     * @param maximumValue Highest acceptable number
     * @return A list of ints in range minimumValue <= maximumValue.
     */
    public static List<Integer> getRandomAndUniqueIntegers(int minimumValue, int maximumValue) {
        int numberOfCheckBoxesToUse = Randomizer.randomInt(minimumValue, maximumValue);

        // Create an int array with the checkboxes to be clicked.
        // The array must have unique values, therefore a Set is used first.
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < numberOfCheckBoxesToUse; i++) {
            set.add(Randomizer.randomInt(0, maximumValue));
        }
        return new ArrayList<>(set);
    }

    /**
     * @return a int from 0 to (asideSize - 1) and not same index as the element where currentStandard is.
     */
    public static int getRandomIndexExcludingCurrent(List<String> list, String currentStandard) {
        Random random = new Random();
        int currentIndex = list.indexOf(currentStandard);
        int randomIndex;
        do {
            randomIndex = random.nextInt(list.size());
        } while (randomIndex == currentIndex);

        return randomIndex;
    }

    /**
     * Returns a random string from a list.
     */
    public static String getRandomStringFromList(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            throw new IllegalArgumentException("List must not be null or empty");
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(strings.size());
        return strings.get(randomIndex);
    }

    /**
     * @return a list of all index position but the one where currentStandard is located.
     */
    public static List<Integer> getPositionsExcludingCurrent(List<String> list, String currentStandard) {
        List<Integer> positions = new ArrayList<>();
        int currentIndex = list.indexOf(currentStandard);

        for (int i = 0; i < list.size(); i++) {
            if (i != currentIndex) {
                positions.add(i);
            }
        }

        return positions;
    }


}
