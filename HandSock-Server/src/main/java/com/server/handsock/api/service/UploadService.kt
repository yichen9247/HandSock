package com.server.handsock.api.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.admin.dao.ServerUserDao
import com.server.handsock.admin.mod.ServerUserModel
import com.server.handsock.admin.service.ServerSystemService
import com.server.handsock.api.dao.UploadDao
import com.server.handsock.api.man.UploadManage
import com.server.handsock.api.mod.UploadModel
import com.server.handsock.clients.dao.ClientChannelDao
import com.server.handsock.clients.mod.ClientChannelModel
import com.server.handsock.clients.service.ClientChannelService
import com.server.handsock.clients.service.ClientUserService
import com.server.handsock.services.CacheService
import com.server.handsock.services.TokenService
import com.server.handsock.utils.ConsoleUtils
import com.server.handsock.utils.HandUtils
import com.server.handsock.utils.IDGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class UploadService @Autowired constructor(
    private val uploadDao: UploadDao,
    private val tokenService: TokenService,
    private val cacheService: CacheService,
    private val serverUserDao: ServerUserDao,
    private val clientChannelDao: ClientChannelDao,
    private val clientUserService: ClientUserService,
    private val serverSystemService: ServerSystemService,
    private val clientChannelService: ClientChannelService
) {
    /**
     * Handles file upload from client.
     *
     * @param headers HTTP headers containing user and group information.
     * @param file    the file to be uploaded.
     * @return a map containing the result of the upload operation.
     */
    @Throws(IOException::class)
    fun clientUploadFile(headers: HttpHeaders, @RequestParam("file") file: MultipartFile, type: String): Map<String, Any> {
        val uidHeader = headers["uid"]
        val gidHeader = headers["gid"]
        val tokenHeader = headers["token"]
        val fileWith = type == "files" || type == "images"
        if (!isValidRequest(
            uidHeader = uidHeader,
            gidHeader = gidHeader,
            tokenHeader = tokenHeader
        )) return HandUtils.handleResultByCode(400, null, "禁止访问")

        if (!cacheService.validRedisUploadCache(gidHeader!![0].toLong())) {
            return HandUtils.handleResultByCode(402, null, "操作频率过快")
        } else cacheService.writeRedisUploadCache(gidHeader[0].toLong())


        if (fileWith && !clientChannelService.getChanOpenStatus(gidHeader[0].toLong())) {
            return HandUtils.handleResultByCode(402, null, "该频道未开启")
        }

        if (fileWith && serverSystemService.getSystemKeyStatus("taboo") && !clientUserService.getUserAdminStatusByUid(uidHeader!![0].toLong())) {
            return HandUtils.handleResultByCode(402, null, "全频禁言开启中")
        }

        if (!serverSystemService.getSystemKeyStatus("upload") && !clientUserService.getUserAdminStatusByUid(uidHeader!![0].toLong())) {
            return HandUtils.handleResultByCode(402, null, "上传权限未开放")
        }

        if (fileWith && clientUserService.getUserTabooStatus(uidHeader!![0].toLong()) && !clientUserService.getUserAdminStatusByUid(uidHeader[0].toLong())) {
            return HandUtils.handleResultByCode(402, null, "你正在被禁言中")
        }

        val time = HandUtils.encodeStringToMD5(HandUtils.formatTimeForString("yyyy-MM-dd-HH-mm-ss"))
        val uploadDir = createUploadDirectory(
            type = type,
            time = time,
            uid = uidHeader!![0]
        )

        try {
            val uploadModel = UploadModel()
            val uid = uidHeader[0].toLong()
            val fileName = file.originalFilename
            val filePath = "$uid/$time/$fileName"
            Files.write(uploadDir.resolve(Objects.requireNonNull(fileName)), file.bytes)
            UploadManage(HandUtils, IDGenerator).insertUploadFile(uploadModel, uid, fileName, filePath, time, type, file.size)
            if (uploadDao.insert(uploadModel) > 0) {
                ConsoleUtils.printInfoLog("upload file $uid $filePath")
                return HandUtils.handleResultByCode(200, object : HashMap<Any?, Any?>() {
                    init {
                        put("path", filePath)
                    }
                }, "上传成功")
            } else return HandUtils.handleResultByCode(400, null, "上传失败")
        } catch (e: IOException) {
            return HandUtils.printErrorLog(e)
        }
    }

    /**
     * Validates the request by checking user and group existence and token validity.
     *
     * @param uidHeader   user ID from headers.
     * @param gidHeader   group ID from headers.
     * @param tokenHeader token from headers.
     * @return true if the request is valid, false otherwise.
     */
    private fun isValidRequest(
        uidHeader: List<String>?,
        gidHeader: List<String>?,
        tokenHeader: List<String>?
    ): Boolean {
        if (uidHeader == null || gidHeader == null || tokenHeader == null) return false
        val user = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("uid", uidHeader[0]))
        val group = clientChannelDao.selectOne(QueryWrapper<ClientChannelModel>().eq("gid", gidHeader[0]))
        val isTokenValid = tokenService.validUserToken(uidHeader[0].toLong(), tokenHeader[0])
        return user != null && group != null && isTokenValid
    }

    /**
     * Creates the upload directory for the file.
     *
     * @param uid  user ID.
     * @param time time stamp for directory naming.
     * @return the path to the upload directory.
     * @throws IOException if directory creation fails.
     */
    @Throws(IOException::class)
    private fun createUploadDirectory(type: String, uid: String, time: String): Path {
        val uploadDir = if (type == "avatar") {
            Paths.get("$AVATAR_DIRECTORY$uid/$time/")
        } else if (type == "images") {
            Paths.get("$IMAGES_DIRECTORY$uid/$time/")
        } else Paths.get("$FILES_DIRECTORY$uid/$time/")
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir)
        return uploadDir
    }

    companion object {
        private const val FILES_DIRECTORY = "upload/files/"
        private const val AVATAR_DIRECTORY = "upload/avatar/"
        private const val IMAGES_DIRECTORY = "upload/images/"
    }
}