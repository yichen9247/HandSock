package com.server.handsock.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class CacheService @Autowired constructor(private val redisTemplate: RedisTemplate<String, String>) {
    fun writeRedisMessageCache(uid: Long) {
        redisTemplate.opsForValue()["handsock-msgCache:$uid", "ok", 2] = TimeUnit.SECONDS
    }

    fun writeRedisUploadCache(uid: Long) {
        redisTemplate.opsForValue()["handsock-uploadCache:$uid", "ok", 10] = TimeUnit.SECONDS
    }

    fun validRedisMessageCache(uid: Long): Boolean {
        val cache = redisTemplate.opsForValue()["handsock-msgCache:$uid"]
        return cache == null
    }

    fun validRedisUploadCache(uid: Long): Boolean {
        val cache = redisTemplate.opsForValue()["handsock-uploadCache:$uid"]
        return cache == null
    }
}
