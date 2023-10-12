package com.intuit.userprofile.helper.impl;

import com.intuit.userprofile.helper.ILockHelper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class RedisLockHelper implements ILockHelper {

    private final RedissonClient redissonClient;

    private static final int MAX_LOCK_TIME = 10;

    @Autowired
    public RedisLockHelper( final RedissonClient redissonClient ) {
        this.redissonClient = redissonClient;
    }

    @Override
    public <T> T executeInLock( final String key, final Supplier<T> supplier) {
        // Acquire a lock
        RLock lock = redissonClient.getLock(key);
        lock.lock(MAX_LOCK_TIME, TimeUnit.SECONDS);

        // execute the method
        T result = supplier.get();

        // release the lock
        if ( lock.isLocked() ) {
            lock.unlock();
        }

        return result;
    }
}
