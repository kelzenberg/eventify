package com.eventify.api.constants;

public final class AuthenticatedPaths {
    public static final String ME = "/me";
    public static final String EVENTS = "/events";
    public static final String MY_EVENTS = ME + EVENTS;
    public static final String MODULES = "/modules";
    public static final String EXPENSE_SHARING = MODULES + "/expense-sharing";
    public static final String PAYMENT_CONTRIBUTION = EXPENSE_SHARING + "/{expenseSharingId}" + "/payments";
}