package com.redis.server.model;

public class RedisString extends RedisObject {

    private String value;

    public RedisString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return "STRING";
    }
}