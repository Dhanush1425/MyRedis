package com.redis.server.response;

public class SimpleStringResponse implements Response {

    private final String value;

    public SimpleStringResponse(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}