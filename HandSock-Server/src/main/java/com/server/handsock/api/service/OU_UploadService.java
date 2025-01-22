package com.server.handsock.api.service;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.server.handsock.console.MD5Encoder;
import com.server.handsock.admin.dao.OR_UserDao;
import com.server.handsock.api.dao.OU_UploadDao;
import com.server.handsock.console.HandleResults;
import com.server.handsock.service.CacheService;
import com.server.handsock.service.TokenService;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.api.mod.OU_UploadModel;
import com.server.handsock.admin.mod.OR_UserModel;
import com.server.handsock.sockets.dao.OP_ChanDao;
import com.server.handsock.console.TimeFormatUtils;
import com.server.handsock.api.man.OU_UploadManage;
import com.server.handsock.sockets.mod.OP_ChanModel;
import org.springframework.web.multipart.MultipartFile;
import com.server.handsock.admin.service.OR_ChanService;
import com.server.handsock.admin.service.OR_UserService;
import com.server.handsock.sockets.service.OP_UserService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class OU_UploadService {

    private final OR_UserDao or_userDao;
    private final OP_ChanDao op_chanDao;
    private final TokenService tokenService;
    private final CacheService cacheService;
    private final OU_UploadDao ou_uploadDao;
    private final OP_UserService op_userService;
    private final OR_UserService or_userService;
    private final OR_ChanService or_chanService;
    private final OU_SystemService ou_systemService;
    
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public OU_UploadService(OR_UserDao or_userDao, OP_ChanDao op_chanDao, TokenService tokenService, CacheService cacheService, OU_UploadDao ou_uploadDao, OP_UserService op_userService, OR_UserService or_userService, OR_ChanService or_chanService, OU_SystemService ou_systemService) {
        this.or_userDao = or_userDao;
        this.op_chanDao = op_chanDao;
        this.tokenService = tokenService;
        this.cacheService = cacheService;
        this.ou_uploadDao = ou_uploadDao;
        this.or_userService = or_userService;
        this.or_chanService = or_chanService;
        this.op_userService = op_userService;
        this.ou_systemService = ou_systemService;
    }

    private static final String FILES_DIRECTORY = "upload/files/";
    private static final String AVATAR_DIRECTORY = "upload/avatar/";
    private static final String IMAGES_DIRECTORY = "upload/images/";

    /**
     * Handles file upload from client.
     *
     * @param headers HTTP headers containing user and group information.
     * @param file the file to be uploaded.
     * @return a map containing the result of the upload operation.
     */
    public Map<String, Object> clientUploadFile(HttpHeaders headers, @RequestParam("file") MultipartFile file, String type) throws IOException {

        List<String> uidHeader = headers.get("uid");
        List<String> gidHeader = headers.get("gid");
        List<String> tokenHeader = headers.get("token");

        if (!isValidRequest(uidHeader, gidHeader, tokenHeader)) return handleResults.handleResultByCode(400, null, "禁止访问");

        if (!cacheService.validRedisUploadCache(Long.parseLong(gidHeader.get(0)))) {
            return handleResults.handleResultByCode(402, null, "操作频率过快");
        } else cacheService.writeRedisUploadCache(Long.parseLong(gidHeader.get(0)));

        if (Objects.equals(type, "files") && !or_chanService.getChanOpenStatus(Long.parseLong(gidHeader.get(0)))) {
            return handleResults.handleResultByCode(402, null, "该频道未开启");
        }

        if (Objects.equals(type, "files") && ou_systemService.getSystemKeyStatus("taboo") && !op_userService.getUserAdminStatusByUid(Long.parseLong(uidHeader.get(0)))) {
            return handleResults.handleResultByCode(402, null, "全频禁言开启中");
        }

        if (!ou_systemService.getSystemKeyStatus("upload") && !op_userService.getUserAdminStatusByUid(Long.parseLong(uidHeader.get(0)))) {
            return handleResults.handleResultByCode(402, null, "上传权限未开放");
        }

        if (Objects.equals(type, "files") && or_userService.getUserTabooStatus(Long.parseLong(uidHeader.get(0))) && !op_userService.getUserAdminStatusByUid(Long.parseLong(uidHeader.get(0)))) {
            return handleResults.handleResultByCode(402, null, "你正在被禁言中");
        }

        String time = new MD5Encoder().toMD5(new TimeFormatUtils().formatTime("yyyy-MM-dd-HH-mm-ss"));
        Path uploadDir = createUploadDirectory(type, uidHeader.get(0), time);

        try {
            byte[] bytes = file.getBytes();
            Long uid = Long.parseLong(uidHeader.get(0));
            String fileName = file.getOriginalFilename();
            String filePath = uid + "/" + time + "/" + fileName;
            OU_UploadModel ou_uploadModel = new OU_UploadModel();
            Files.write(uploadDir.resolve(Objects.requireNonNull(fileName)), bytes);
            new OU_UploadManage().insertUploadFile(ou_uploadModel, uid, fileName, filePath, time, type, file.getSize());
            if (ou_uploadDao.insert(ou_uploadModel) > 0) {
                new ConsolePrints().printInfoLogV2("upload file " + uid + " " + filePath);
                return handleResults.handleResultByCode(200, new HashMap<>(){{
                    put("path", filePath);
                }}, "上传成功");
            } else return handleResults.handleResultByCode(400, null, "上传失败");
        } catch (IOException e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    /**
     * Validates the request by checking user and group existence and token validity.
     *
     * @param uidHeader user ID from headers.
     * @param gidHeader group ID from headers.
     * @param tokenHeader token from headers.
     * @return true if the request is valid, false otherwise.
     */
    private boolean isValidRequest(List<String> uidHeader, List<String> gidHeader, List<String> tokenHeader) {
        if (uidHeader == null || gidHeader == null || tokenHeader == null) return false;
        OR_UserModel user = or_userDao.selectOne(new QueryWrapper<OR_UserModel>().eq("uid", uidHeader.get(0)));
        OP_ChanModel group = op_chanDao.selectOne(new QueryWrapper<OP_ChanModel>().eq("gid", gidHeader.get(0)));
        boolean isTokenValid = tokenService.validUserToken(Long.parseLong(uidHeader.get(0)), tokenHeader.get(0));
        return user != null && group != null && isTokenValid;
    }

    /**
     * Creates the upload directory for the file.
     *
     * @param uid user ID.
     * @param time time stamp for directory naming.
     * @return the path to the upload directory.
     * @throws IOException if directory creation fails.
     */
    private Path createUploadDirectory(String type, String uid, String time) throws IOException {
        Path uploadDir;
        if (Objects.equals(type, "avatar")) {
            uploadDir = Paths.get(AVATAR_DIRECTORY + uid + "/" + time + "/");
        } else if (Objects.equals(type, "images")) {
            uploadDir = Paths.get(IMAGES_DIRECTORY + uid + "/" + time + "/");
        } else uploadDir = Paths.get(FILES_DIRECTORY + uid + "/" + time + "/");
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
        return uploadDir;
    }
}