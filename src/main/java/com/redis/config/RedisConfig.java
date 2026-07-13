package com.redis.config;

public class RedisConfig {

    public static int PORT = 6378;

    public static boolean IS_REPLICA = false;

    public static String MASTER_HOST = "localhost";

    public static int MASTER_PORT = 6378;

    private RedisConfig() {
    }
}