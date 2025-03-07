package com.server.handsock.utils

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import javax.imageio.ImageIO

object HandUtils {
    private const val USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,16}$"
    private const val PASSWORD_PATTERN = "^[a-zA-Z0-9_@#$%^&*!-]{6,18}$"

    fun printErrorLog(e: Exception): Map<String, Any> {
        ConsoleUtils.printErrorLog(e)
        return handleResultByCode(500, null, "服务端异常")
    }

    fun isValidUsername(username: String): Boolean {
        return !username.matches(USERNAME_PATTERN.toRegex()) || username.length < 5 || username.length > 20
    }

    fun isValidPassword(password: String): Boolean {
        return !password.matches(PASSWORD_PATTERN.toRegex()) || password.length < 6 || password.length > 20
    }

    fun stripHtmlTagsForString(html: String): String {
        return Pattern.compile("<.*?>", Pattern.DOTALL).matcher(html).replaceAll("")
    }

    fun formatBytesForString(bytes: Long): String {
        return if (bytes < 1024) {
            bytes.toString() + "B"
        } else if (bytes < 1024 * 1024) {
            String.format("%.1fKB", bytes / 1024.0)
        } else if (bytes < 1024L * 1024L * 1024L) {
            String.format("%.1fMB", bytes / (1024.0 * 1024))
        } else String.format("%.1fGB", bytes / (1024.0 * 1024 * 1024))
    }

    fun handleResultByCode(code: Int, data: Any?, message: String): Map<String, Any> {
        val result: MutableMap<String, Any> = HashMap()
        result["code"] = code
        result["message"] = message
        if (data != null) result["data"] = data
        return result
    }

    fun formatTimeForString(pattern: String?): String {
        val now = OffsetDateTime.now()
        val formatter = pattern?.let { DateTimeFormatter.ofPattern(it) }
        return now.format(formatter)
    }

    fun sendGlobalMessage(server: SocketIOServer, event: String, content: Any?) {
        val broadcastOperations = server.broadcastOperations
        broadcastOperations.sendEvent(event, content)
    }

    fun sendRoomMessage(server: SocketIOServer, client: SocketIOClient, event: String, content: Any?) {
        @Suppress("UNCHECKED_CAST")
        val headers = client.handshakeData.authToken as Map<String, Any>
        val broadcastOperations = server.getRoomOperations(headers["gid"].toString())
        broadcastOperations.sendEvent(event, content)
    }

    fun checkImageValidExtension(file: MultipartFile): Boolean {
        val fileName = checkNotNull(file.originalFilename)
        val isValidExtension = fileName.endsWith(".jpg")
                || fileName.endsWith(".gif")
                || fileName.endsWith(".png")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".webp")
        if (!isValidExtension) return false
        try {
            ImageIO.read(file.inputStream) ?: return false
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
            return false
        }
        return true
    }

    fun encodeStringToSHA256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val encodedHash = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
        val hexString = StringBuilder(2 * encodedHash.size)
        for (b in encodedHash) {
            val hex = Integer.toHexString(0xff and b.toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    }

    fun encodeStringToMD5(text: String): String {
        val md = MessageDigest.getInstance("MD5")
        val messageDigest = md.digest(text.toByteArray())
        val hexString = StringBuilder()
        for (b in messageDigest) {
            val hex = Integer.toHexString(0xff and b.toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    }
}