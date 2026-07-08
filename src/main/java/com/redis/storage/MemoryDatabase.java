package com.redis.storage;

import com.redis.model.RedisHash;
import com.redis.model.RedisObject;
import com.redis.model.RedisString;

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

    public void hset(String key, String field, String value){
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