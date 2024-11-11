package com.server.handsock.service;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CacheService {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public CacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void writeRedisMessageCache(Long uid) {
        redisTemplate.opsForValue().set("handsock-msgCache:" + uid, "ok", 3, TimeUnit.SECONDS);
    }

    public void writeRedisUploadCache(Long uid) {
        redisTemplate.opsForValue().set("handsock-uploadCache:" + uid, "ok", 10, TimeUnit.SECONDS);
    }

    public Boolean validRedisMessageCache(long uid) {
        String cache = redisTemplate.opsForValue().get("handsock-msgCache:" + uid);
        return cache == null;
    }

    public Boolean validRedisUploadCache(long uid) {
        String cache = redisTemplate.opsForValue().get("handsock-uploadCache:" + uid);
        return cache == null;
    }
}
