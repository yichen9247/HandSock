package com.server.handsock.services

import com.server.handsock.utils.HandUtils
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class TokenService @Autowired constructor(private val redisTemplate: RedisTemplate<String, String>) {
    fun removeUserToken(uid: Long) {
        val cachedToken = redisTemplate.opsForValue()[uid.toString()]
        if (cachedToken != null) redisTemplate.delete(uid.toString())
    }

    fun generateUserToken(uid: Long, username: String, address: String): String? {
        val nanoTime = System.nanoTime().toString()
        val randomString = RandomStringUtils.randomAlphanumeric(16)
        val formatTime = HandUtils.formatTimeForString("yyyy-MM-dd HH:mm:ss.SSS")
        var token = HandUtils.encodeStringToSHA256(uid.toString() + username + address + nanoTime + randomString + formatTime)
        token = Base64.getEncoder().encodeToString(token.toByteArray(StandardCharsets.UTF_8))
        redisTemplate.opsForValue()["handsock-userToken:$uid", token, 30] = TimeUnit.DAYS
        return token
    }

    fun validUserToken(uid: Long, oldToken: String): Boolean {
        val cachedToken = redisTemplate.opsForValue()["handsock-userToken:$uid"]
        return if (cachedToken != null) {
            cachedToken == oldToken
        } else false
    }
}
