package com.eventify.api.entities.modules.expensesharing.utils;

import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.entities.controllers.RequestCostShare;
import com.eventify.api.exceptions.EntityIsInvalidException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode
public class PayHelper {
    private final ShareType shareType;
    private final int total;
    private final int[] shares;
    private final List<UUID> userIds;
    private final int totalGap;
    private final boolean isSettled;

    private int convertDecimalToInt(double value) {
        return (int) (value * 100);
    }

    private int[] convertToIntArray(ShareType shareType, List<RequestCostShare> shares) {
        switch (shareType) {
            case DECIMAL:
                return shares.stream()
                        .mapToInt(share -> convertDecimalToInt(share.getAmount()))
                        .toArray();
            case EQUAL:
                // share amounts will be ignored on equal distribution
                return new int[shares.size()];
            case PERCENTAGE:
                return shares.stream()
                        .mapToInt(share -> (int) share.getAmount()) // % to #: amount * (percentage / 100)
                        .toArray();
            default:
                throw new EntityIsInvalidException("Payment contribution share type is invalid.");
        }
    }

    public PayHelper(ShareType shareType, List<RequestCostShare> shares, double total) {
        int[] integerShares = convertToIntArray(shareType, shares);
        int integerAmount = convertDecimalToInt(total);
        int integerSharesSum = Arrays.stream(integerShares).reduce(0, Integer::sum);

        this.shareType = shareType;
        this.total = integerAmount;
        this.shares = integerShares;
        this.userIds = shares.stream().map(RequestCostShare::getUserId).collect(Collectors.toList());
        this.totalGap = shareType == ShareType.PERCENTAGE ? 100 - integerSharesSum : integerAmount - integerSharesSum;
        this.isSettled = this.totalGap == 0;
    }
}
