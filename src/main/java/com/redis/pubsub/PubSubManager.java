package com.redis.pubsub;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PubSubManager {

    private final Map<String , Set<SocketChannel>> channels = new ConcurrentHashMap<>();

    public void subscribe(String channel, SocketChannel client){
        channels.computeIfAbsent(channel, k -> ConcurrentHashMap.newKeySet()).add(client);
        System.out.println(channels);
    }

    public void unsubscribe(String channel, SocketChannel client){
        Set<SocketChannel> subscribers = channels.get(channel);
        if (subscribers == null){
            return;
        }
        subscribers.remove(client);
        if (subscribers.isEmpty()){
            channels.remove(channel);
        }
    }

    public void removeClient(SocketChannel client) {

        for (Set<SocketChannel> subscribers : channels.values()) {
            subscribers.remove(client);
        }

        channels.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    public int publish(String channel, String message) {

        Set<SocketChannel> subscribers = channels.get(channel);

        if (subscribers == null) {
            return 0;
        }

        String resp = encodePubSubMessage(channel, message);

        ByteBuffer buffer = ByteBuffer.wrap(resp.getBytes(StandardCharsets.UTF_8));

        int delivered = 0;

        for (SocketChannel client : subscribers) {
            try {
                buffer.rewind();        // reset the position in buffer
                // while writing -> internally has position=0 and limit =35 , then after writing pos = 35 ,then for client B reset pos to 0.
                client.write(buffer);
                delivered++;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return delivered;
    }



    private String encodePubSubMessage(String channel, String message) {

        return "*3\r\n" +
                "$7\r\nmessage\r\n" +
                "$" + channel.length() + "\r\n" +
                channel + "\r\n" +
                "$" + message.length() + "\r\n" +
                message + "\r\n";
    }
}
