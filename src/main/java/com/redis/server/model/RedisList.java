package com.redis.server.model;

import java.util.LinkedList;
import java.util.List;

public class RedisList extends RedisObject {

    private final LinkedList<String> values =
            new LinkedList<>();

    // LPUSH
    public void lpush(String value) {
        values.addFirst(value);
    }

    // RPUSH
    public void rpush(String value) {
        values.addLast(value);
    }

    // LPOP
    public String lpop() {

        if (values.isEmpty()) {
            return null;
        }

        return values.removeFirst();
    }

    // RPOP
    public String rpop() {

        if (values.isEmpty()) {
            return null;
        }

        return values.removeLast();
    }

    public List<String> lrange(int start, int end) {

        int size = values.size();
        if (size == 0) {
            return List.of();
        }
        // Handle negative indexes
        if (start < 0) {
            start = size + start;
        }

        if (end < 0) {
            end = size + end;
        }

        // Clamp to valid range
        start = Math.max(0, start);
        end = Math.min(size - 1, end);

        if (start > end) {
            return List.of();
        }

        return new LinkedList<>(values.subList(start, end + 1));
    }

    // Number of elements
    public int size() {
        return values.size();
    }

    // Needed later for LRANGE
    public List<String> getValues() {
        return values;
    }

    @Override
    public String getType() {
        return "LIST";
    }
}