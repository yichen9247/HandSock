package com.server.handsock.common.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import java.util.*

object JwtUtils {
    private const val EXPIRATION_TIME_DAYS = 30L
    private var SECRET_KEY = GlobalService.handsockProps?.secretKey?.toByteArray()

    fun createToken(uid: Long, username: String): String {
        val now = Date()
        val expirationDate = Date(now.time + EXPIRATION_TIME_DAYS * 24 * 60 * 60 * 1000)
        return JWT.create()
            .withClaim("uid", uid)
            .withClaim("username", username)
            .withExpiresAt(expirationDate)
            .sign(Algorithm.HMAC256(SECRET_KEY))
    }

    fun verifyToken(token: String): Boolean {
        try {
            val algorithm = Algorithm.HMAC256(SECRET_KEY)
            val verifier: JWTVerifier = JWT.require(algorithm).build()
            verifier.verify(token)
            return true
        } catch (e: Exception) {
            return false
        }
    }
}