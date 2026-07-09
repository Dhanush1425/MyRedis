package com.redis.response;

import java.util.List;

public class ArrayResponse implements Response {

    private final List<String> values;

    public ArrayResponse(List<String> values) {
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }
}