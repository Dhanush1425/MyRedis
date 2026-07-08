package com.redis.commands;

import com.redis.storage.MemoryDatabase;

import java.util.List;

public class GetCommand implements CommandHandler {

    private final MemoryDatabase database;

    public GetCommand(MemoryDatabase database) {

        this.database = database;

    }

    @Override
    public String execute(List<String> arguments) {

        if (arguments.size() != 1) {

            return "ERROR: GET requires key";

        }

        String value = database.get(arguments.get(0));

        return value == null ? "(nil)" : value;

    }

}