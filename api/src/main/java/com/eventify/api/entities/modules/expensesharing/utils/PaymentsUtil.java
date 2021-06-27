package com.eventify.api.entities.modules.expensesharing.utils;

import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.modules.expensesharing.entities.controllers.RequestCostShare;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.handlers.exceptions.EntityIsInvalidException;
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

    private double[] distributePerShareType(ShareType shareType, int[] shares, int total) throws EntityIsInvalidException {
        switch (shareType) {
            case DECIMAL:
                return Arrays.stream(shares).mapToDouble(value -> value / 100.0).toArray();
            case EQUAL:
                return distributionUtil.distributeCurrencyEqually(shares.length, total);
            case PERCENTAGE:
                return distributionUtil.distributeCurrencyByPercentage(shares, total);
            default:
                throw new EntityIsInvalidException("Payment contribution share type is invalid.");
        }
    }

    private List<RequestCostShare> convertToRequestCostShares(List<UUID> userIds, double[] shares) {
        List<Double> shareList = DoubleStream.of(shares).boxed().collect(Collectors.toList());

        // zip userIds & shares back to RequestCostShare list
        return IntStream.range(0, Math.min(userIds.size(), shareList.size()))
                .mapToObj(idx -> new RequestCostShare(userIds.get(idx), shareList.get(idx)))
                .collect(Collectors.toList());
    }

    public List<RequestCostShare> validateShares(
            ShareType shareType,
            List<RequestCostShare> shares,
            double amount
    ) throws EntityIsInvalidException {
        PayHelper payHelper = new PayHelper(shareType, shares, amount);
        boolean isSettled = payHelper.getShareType() == ShareType.EQUAL || payHelper.isSettled();

        if (!isSettled) {
            String exceptionMessage = String.format("Shares do not add up to %s (%s by %s, from %s).",
                    payHelper.getShareType() == ShareType.PERCENTAGE ? "100 percent" : "total amount",
                    Math.signum(payHelper.getTotalGap()) >= 0 ? "underpaid" : "overpaid",
                    Math.abs(payHelper.getTotalGap()),
                    payHelper.getShareType() == ShareType.PERCENTAGE ? 100 : payHelper.getTotal()
            );
            throw new EntityIsInvalidException(exceptionMessage);
        }

        double[] distributedShares = distributePerShareType(payHelper.getShareType(), payHelper.getShares(), payHelper.getTotal());
        List<RequestCostShare> validatedShares = convertToRequestCostShares(payHelper.getUserIds(), distributedShares);

        if (!new PayHelper(ShareType.DECIMAL, validatedShares, amount).isSettled()) { // payHelper.getTotal() return int, but double is needed
            throw new RuntimeException("Payment cost share validation failed.");
        }

        return validatedShares;
    }

    public void validateUserIds(ExpenseSharingModule expenseModule, User payer, List<RequestCostShare> shares) throws EntityIsInvalidException {
        List<UUID> paymentUserIds = shares.stream()
                .map(RequestCostShare::getUserId)
                .collect(Collectors.toList());
        paymentUserIds.add(payer.getId());

        List<UUID> eventUserIds = expenseModule.getEvent().getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList());

        List<UUID> unknownUserIds = paymentUserIds.stream()
                .filter(paymentUserId -> !eventUserIds.contains(paymentUserId))
                .collect(Collectors.toList());
        int unknownAmount = unknownUserIds.size();

        if (unknownAmount > 0) {
            throw new EntityIsInvalidException(String.format("%s %s not in this Event: %s",
                    unknownAmount,
                    unknownAmount > 1 ? "users are" : "user is",
                    unknownUserIds
            ));
        }
    }

    public double trimDoubleToDecimal(double doubleValue) {
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        return Double.parseDouble(decimalFormat.format(doubleValue));
    }

    public List<RequestCostShare> trimSharesToDecimal(List<RequestCostShare> requestShares) {
        requestShares.forEach(share -> share.setAmount(
                trimDoubleToDecimal(share.getAmount())
        ));
        return requestShares;
    }
}

