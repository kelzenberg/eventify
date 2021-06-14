package com.eventify.api.entities;

public class Views {
    public static class Meta {
    }

    public static class PublicShort extends Meta {
    }

    public static class PublicExtended extends PublicShort {
    }

    public static class Me extends PublicExtended {
    }
}
