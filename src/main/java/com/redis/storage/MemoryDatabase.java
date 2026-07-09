package com.redis.storage;

import com.redis.model.RedisHash;
import com.redis.model.RedisList;
import com.redis.model.RedisObject;
import com.redis.model.RedisString;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryDatabase {

    private final Map<String, RedisObject> storage = new ConcurrentHashMap<>();
    private final Map<String, Long> expiryMap = new ConcurrentHashMap<>();

    public void set(String key, String value) {
        storage.put(key, new RedisString(value));
    }

    public String get(String key) {
        if (isExpired(key)) {
            return null;
        }
        RedisObject object = storage.get(key);
        if (object == null){
            return null;
        }
        if (!(object instanceof RedisString)){
            return null;
        }
        RedisString stringObject = (RedisString) object;
        return stringObject.getValue();
    }

    public int hset(String key, String field, String value){
        RedisObject object = storage.get(key);
        RedisHash hash;
        if (object == null){
            hash  = new RedisHash();
            storage.put(key, hash);// this just add RedisHash obj in user1: -> RedisHash
        }else if (object instanceof RedisHash){
            hash = (RedisHash) object;
        }else {
            throw new IllegalArgumentException(
                    "WRONGTYPE Operation against a key holding the wrong kind of value");
        }
        hash.put(field, value);// here only actually adding fields in that RedisHash
        return 0;
    }


    public String hget(String key, String field) {
        RedisObject object = storage.get(key);

        if (object == null) {
            return null;
        }
        if (!(object instanceof RedisHash)) {
            throw new IllegalArgumentException("WRONGTYPE Operation against a key holding the wrong kind of value");
        }
        RedisHash hash = (RedisHash) object;
        return hash.get(field);
    }

    public int lpush(String key, String value) {

        RedisObject object = storage.get(key);
        RedisList list;

        if (object == null) {
            list = new RedisList();
            storage.put(key, list);
        } else if (object instanceof RedisList) {
            list = (RedisList) object;
        } else {
            throw new IllegalArgumentException("WRONGTYPE Operation against a key holding the wrong kind of value");
        }
        list.lpush(value);
        return list.size();
    }

    public int rpush(String key, String value) {

        RedisObject object = storage.get(key);
        RedisList list;

        if (object == null) {
            list = new RedisList();
            storage.put(key, list);
        } else if (object instanceof RedisList) {
            list = (RedisList) object;
        } else {
            throw new IllegalArgumentException("WRONGTYPE Operation against a key holding the wrong kind of value");
        }
        list.rpush(value);
        return list.size();
    }

    public String lpop(String key) {

        RedisObject object = storage.get(key);

        if (!(object instanceof RedisList)) {
            return null;
        }
        RedisList list = (RedisList) object;
        return list.lpop();
    }

    public String rpop(String key) {

        RedisObject object = storage.get(key);

        if (!(object instanceof RedisList)) {
            return null;
        }
        RedisList list = (RedisList) object;
        return list.rpop();
    }

    public List<String> lrange(String key, int start, int end) {

        RedisObject object = storage.get(key);
        if (!(object instanceof RedisList)) {
            return List.of();
        }

        RedisList list = (RedisList) object;
        return list.lrange(start, end);
    }

    public boolean delete(String key) {
        return storage.remove(key) != null;
    }

    public boolean expire(String key, long seconds) {
        if (!storage.containsKey(key)) {
            return false;
        }
        long expiryTime = System.currentTimeMillis() + (seconds * 1000);
        expiryMap.put(key, expiryTime);
        return true;
    }

    public void cleanUpExpiredKeys(){
        for (String key : expiryMap.keySet()){
            isExpired(key);
        }
    }

    private boolean isExpired(String key) {
        Long expiry = expiryMap.get(key);

        if (expiry == null) {
            return false;
        }

        if (System.currentTimeMillis() > expiry) {
            storage.remove(key);
            expiryMap.remove(key);
            return true;
        }
        return false;

    }

}