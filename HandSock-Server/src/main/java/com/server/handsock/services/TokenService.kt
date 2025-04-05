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
        val cachedToken = redisTemplate.opsForValue()["handsock-userToken:$uid"]
        if (cachedToken != null) redisTemplate.delete("handsock-userToken:$uid")
    }

    fun generateUserToken(uid: Long, username: String, address: String): String? {
        val cachedToken = redisTemplate.opsForValue()["handsock-userToken:$uid"]
        if (cachedToken != null) return cachedToken // 直接使用原有Token（可实现多端同时在线）
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

    fun setScanStatus(qid: String, status: Int) {
        redisTemplate.opsForValue()["handsock-scanStatus:$qid", status.toString(), 30] = TimeUnit.SECONDS
    }

    fun removeScanStatus(qid: String) {
        val scanStatus = redisTemplate.opsForValue()["handsock-scanStatus:$qid"]
        if (scanStatus != null) redisTemplate.delete("handsock-scanStatus:$qid")
    }

    fun getScanStatus(qid: String): String? {
        val scanStatus = redisTemplate.opsForValue()["handsock-scanStatus:$qid"]
        return scanStatus
    }

    fun setScanTargetUser(qid: String, uid: String) {
        redisTemplate.opsForValue()["handsock-scanTargetUser:$qid", uid, 45] = TimeUnit.SECONDS
    }

    fun getScanTargetUser(qid: String): String? {
        val targetUser = redisTemplate.opsForValue()["handsock-scanTargetUser:$qid"]
        return targetUser
    }

    fun setOpenApiCache(type: String, address: String) {
        redisTemplate.opsForValue()["handsock-openai-$type:$address", "ok", 3] = TimeUnit.SECONDS
    }

    fun getOpenApiCache(type: String, address: String): Boolean {
        val requestStatus = redisTemplate.opsForValue()["handsock-openai-$type:$address"]
        return requestStatus == null
    }
}