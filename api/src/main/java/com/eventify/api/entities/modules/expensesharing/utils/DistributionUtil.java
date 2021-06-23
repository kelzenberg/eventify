package com.eventify.api.entities.modules.expensesharing.utils;

import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

@Component
public class DistributionUtil {
    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    protected static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##", symbols);

    private double[] shuffleArray(double[] arr) {
        Random random = new Random();
        IntStream.range(0, arr.length).forEach(idx -> {
            int randomIdx = random.nextInt(arr.length);
            double tempValue = arr[randomIdx];
            arr[randomIdx] = arr[idx];
            arr[idx] = tempValue;
        });
        return arr;
    }

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

        double[] concatenatedValues = DoubleStream.concat(Arrays.stream(largestValues), Arrays.stream(restValues)).toArray();
        return shuffleArray(concatenatedValues);
    }

    private double[] distributeByPercentages(int[] percentages, int amount) {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_UP);

        int[] integerShares = Arrays.stream(percentages)
                .mapToDouble(percentage -> amount * (percentage / 100.0)) // % -> double
                .map(percentageNum -> Double.parseDouble(DECIMAL_FORMAT.format(percentageNum))) // double -> decimal
                .mapToInt(decimal -> (int) Math.floor(decimal)) // decimal -> int
                .toArray();
        int shareLength = integerShares.length;
        int rest = amount - Arrays.stream(integerShares).reduce(0, Integer::sum);

        if (rest == 1) {
            int smallestShareIdx = IntStream.range(0, shareLength)
                    .reduce((curr, next) -> integerShares[curr] <= integerShares[next] ? curr : next)
                    .getAsInt();
            integerShares[smallestShareIdx] += 1;
        } else if (rest > 1) {
            IntStream.range(0, rest).forEach(idx -> {
                // start adding +1 from the back of the array
                int wrapIdx = (shareLength - (idx % shareLength)) - 1;
                integerShares[wrapIdx] += 1;
            });

            if (rest >= shareLength) {
                throw new RuntimeException("Percentage calculation seems defective.");
            }
        }

        return Arrays.stream(integerShares).mapToDouble(value -> value / 100.0).toArray();
    }

    public double[] distributeCurrencyEqually(int parts, int amount) {
        return distributeEqually(2, parts, amount);
    }

    public double[] distributeCurrencyByPercentage(int[] percentages, int amount) {
        return distributeByPercentages(percentages, amount);
    }
}
