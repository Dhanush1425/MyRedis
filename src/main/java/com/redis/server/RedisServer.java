package com.redis.server;

import com.redis.config.RedisConfig;
import com.redis.executer.CommandExecuter;
import com.redis.network.ClientHandler;
import com.redis.network.ConnectionManager;
import com.redis.persistence.AOFRecovery;
import com.redis.persistence.AppendOnlyFile;
import com.redis.pubsub.PubSubManager;
import com.redis.replication.ReplicaClient;
import com.redis.replication.ReplicaManager;
import com.redis.scheduler.ExpirationScheduler;
import com.redis.storage.MemoryDatabase;

import java.io.IOException;

public class RedisServer {

    public static void main(String[] args) {
        if (args.length > 0) {
            if ("master".equalsIgnoreCase(args[0])) {

                RedisConfig.IS_REPLICA = false;

                if (args.length >= 2) {
                    RedisConfig.PORT = Integer.parseInt(args[1]);
                }

            } else if ("replica".equalsIgnoreCase(args[0])) {
                RedisConfig.IS_REPLICA = true;
                RedisConfig.PORT = Integer.parseInt(args[1]);
                RedisConfig.MASTER_HOST = args[2];
                RedisConfig.MASTER_PORT = Integer.parseInt(args[3]);
            }
        }

        System.out.println("--------------------------------");
        System.out.println("        MyRedis Server");
        System.out.println("--------------------------------");

        if (RedisConfig.IS_REPLICA) {
            System.out.println("Mode : Replica");
            System.out.println("Port : " + RedisConfig.PORT);
            System.out.println("Master : " +
                    RedisConfig.MASTER_HOST + ":" +
                    RedisConfig.MASTER_PORT);

        } else {
            System.out.println("Mode : Master");
            System.out.println("Port : " + RedisConfig.PORT);
        }
        ReplicaManager replicaManager = new ReplicaManager();

        // Shared database
        MemoryDatabase database = new MemoryDatabase();

        // Persistence
        AppendOnlyFile aof = new AppendOnlyFile("appendonly.aof");

        // Command executor
        CommandExecuter executer = new CommandExecuter(database, aof, replicaManager);

        // Recover previous data
        AOFRecovery recovery = new AOFRecovery(executer, "appendonly.aof");
        recovery.recover();

        // Start expiration scheduler
        ExpirationScheduler scheduler = new ExpirationScheduler(database);
        scheduler.start();

        // Shared managers
        PubSubManager pubSubManager = new PubSubManager();

        // Client handler
        ClientHandler clientHandler = new ClientHandler(executer, pubSubManager, replicaManager);

        // Start replication only if this server is a replica
        if (RedisConfig.IS_REPLICA) {

            ReplicaClient replicaClient = new ReplicaClient(RedisConfig.MASTER_HOST, RedisConfig.MASTER_PORT, executer);

            Thread replicationThread = new Thread(() -> {
                try {
                    replicaClient.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            replicationThread.setDaemon(true);
            replicationThread.start();
        }

        // Start TCP server
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