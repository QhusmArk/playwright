package com.example.helpers;

import com.example.playwright.helpers.PlaywrightActions;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssertionHelpers {

    /**
     * @return true if all strings in the list can be parsed to Integer.
     */
    public static boolean onlyIntegersInList(List<String> stringList) {
        for (String value : stringList) {
            // Check if the input matches the integer regex pattern
            if (!value.matches("^-?[0-9]+$")) {
                return false;
            }
            try {
                // Try to parse the string to an integer
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // The string is too large to fit in an int
                return false;
            }
        }
        return true; // Return true only if all elements are valid integers
    }

    /**
     *  Checks for duplicates by creating a Set (which do not contain duplicates, and then compares size with original list.
     * @return true if there is no duplicates
     */
    public static boolean noDuplicatesInList(List<String> stringList) {
        // Use list to make a set
        Set<String> set = new HashSet<>(stringList);
        return stringList.size() == set.size();
    }

    public static boolean hasSingleInstance(String expectedKey, List<String> actualKeys) {
        int count = 0;

        for (String key : actualKeys) {
            if (key.equals(expectedKey)) {
                count++;
            }

            // If more than one instance is found, return false immediately
            if (count > 1) {
                return false;
            }
        }

        // Return true only if exactly one instance is found
        return (count == 1);
    }

    public static boolean areTrimmedAndSortedListsIdentical(List<String> expected, List<String> actual) {
        List<String> trimmedExpected = expected.stream()
                .map(String::trim)
                .toList();
        List<String> trimmedActual = actual.stream()
                .map(String::trim)
                .toList();

        // Create a modifiable copy in case a list is unmodifiable
        List<String> list1 = new ArrayList<>(trimmedExpected);
        List<String> list2 = new ArrayList<>(trimmedActual);

        System.out.println("list1: " + list1.size() + ", list2: " + list2.size());
        // Check if both lists have the same size
        if (list1.size() != list2.size()) {
            return false;
        }

        list1.sort(Comparator.nullsLast(Comparator.naturalOrder()));
        list2.sort(Comparator.nullsLast(Comparator.naturalOrder()));

        // Compare the sorted lists
        if (!list1.equals(list2)) {
            System.out.println("Lists are not identical: ");
            System.out.println("List1: ");
            list1.forEach(System.out::println);
            System.out.println("List2: ");
            list2.forEach(System.out::println);

            return false;
        }
        return true;
    }

    public static boolean secondCheckOfIdenticalList(List<String> expected, List<String> actual) {
        boolean listsAreEqual = true;
        List<String> trimmedExpected = expected.stream()
                .map(String::trim)
                .toList();
        List<String> trimmedActual = actual.stream()
                .map(String::trim)
                .toList();

        List<String> duplicatesInExpected = findDuplicates(expected);

        if (!duplicatesInExpected.isEmpty()) {
            System.out.println("Duplicates in duplicatesInExpected");
            duplicatesInExpected.forEach(System.out::println);
            System.out.println();
        }

        List<String> duplicatesInActual = findDuplicates(actual);
        if (!duplicatesInActual.isEmpty()) {
            System.out.println("Duplicates in duplicatesInActual");
            duplicatesInActual.forEach(System.out::println);
            System.out.println();
        }

        if (!duplicatesInExpected.isEmpty() || !duplicatesInActual.isEmpty()) {
            System.out.println("Duplicates found.\n");
            listsAreEqual = false;
        }

        // Create a modifiable copy in case a list is unmodifiable
        List<String> list1 = new ArrayList<>(trimmedExpected);
        List<String> list2 = new ArrayList<>(trimmedActual);
        System.out.println("list1: " + list1.size() + ", list2: " + list2.size());
        // Check if both lists have the same size
        if (list1.size() != list2.size()) {
            listsAreEqual = false;
        } else {
            // Check expected list
            for (int e = 0; e <= list1.size(); e++) {
                String expectedProjectName = list1.get(e);

                boolean foundMatch = false;

                for (int a = 0; a <= list2.size(); a++) {
                    String actualProjectName = list2.get(a);
                    if (expectedProjectName.equals(actualProjectName)) {
                        foundMatch = true;
                    }
                }

                if (!foundMatch) {
                    System.out.println("No match found for expectedProjectName:" + expectedProjectName);
                    listsAreEqual = false;
                }
            }

            // Check actual list
            for (int a = 0; a <= list2.size(); a++) {
                String actualProjectName = list2.get(a);

                boolean foundMatch = false;

                for (int e = 0; e <= list1.size(); e++) {
                    String expectedProjectName = list1.get(e);
                    if (actualProjectName.equals(expectedProjectName)) {
                        foundMatch = true;
                    }
                }

                if (!foundMatch) {
                    System.out.println("No match found for actualProjectName:" + actualProjectName);
                    listsAreEqual = false;
                }
            }

        }
        return listsAreEqual;
    }


    public static List<String> findDuplicates(List<String> strings) {
        // Create a HashMap to store the frequency of each string
        Map<String, Integer> frequencyMap = new HashMap<>();

        // Traverse the list and count the frequency of each string
        for (String str : strings) {
            frequencyMap.put(str, frequencyMap.getOrDefault(str, 0) + 1);
        }

        // Create a list to store the duplicate strings
        List<String> duplicates = new ArrayList<>();

        // Identify strings that have a frequency greater than 1
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > 1) {
                duplicates.add(entry.getKey());
            }
        }

        return duplicates;
    }

    public static void urlContains(String expectedUrl, String currentUrl) {
        PlaywrightActions.sleep(1);
        System.out.println("currentUrl: " + currentUrl);
        assertTrue(currentUrl.contains(expectedUrl), "currentUrl '"+currentUrl+"' did not contain '" +expectedUrl+"'");
    }
}
