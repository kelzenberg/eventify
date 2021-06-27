package com.eventify.api.entities.user.utils;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class VerificationUtil {
    public static String UUIDtoHash(UUID verificationUUID) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(verificationUUID.getMostSignificantBits());
        buffer.putLong(verificationUUID.getLeastSignificantBits());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
    }

    public static UUID hashToUUID(String verificationHash) {
        byte[] bytes = Base64.getUrlDecoder().decode(verificationHash);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    public static boolean verificationHashIsValid(UUID verificationUUID, String verificationHash) {
        return verificationUUID.equals(hashToUUID(verificationHash));
    }
}
