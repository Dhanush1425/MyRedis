package com.redis.client;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        MyRedisClient client = new MyRedisClient("localhost", 6378);

        client.connect();

        String response = client.ping();

//        System.out.println(client.get("name"));
//        System.out.println(client.get("unknown"));
//        System.out.println(client.set("name","Gugan"));
//        System.out.println(client.del("name"));
//        System.out.println(client.del("name"));
        System.out.println(client.set("name","julie"));
        client.expire("name", 30);
        System.out.println(client.ttl("name"));
        Thread.sleep(5000);
        System.out.println(client.ttl("name"));
        System.out.println(response);

//        String ping = ClientEncoder.encode("PING", List.of());
//
//        System.out.println(ping);
//
//        String set = ClientEncoder.encode("SET", List.of("name", "gugu"));
//
//        System.out.println(set);

//        client.close();
    }
}