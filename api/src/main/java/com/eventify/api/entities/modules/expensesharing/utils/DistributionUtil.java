package com.eventify.api.entities.modules.expensesharing.utils;

import java.util.Arrays;
import java.util.stream.DoubleStream;

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

    public static double[] distributeCurrencyEqually(int parts, double amount) {
        return distributeEqually(2, parts, amount);
    }
}
