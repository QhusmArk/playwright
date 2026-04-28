package com.example.api.helpers;

import java.util.AbstractMap;
import java.util.List;

public class NoiseCalculators {

    /**
     * Method that takes intervals with equal, or different, length of measuring periods.
     * @param leqDataList
     * @return
     */
    public static double combineLeq(List<AbstractMap.SimpleImmutableEntry<Double, Double>> leqDataList) {
        double totalT = 0.0;
        double sum = 0.0;

        for (AbstractMap.SimpleImmutableEntry<Double, Double> data : leqDataList) {
            double leq = data.getKey();
            double timeSeconds = data.getValue();
            totalT += timeSeconds;
            sum += timeSeconds * Math.pow(10.0, leq / 10.0);
        }

        // Calculate the combined Leq
        double combinedLeq = 10.0 * Math.log10(sum / totalT);
        return combinedLeq;
    }

    /**
     * Calculate Leq from many Leq's.
     */
    public static double calculateAdvLeq(List<Double> leqValues) {
        double sum = 0;

        // Convert each value to 10^(value/10) and sum them
        for (Double value : leqValues) {
            sum += Math.pow(10, value / 10);
        }

        // Calculate the average of the sum
        double average = sum / leqValues.size();

        // Compute 10 * log10(average)
        double dB = 10 * Math.log10(average);
        return Math.round(dB * 100.0) / 100.0;
    }

    /**
     * Converts Leq in Pascal to dB.
     */
    public static double calculateLeq(double value, int interval_time_sec) {
        double dB = 10 * Math.log10(value / (interval_time_sec * 64000)) + 94;
        return Math.round(dB * 100.0) / 100.0;
    }


}
