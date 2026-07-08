package com.redis.commands;

import com.redis.storage.MemoryDatabase;

import java.util.List;

public class HGetCommand implements CommandHandler {

    private final MemoryDatabase database;

    public HGetCommand(MemoryDatabase database) {
        this.database = database;
    }

    @Override
    public String execute(List<String> args) {

        if (args.size() != 2) {
            return "ERROR: HGET requires key field";
        }

        String value = database.hget(args.get(0), args.get(1));
        if (value == null) {
            return "(nil)";
        }
        return value;
    }
}