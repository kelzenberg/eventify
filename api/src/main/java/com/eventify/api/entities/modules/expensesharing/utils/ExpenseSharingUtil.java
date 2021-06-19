package com.eventify.api.entities.modules.expensesharing.utils;

import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.entities.controllers.RequestCostShare;
import com.eventify.api.exceptions.EntityIsInvalidException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

@Component
public class ExpenseSharingUtil {

    @Getter
    @ToString
    @EqualsAndHashCode
    private static class Difference {
        private final ShareType shareType;
        private final double amount;
        private final double[] shares;
        private final double totalGap;
        private final boolean isEqual;

        public Difference(ShareType shareType, double amount, List<RequestCostShare> shares) {
            this.shareType = shareType;
            this.amount = amount;
            this.shares = shares.stream().map(RequestCostShare::getAmount).mapToDouble(Double::doubleValue).toArray();
            this.totalGap = (shareType == ShareType.PERCENTAGE ? 100 : amount)
                    - shares.stream().map(RequestCostShare::getAmount).reduce(0.0, Double::sum);
            this.isEqual = this.totalGap == 0.0;
        }
    }

    private double[] convertPercentageToDecimal(double amount, double[] shares) {
        return Arrays.stream(shares)
                .map(percentage -> amount * (percentage / 100.0))
                .toArray();
    }

    public List<RequestCostShare> validateShares(ShareType shareType, double amount, List<RequestCostShare> requestShares) throws EntityIsInvalidException {
        // e.g. PERCENTAGE 100:[20%, 50%, 30%] or DECIMAL 55:[19.99, 15.01, 20.00]
        Difference diff = new Difference(shareType, amount, requestShares);

        if (shareType != ShareType.EQUAL && !diff.isEqual()) {
            throw new EntityIsInvalidException("Shares do not add up to the total amount paid or 100%.");
        }

        double[] shareArray;
        switch (shareType) {
            case DECIMAL:
                shareArray = diff.getShares();
                break;
            case EQUAL:
                shareArray = DistributionUtil.distributeCurrencyEqually(diff.getShares().length, amount);
                break;
            case PERCENTAGE:
                throw new RuntimeException("Currently unsupported share type (PERCENTAGE).");
//                double[] decimalShares = convertPercentageToDecimal(amount, diff.getShares());
//                shareArray = DistributionUtil.distributeCurrencyByPercentage(decimalShares, amount);
//                break;
            default:
                throw new EntityIsInvalidException("Payment contribution share type is invalid.");
        }

        List<UUID> userIds = requestShares.stream().map(RequestCostShare::getUserId).collect(Collectors.toList());
        List<Double> shares = DoubleStream.of(shareArray).boxed().collect(Collectors.toList());

        // zip userIds & shares back to RequestCostShare list
        return IntStream.range(0, Math.min(userIds.size(), shares.size()))
                .mapToObj(idx -> new RequestCostShare(userIds.get(idx), shares.get(idx)))
                .collect(Collectors.toList());
    }

}

