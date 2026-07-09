package com.redis.commands;

import com.redis.storage.MemoryDatabase;
import java.util.List;

public class LRangeCommand implements CommandHandler {

    private final MemoryDatabase database;

    public LRangeCommand(MemoryDatabase database) {
        this.database = database;
    }

    @Override
    public String execute(List<String> args) {

        if (args.size() != 3) {
            return "ERROR: LRANGE requires key start end";
        }

        String key = args.get(0);

        int start = Integer.parseInt(args.get(1));
        int end = Integer.parseInt(args.get(2));

        List<String> values = database.lrange(key, start, end);

        if (values.isEmpty()) {
            return "(empty list)";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            result.append(i + 1)
                    .append(") ")
                    .append(values.get(i))
                    .append(" ");
        }

        return result.toString();
    }
}