package com.eventify.api.entities.modules.expensesharing.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class DistributionUtil {
    protected static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    protected static final DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);

    /**
     * Creator: Thank you (https://stackoverflow.com/users/633183/thank-you)
     * Source: https://stackoverflow.com/a/38925164 (last edited Dec 18 '18 at 4:31)
     * Licence: CC BY-SA 4.0 (https://creativecommons.org/licenses/by-sa/4.0/)
     * Changes to Original: Translation from JavaScript (Source) to Java (Method below).
     */
    private static double[] quotrem(int parts, double amount) {
        return new double[]{
                Math.floor(amount / parts),
                Math.floor(amount % parts)
        };
    }

    /**
     * Creator: Thank you (https://stackoverflow.com/users/633183/thank-you)
     * Source: https://stackoverflow.com/a/38925164 (last edited Dec 18 '18 at 4:31)
     * Licence: CC BY-SA 4.0 (https://creativecommons.org/licenses/by-sa/4.0/)
     * Changes to Original: Translation from JavaScript (Source) to Java (Method below).
     */
    private static double[] distributeEqually(int precision, int parts, double amount) {
        double e = Math.pow(10, precision);
        double[] quotRem = quotrem(parts, amount * e);
        double largestValue = quotRem[0];
        int rest = (int) quotRem[1];

        double[] largestValues = new double[rest];
        Arrays.fill(largestValues, (largestValue + 1) / e);

        double[] restValues = new double[parts - rest];
        Arrays.fill(restValues, largestValue / e);

        return DoubleStream.concat(Arrays.stream(largestValues), Arrays.stream(restValues)).toArray();
    }

    private static double[] distributeByPercentage(double[] shares, double amount) {
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);

        double[] decimalShares = Arrays.stream(shares)
                .map(value -> Double.parseDouble(decimalFormat.format(value)))
                .toArray();
        double rest = amount - Arrays.stream(decimalShares)
                .reduce(0.0, Double::sum);

        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

        int remainingRounded = Double.valueOf(
                Double.parseDouble(decimalFormat.format(rest)) * 100
        ).intValue();

        double[] adjustedDecimals;
        if (remainingRounded >= decimalShares.length) {
            double[] remainingEquallyDistributed = distributeCurrencyEqually(decimalShares.length, rest);
            adjustedDecimals = IntStream.range(0, decimalShares.length)
                    .mapToDouble(idx -> decimalShares[idx] + remainingEquallyDistributed[idx])
                    .toArray();
        } else {
            adjustedDecimals = IntStream.range(0, decimalShares.length)
                    .mapToDouble(idx -> idx >= remainingRounded ? decimalShares[idx] : decimalShares[idx] + 0.01)
                    .toArray();
        }

        return adjustedDecimals;
    }

    public static double[] distributeCurrencyEqually(int parts, double amount) {
        return distributeEqually(2, parts, amount);
    }

    public static double[] distributeCurrencyByPercentage(double[] shares, double amount) {
        return distributeByPercentage(shares, amount);
    }
}
