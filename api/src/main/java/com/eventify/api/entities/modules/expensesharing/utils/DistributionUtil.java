package com.eventify.api.entities.modules.expensesharing.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Creator: Thank you (https://stackoverflow.com/users/633183/thank-you)
 * Source: https://stackoverflow.com/a/38925164 (last edited Dec 18 '18 at 4:31)
 * Licence: CC BY-SA 4.0 (https://creativecommons.org/licenses/by-sa/4.0/)
 * Changes to Original: Translation from JavaScript (Source) to Java (code below).
 */
public class DistributionUtil {

    private static double[] quotrem(int parts, double amount) {
        return new double[]{
                Math.floor(amount / parts),
                Math.floor(amount % parts)
        };
    }

    private static double[] distributeEqually(int precision, int parts, double amount) {
        double e = Math.pow(10, precision);
        double[] quotRem = quotrem(parts, amount * e);
        double largestValue = quotRem[0];
        int remaining = (int) quotRem[1];

        double[] largestValues = new double[remaining];
        Arrays.fill(largestValues, (largestValue + 1) / e);

        double[] restValues = new double[parts - remaining];
        Arrays.fill(restValues, largestValue / e);

        return DoubleStream.concat(Arrays.stream(largestValues), Arrays.stream(restValues)).toArray();
    }

    private static double[] distributeByPercentage(double[] percentages, double amount) {
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        final DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);

        double[] decimalAmounts = Arrays.stream(percentages)
                .map(value -> Double.parseDouble(decimalFormat.format(value)))
                .toArray();

        double remaining = amount - Arrays.stream(decimalAmounts).reduce(0.0, Double::sum);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        int roundedRemaining = Double.valueOf(Double.parseDouble(decimalFormat.format(remaining)) * 100).intValue();
//        System.out.println("[DEBUG] Remaining: " + remaining + " -> " + roundedRemaining);

        double[] adjustedDecimalAmounts;
        if (roundedRemaining >= decimalAmounts.length) {
            double[] distributedRemaining = distributeCurrencyEqually(decimalAmounts.length, remaining); // value from high -> low
            adjustedDecimalAmounts = IntStream.range(0, Math.min(decimalAmounts.length, distributedRemaining.length))
                    .mapToDouble(idx -> decimalAmounts[idx] + distributedRemaining[idx])
                    .toArray();
        } else {
            adjustedDecimalAmounts = IntStream.range(0, decimalAmounts.length)
                    .mapToDouble(idx -> idx >= roundedRemaining ? decimalAmounts[idx] : decimalAmounts[idx] + 0.01)
                    .toArray();
        }

//        double[] formattedPercentages = Arrays.stream(convertedPercentages)
//                .map(value -> Double.parseDouble(decimalFormat.format(value)))
//                .toArray();
//
//        System.out.println("[DEBUG] Decimal Amounts: " + Arrays.toString(decimalAmounts) + " -> " + Arrays.toString(adjustedDecimalAmounts));
//        System.out.println("[DEBUG] Difference: " + (amount - Arrays.stream(adjustedDecimalAmounts).reduce(0.0, Double::sum)));
//        System.out.println("[DEBUG] Smaller than 0.01? " + ((amount - Arrays.stream(adjustedDecimalAmounts).reduce(0.0, Double::sum)) < 0.01));
        return adjustedDecimalAmounts;
    }

    public static double[] distributeCurrencyEqually(int parts, double amount) {
        return distributeEqually(2, parts, amount);
    }

    public static double[] distributeCurrencyByPercentage(double[] percentages, double amount) {
        return distributeByPercentage(percentages, amount);
    }
}
