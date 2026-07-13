package com.redis.replication;

public final class ReplicationContext {

    private static final ThreadLocal<Boolean> REPLICATING =
            ThreadLocal.withInitial(() -> false);

    private ReplicationContext() {
    }

    public static void begin() {
        REPLICATING.set(true);
    }

    public static void end() {
        REPLICATING.set(false);
    }

    public static boolean isReplicating() {
        return REPLICATING.get();
    }
}