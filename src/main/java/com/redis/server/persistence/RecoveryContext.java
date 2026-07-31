package com.redis.server.persistence;

public class RecoveryContext {

    private static boolean recovering = false;

    public static boolean isRecovering() {
        return recovering;
    }

    public static void startRecovery() {
        recovering = true;
    }

    public static void finishRecovery() {
        recovering = false;
    }
}