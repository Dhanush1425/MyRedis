package com.redis.server.response;

public class BulkStringResponse implements Response {

    private final String value;

    public BulkStringResponse(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}