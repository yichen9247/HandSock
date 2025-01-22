package com.server.handsock.api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.console.ValidExtension;
import org.springframework.web.multipart.MultipartFile;
import com.server.handsock.api.service.OU_UploadService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/upload")
public class UploadController {

    private final OU_UploadService OUUploadService;
    private final ConsolePrints consolePrints = new ConsolePrints();

    @Autowired
    public UploadController(OU_UploadService OUUploadService) {
        this.OUUploadService = OUUploadService;
    }

    private static final String FILES_DIRECTORY = "upload/files/";
    private static final String AVATAR_DIRECTORY = "upload/avatar/";
    private static final String IMAGES_DIRECTORY = "upload/images/";

    @PostMapping("/file")
    public Map<String, Object> clientUploadFile(HttpEntity<String> entity, @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return new HandleResults().handleResultByCode(400, null, "禁止上传空文件");
        return OUUploadService.clientUploadFile(entity.getHeaders(), file, "files");
    }

    @PostMapping("/avatar")
    public Map<String, Object> clientUploadAvatar(HttpEntity<String> entity, @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return new HandleResults().handleResultByCode(400, null, "禁止上传空文件");
        if (!new ValidExtension().checkImageValidExtension(file)) return new HandleResults().handleResultByCode(400, null, "不支持的图片类型");
        return OUUploadService.clientUploadFile(entity.getHeaders(), file, "avatar");
    }

    @PostMapping("/images")
    public Map<String, Object> clientUploadImages(HttpEntity<String> entity, @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return new HandleResults().handleResultByCode(400, null, "禁止上传空文件");
        if (!new ValidExtension().checkImageValidExtension(file)) return new HandleResults().handleResultByCode(400, null, "不支持的图片类型");
        return OUUploadService.clientUploadFile(entity.getHeaders(), file, "images");
    }

    @GetMapping("/download/file/{uid}/{md5}/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String uid, @PathVariable String md5, @PathVariable String filename) {
        return getResourceResponseEntity(uid, md5, filename, FILES_DIRECTORY);
    }

    private ResponseEntity<Resource> getResourceResponseEntity(@PathVariable String uid, @PathVariable String md5, @PathVariable String filename, String filesDirectory) {
        try {
            Path filePath = Paths.get(filesDirectory).resolve(uid).resolve(md5).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                        .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                        .body(resource);
            } else return ResponseEntity.notFound().build();
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/download/avatar/{uid}/{md5}/{filename:.+}")
    public ResponseEntity<Resource> downloadAvatar(@PathVariable String uid, @PathVariable String md5, @PathVariable String filename) {
        return getResourceResponseEntity(uid, md5, filename, AVATAR_DIRECTORY);
    }

    @GetMapping("/download/images/{uid}/{md5}/{filename:.+}")
    public ResponseEntity<Resource> downloadImages(@PathVariable String uid, @PathVariable String md5, @PathVariable String filename) {
        return getResourceResponseEntity(uid, md5, filename, IMAGES_DIRECTORY);
    }
}
