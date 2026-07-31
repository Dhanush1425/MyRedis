package com.redis.server.response;

public class IntegerResponse implements Response {

    private final int value;

    public IntegerResponse(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}