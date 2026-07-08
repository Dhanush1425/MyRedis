package com.redis.server;

import com.redis.config.RedisConfig;
import com.redis.executer.CommandExecuter;
import com.redis.network.ClientHandler;
import com.redis.network.ConnectionManager;
import com.redis.persistence.AOFRecovery;
import com.redis.persistence.AppendOnlyFile;
import com.redis.scheduler.ExpirationScheduler;
import com.redis.storage.MemoryDatabase;

public class RedisServer {

    public static void main(String[] args) {

        System.out.println("--------------------------------");
        System.out.println("        MyRedis Server");
        System.out.println("--------------------------------");

        // Create one shared database
        MemoryDatabase database = new MemoryDatabase();

        AppendOnlyFile aof = new AppendOnlyFile("appendonly.aof");

        CommandExecuter executer = new CommandExecuter(database, aof);

        //  background expiration scheduler
        ExpirationScheduler scheduler = new ExpirationScheduler(database);

        scheduler.start();

        // Create client handler
        ClientHandler clientHandler = new ClientHandler(executer);

        AOFRecovery recovery = new AOFRecovery(executer,"appendonly.aof");
        recovery.recover();

        // Start server
        ConnectionManager connectionManager = new ConnectionManager(clientHandler);

        connectionManager.start(RedisConfig.PORT);
    }
}




//
//
//Client Request
//
//↓
//
//Network Layer
//
//↓
//
//Protocol Parser
//
//↓
//
//Command Object
//
//↓
//
//Command Executor
//
//↓
//
//Storage Engine
//
//↓
//
//Response