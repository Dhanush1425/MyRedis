package com.redis.executer;

import com.redis.command.Command;
import com.redis.commands.*;
//import com.redis.commands.HGetCommand;
import com.redis.persistence.AppendOnlyFile;
import com.redis.storage.MemoryDatabase;

import java.util.HashMap;
import java.util.Map;

public class CommandExecuter {

    private final Map<String, CommandHandler> handlers =
            new HashMap<>();

    public CommandExecuter(MemoryDatabase database, AppendOnlyFile aof) {

        handlers.put("PING", new PingCommand());

        handlers.put("SET", new SetCommand(database, aof));

        handlers.put("GET", new GetCommand(database));

        handlers.put("DEL", new DeleteCommand(database, aof));

        handlers.put("EXPIRE", new ExpireCommand(database, aof));

        handlers.put("HSET", new HSetCommand(database, aof));

        handlers.put("HGET", new HGetCommand(database));
    }

    public String execute(Command command) {

        CommandHandler handler = handlers.get(command.commandName().toUpperCase());

        if (handler == null) {
            return "ERROR: Unknown Command";
        }

        try {
            return handler.execute(command.arguments());
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}