package com.redis.scheduler;

import com.redis.storage.MemoryDatabase;

public class ExpirationScheduler {

    private final MemoryDatabase database;

    public ExpirationScheduler(MemoryDatabase database) {
        this.database = database;
    }

    public void start(){
        Thread thread =  new Thread(() -> {
            while (true){
                try{
                    Thread.sleep(1000);
                    database.cleanUpExpiredKeys();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
