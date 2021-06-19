package com.eventify.api.entities.modules.expensesharing.utils;

import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.entities.controllers.RequestCostShare;
import com.eventify.api.exceptions.EntityIsInvalidException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

@Component
public class PaymentsUtil {

    @Autowired
    DistributionUtil distributionUtil;

    private final DecimalFormat decimalFormat = DistributionUtil.DECIMAL_FORMAT;

    @Getter
    @ToString
    @EqualsAndHashCode
    private static class PayHelper {
        private final ShareType shareType;
        private final double amount;
        private final double[] shares;
        private final double totalGap;
        private final boolean isSettled;

        public PayHelper(ShareType shareType, double amount, List<RequestCostShare> shares) {
            double[] convertedShares = shares.stream()
                    .map(RequestCostShare::getAmount)
                    .mapToDouble(Double::doubleValue)
                    .toArray();

            double sharesSum = shares.stream()
                    .map(RequestCostShare::getAmount)
                    .reduce(0.0, Double::sum);

            double totalGap = amount - sharesSum;

            if (shareType == ShareType.PERCENTAGE) {
                convertedShares = Arrays.stream(convertedShares)
                        .map(percentage -> amount * (percentage / 100.0))
                        .toArray();
                totalGap = 100 - sharesSum;
            }

            this.shareType = shareType;
            this.amount = amount;
            this.shares = convertedShares;
            this.totalGap = totalGap;
            this.isSettled = this.totalGap == 0.0;
        }
    }

    public double trimDoubleToDecimal(double doubleValue) {
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        return Double.parseDouble(decimalFormat.format(doubleValue));
    }

    private List<RequestCostShare> trimShareAmounts(List<RequestCostShare> requestShares) {
        requestShares.forEach(share -> share.setAmount(
                trimDoubleToDecimal(share.getAmount())
        ));
        return requestShares;
    }

    public List<RequestCostShare> validateShares(
            ShareType shareType,
            double amount,
            List<RequestCostShare> requestShares
    ) throws EntityIsInvalidException {
        // e.g. PERCENTAGE 100:[20%, 50%, 30%] or DECIMAL 55:[19.99, 15.01, 20.00]
        PayHelper payHelper = new PayHelper(shareType, amount, trimShareAmounts(requestShares));

        if (payHelper.getShareType() != ShareType.EQUAL && !payHelper.isSettled()) {
            String exceptionMessage = String.format("Shares do not add up to %s (%s by %s, from %s).",
                    payHelper.getShareType() == ShareType.PERCENTAGE ? "100 percent" : "total amount",
                    Math.signum(payHelper.getTotalGap()) >= 0 ? "underpaid" : "overpaid",
                    Math.abs(payHelper.getTotalGap()),
                    payHelper.getShareType() == ShareType.PERCENTAGE ? 100 : payHelper.getAmount()
            );
            throw new EntityIsInvalidException(exceptionMessage);
        }

        double[] shareArray = new double[0];
        switch (shareType) {
            case DECIMAL:
                shareArray = payHelper.getShares();
                break;
            case EQUAL:
                shareArray = distributionUtil.distributeCurrencyEqually(payHelper.getShares().length, payHelper.getAmount());
                break;
            case PERCENTAGE:
                shareArray = distributionUtil.distributeCurrencyByPercentage(payHelper.getShares(), payHelper.getAmount());
                break;
            default:
                throw new EntityIsInvalidException("Payment contribution share type is invalid.");
        }

        List<UUID> userIds = requestShares.stream().map(RequestCostShare::getUserId).collect(Collectors.toList());
        List<Double> shareList = DoubleStream.of(shareArray).boxed().collect(Collectors.toList());

        // zip userIds & shares back to RequestCostShare list
        return IntStream.range(0, Math.min(userIds.size(), shareList.size()))
                .mapToObj(idx -> new RequestCostShare(userIds.get(idx), shareList.get(idx)))
                .collect(Collectors.toList());
    }

}

