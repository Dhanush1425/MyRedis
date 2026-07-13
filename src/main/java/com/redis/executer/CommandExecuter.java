package com.redis.executer;

import com.redis.command.Command;
import com.redis.commands.*;
//import com.redis.commands.HGetCommand;
import com.redis.persistence.AppendOnlyFile;
import com.redis.replication.ReplicaManager;
import com.redis.response.ErrorResponse;
import com.redis.response.Response;
import com.redis.storage.MemoryDatabase;

import java.util.HashMap;
import java.util.Map;

public class CommandExecuter {

    private final Map<String, CommandHandler> handlers =
            new HashMap<>();

    public CommandExecuter(MemoryDatabase database, AppendOnlyFile aof, ReplicaManager replicaManager) {

        handlers.put("PING", new PingCommand());

        handlers.put("SET", new SetCommand(database, aof, replicaManager));

        handlers.put("GET", new GetCommand(database));

        handlers.put("DEL", new DeleteCommand(database, aof, replicaManager));

        handlers.put("EXPIRE", new ExpireCommand(database, aof, replicaManager));

        handlers.put("HSET", new HSetCommand(database, aof, replicaManager));

        handlers.put("HGET", new HGetCommand(database));

        handlers.put("LPUSH", new LPushCommand(database, aof, replicaManager));

        handlers.put("LPOP", new LPopCommand(database, aof, replicaManager));

        handlers.put("RPUSH", new RPushCommand(database, aof, replicaManager));

        handlers.put("RPOP", new RPopCommand(database, aof, replicaManager));

        handlers.put("LRANGE", new LRangeCommand(database));
    }

    public Response execute(Command command) {

        CommandHandler handler = handlers.get(command.commandName().toUpperCase());

        if (handler == null) {
            return new ErrorResponse("Unknown Command");
        }

        try {
            return handler.execute(command.arguments());
        }catch (IllegalArgumentException e) {
            return new ErrorResponse(e.getMessage());
        }
    }
}