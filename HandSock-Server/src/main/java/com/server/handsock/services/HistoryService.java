package com.server.handsock.services;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class HistoryService {

    private final RedisTemplate<String, String> redisTemplate;

    public HistoryService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String queryGroupLeastHistory(long gid) {
        return redisTemplate.opsForValue().get("handsock-leastHistory:" + gid);
    }
}
