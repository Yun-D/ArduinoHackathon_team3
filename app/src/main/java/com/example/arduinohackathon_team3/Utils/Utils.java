package com.example.arduinohackathon_team3.Utils;

import java.util.UUID;

public class Utils {
    public static String getUserIdFromUUID(String userEmail) {
        long val = UUID.nameUUIDFromBytes(userEmail.getBytes()).getMostSignificantBits();
        return String.valueOf(val);
    }
}
