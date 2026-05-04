package com.example.helpers;

import com.example.api.models.device.Device;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class DeviceAssesser {


    public static List<Device> filterOnApiCommunicatingDevices(List<Device> expectedDevices) {
        // Get only Communicating devices
        Predicate<Device> compact = device -> device.getType().equals("COMPACT");
        Predicate<Device> c2x = device -> device.getType().equals("C20") || device.getType().equals("C22");
        Predicate<Device> c5x = device -> device.getType().equals("C50");
        Predicate<Device> point = device -> device.getType().equals("POINT");
        Predicate<Device> im = device -> device.getType().equals("IM");
        Predicate<Device> d10 = device -> device.getType().equals("D10");

        // Combine all predicates into a list
        List<Predicate<Device>> predicates = Arrays.asList(compact, c2x, c5x, im, d10, point);

        // Reduce the list of predicates into a single predicate with 'or' logic
        Predicate<Device> combinedPredicate = predicates.stream()
                .reduce(x -> false, Predicate::or);

        // Use the combined predicate to filter the devices
        return expectedDevices.stream()
                .filter(combinedPredicate)
                .toList();
    }
}
