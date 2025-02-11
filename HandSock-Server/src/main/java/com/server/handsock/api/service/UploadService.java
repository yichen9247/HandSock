package com.server.handsock.api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.server.handsock.admin.dao.ServerUserDao;
import com.server.handsock.admin.mod.ServerUserModel;
import com.server.handsock.admin.service.ServerSystemService;
import com.server.handsock.api.dao.UploadDao;
import com.server.handsock.api.man.UploadManage;
import com.server.handsock.api.mod.UploadModel;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.console.UtilityService;
import com.server.handsock.services.CacheService;
import com.server.handsock.services.TokenService;
import com.server.handsock.clients.dao.ClientChannelDao;
import com.server.handsock.clients.mod.ClientChannelModel;
import com.server.handsock.clients.service.ClientChannelService;
import com.server.handsock.clients.service.ClientUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UploadService {

    private final UploadDao uploadDao;
    private final TokenService tokenService;
    private final CacheService cacheService;
    private final ServerUserDao serverUserDao;
    private final ClientChannelDao clientChannelDao;
    private final ClientUserService clientUserService;
    private final ClientUserService client_userService;
    private final ServerSystemService API_systemService;
    private final ClientChannelService clientChannelService;

    private final UtilityService utilityService = new UtilityService();
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public UploadService(ServerUserDao serverUserDao, ClientChannelDao clientChannelDao, TokenService tokenService, CacheService cacheService, UploadDao uploadDao, ClientUserService client_userService, ServerSystemService API_systemService, ClientUserService clientUserService, ClientChannelService clientChannelService) {
        this.uploadDao = uploadDao;
        this.cacheService = cacheService;
        this.tokenService = tokenService;
        this.serverUserDao = serverUserDao;
        this.clientChannelDao = clientChannelDao;
        this.API_systemService = API_systemService;
        this.clientUserService = clientUserService;
        this.client_userService = client_userService;
        this.clientChannelService = clientChannelService;
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

        if (Objects.equals(type, "files") && !clientChannelService.getChanOpenStatus(Long.parseLong(gidHeader.get(0)))) {
            return handleResults.handleResultByCode(402, null, "该频道未开启");
        }

        if (Objects.equals(type, "files") && API_systemService.getSystemKeyStatus("taboo") && !client_userService.getUserAdminStatusByUid(Long.parseLong(uidHeader.get(0)))) {
            return handleResults.handleResultByCode(402, null, "全频禁言开启中");
        }

        if (!API_systemService.getSystemKeyStatus("upload") && !client_userService.getUserAdminStatusByUid(Long.parseLong(uidHeader.get(0)))) {
            return handleResults.handleResultByCode(402, null, "上传权限未开放");
        }

        if (Objects.equals(type, "files") && clientUserService.getUserTabooStatus(Long.parseLong(uidHeader.get(0))) && !client_userService.getUserAdminStatusByUid(Long.parseLong(uidHeader.get(0)))) {
            return handleResults.handleResultByCode(402, null, "你正在被禁言中");
        }

        String time = utilityService.encodeStringToMD5(utilityService.formatTime("yyyy-MM-dd-HH-mm-ss"));
        Path uploadDir = createUploadDirectory(type, uidHeader.get(0), time);

        try {
            byte[] bytes = file.getBytes();
            Long uid = Long.parseLong(uidHeader.get(0));
            String fileName = file.getOriginalFilename();
            String filePath = uid + "/" + time + "/" + fileName;
            UploadModel uploadModel = new UploadModel();
            Files.write(uploadDir.resolve(Objects.requireNonNull(fileName)), bytes);
            new UploadManage().insertUploadFile(uploadModel, uid, fileName, filePath, time, type, file.getSize());
            if (uploadDao.insert(uploadModel) > 0) {
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
        ServerUserModel user = serverUserDao.selectOne(new QueryWrapper<ServerUserModel>().eq("uid", uidHeader.get(0)));
        ClientChannelModel group = clientChannelDao.selectOne(new QueryWrapper<ClientChannelModel>().eq("gid", gidHeader.get(0)));
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