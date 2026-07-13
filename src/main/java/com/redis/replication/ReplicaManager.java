package com.redis.replication;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ReplicaManager {

    private final Set<SocketChannel> replicas = ConcurrentHashMap.newKeySet();

    public void addReplica(SocketChannel replica) {
        replicas.add(replica);
        System.out.println("Replica registered");
    }

    public void removeReplica(SocketChannel replica) {
        replicas.remove(replica);
    }

    public int count() {
        return replicas.size();
    }

    public void broadcast(String command) {

        System.out.println("broadcast:" + command);

        ByteBuffer buffer = ByteBuffer.wrap(command.getBytes(StandardCharsets.UTF_8));
        Iterator<SocketChannel> iterator = replicas.iterator();
        while (iterator.hasNext()) {
            SocketChannel replica = iterator.next();
            try {
                buffer.rewind();
                replica.write(buffer);
            } catch (IOException e) {
                iterator.remove();
                try {
                    replica.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

}