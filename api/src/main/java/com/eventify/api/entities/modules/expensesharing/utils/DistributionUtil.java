package com.eventify.api.entities.modules.expensesharing.utils;

import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

@Component
public class DistributionUtil {
    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    protected static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##", symbols);

    /**
     * Creator: Thank you (https://stackoverflow.com/users/633183/thank-you)
     * Source: https://stackoverflow.com/a/38925164 (last edited Dec 18 '18 at 4:31)
     * Licence: CC BY-SA 4.0 (https://creativecommons.org/licenses/by-sa/4.0/)
     * Changes to Original: Translation from JavaScript (Source) to Java (Method below).
     */
    private double[] quotrem(int parts, double amount) {
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
    private double[] distributeEqually(int precision, int parts, int amount) {
        double e = Math.pow(10, precision);
        double[] quotRem = quotrem(parts, (amount / 100.0) * e);
        double largestValue = quotRem[0];
        int rest = (int) quotRem[1];

        double[] largestValues = new double[rest];
        Arrays.fill(largestValues, (largestValue + 1) / e);

        double[] restValues = new double[parts - rest];
        Arrays.fill(restValues, largestValue / e);

        return DoubleStream.concat(Arrays.stream(largestValues), Arrays.stream(restValues)).toArray();
    }

//    private double[] distributeByPercentage(double[] shares, double amount) {
//        DECIMAL_FORMAT.setRoundingMode(RoundingMode.FLOOR);
//
//        double[] decimalShares = Arrays.stream(shares)
//                .map(value -> Double.parseDouble(DECIMAL_FORMAT.format(value)))
//                .toArray();
//        double rest = amount - Arrays.stream(decimalShares)
//                .reduce(0.0, Double::sum);
//
//        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
//
//        int remainingRounded = Double.valueOf(
//                Double.parseDouble(DECIMAL_FORMAT.format(rest)) * 100
//        ).intValue();
//
//        double[] adjustedDecimals;
//        if (remainingRounded >= decimalShares.length) {
//            double[] remainingEquallyDistributed = distributeCurrencyEqually(decimalShares.length, rest);
//            adjustedDecimals = IntStream.range(0, decimalShares.length)
//                    .mapToDouble(idx -> decimalShares[idx] + remainingEquallyDistributed[idx])
//                    .toArray();
//        } else {
//            adjustedDecimals = IntStream.range(0, decimalShares.length)
//                    .mapToDouble(idx -> idx >= remainingRounded ? decimalShares[idx] : decimalShares[idx] + 0.01)
//                    .toArray();
//        }
//
//        // TODO: some values still create broken shares, e.g. 77.11 with 55% and 45% shares -> 42.419999999999995
//        return adjustedDecimals;
//    }

    private double[] distributeByPercentage2(int[] percentages, int amount) {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_UP);

        System.out.println("AMOUNT " + amount);
        System.out.println("SHARES " + Arrays.toString(percentages));

        int[] integerShares = Arrays.stream(percentages)
                .mapToDouble(percentage -> amount * (percentage / 100.0)) // % -> double
                .map(percentageNum -> Double.parseDouble(DECIMAL_FORMAT.format(percentageNum))) // double -> decimal
                .mapToInt(decimal -> (int) Math.floor(decimal))
                .toArray();
        int rest = amount - Arrays.stream(integerShares).reduce(0, Integer::sum);

        System.out.println("INT_SHARES " + Arrays.toString(integerShares));
        System.out.println("REST " + rest);

        if (rest == 1) {
            int minShareIdx = IntStream.range(0, integerShares.length)
                    .reduce((curr, next) -> integerShares[curr] <= integerShares[next] ? curr : next)
                    .getAsInt();
            integerShares[minShareIdx] += 1;
        } else if (rest > 1) {
            IntStream.range(0, rest).forEach(idx -> {
                int wrapIdx = (integerShares.length - (idx % integerShares.length)) - 1;
                integerShares[wrapIdx] += 1;
            });
            if (rest >= integerShares.length) {
                System.out.println("ULTRA REST " + rest);
            }
        }

        System.out.println("SUM " + Arrays.stream(integerShares).reduce(0, Integer::sum));
        System.out.println("EQUAL " + (Arrays.stream(integerShares).reduce(0, Integer::sum) == amount));
        System.out.println("PERC " + (Arrays.toString(Arrays.stream(integerShares).mapToDouble(share -> Math.round((double) share / (double) amount * 100.0)).toArray())));
        return Arrays.stream(integerShares).mapToDouble(value -> value / 100.0).toArray();
    }

    public double[] distributeCurrencyEqually(int parts, int amount) {
        return distributeEqually(2, parts, amount);
    }

    public double[] distributeCurrencyByPercentage(int[] percentages, int amount) {
        return distributeByPercentage2(percentages, amount);
    }
}
