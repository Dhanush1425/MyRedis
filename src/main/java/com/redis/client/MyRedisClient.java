package com.redis.client;

import java.io.IOException;
import java.util.List;

public class MyRedisClient {

    private final ClientConnection connection;

    public MyRedisClient(String host, int port) {
        this.connection = new ClientConnection(host, port);
    }

    /**
     * Opens a connection to the MyRedis server.
     */
    public void connect() throws IOException {
        connection.connect();
        System.out.println("Connected to MyRedis");
    }

    /**
     * Closes the connection.
     */
    public void close() throws IOException {
        connection.close();
        System.out.println("Disconnected from MyRedis");
    }

    /**
     * Returns whether the client is currently connected.
     */
    public boolean isConnected() {
        return connection.isConnected();
    }

    public String ping() throws IOException {
        return (String) execute("PING", List.of());
    }

    public String get(String key) throws IOException {
        return (String) execute("GET", List.of(key));
    }

    public boolean set(String key, String value) throws IOException {
        execute("SET", List.of(key, value));
        return true;
    }

    public long del(String key) throws IOException {
        return (Long) execute("DEL", List.of(key));
    }

    public boolean expire(String key, long seconds) throws IOException {
        long result = (Long) execute("EXPIRE", List.of(key, String.valueOf(seconds)));
        return result == 1;
    }

    public long ttl(String key) throws IOException {
        return (Long) execute("TTL", List.of(key));
    }

    public boolean setnx(String key, String value) throws IOException {
        long result = (Long) execute("SETNX", List.of(key, value));
        return result == 1;
    }

    public boolean exists(String key) throws IOException {
        long result = (Long) execute("EXISTS", List.of(key));
        return result == 1;
    }

    public long incr(String key) throws IOException {
        return (Long) execute("INCR", List.of(key));
    }

    public long decr(String key) throws IOException {
        return (Long) execute("DECR", List.of(key));
    }

    /**
     * Executes a Redis command.
     */
    private Object execute(String command, List<String> arguments) throws IOException {
        String request = ClientEncoder.encode(command, arguments);
        connection.send(request);
        String response = connection.receive();
        return ClientDecoder.decode(response);
    }

    /**
     * Returns the underlying connection.
     * Package-private because only SDK classes should use it.
     */
    ClientConnection getConnection() {
        return connection;
    }
}