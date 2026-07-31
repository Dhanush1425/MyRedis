package com.redis.server.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisHash extends RedisObject {

    private final Map<String, String> fields =
            new ConcurrentHashMap<>();

    @Override
    public String getType() {
        return "HASH";
    }

    public void put(String field, String value) {
        fields.put(field, value);
    }

    public String get(String field) {
        return fields.get(field);
    }

    public boolean remove(String field) {
        return fields.remove(field) != null;
    }

    public boolean containsField(String field) {
        return fields.containsKey(field);
    }

    public Map<String, String> getFields() {
        return fields;
    }
}