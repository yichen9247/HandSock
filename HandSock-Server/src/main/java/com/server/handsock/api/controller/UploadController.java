package com.server.handsock.api.controller;

import com.server.handsock.api.service.UploadService;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.AppProperties;
import com.server.handsock.console.HandleResults;
import com.server.handsock.console.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private final String crossOriginUrls;
    private final UploadService uploadService;
    private final UtilityService utilityService = new UtilityService();
    private final ConsolePrints consolePrints = new ConsolePrints();
    private static final String[] UPLOAD_DIRECTORIES = { "upload/files/", "upload/avatar/", "upload/images/" };

    @Autowired
    public UploadController(UploadService ouUploadService, AppProperties appProperties) {
        this.uploadService = ouUploadService;
        this.crossOriginUrls = appProperties.getOrigin();
    }

    private String getReferer() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getHeader("Referer");
    }

    private Map<String, Object> uploadFile(HttpEntity<String> entity, MultipartFile file, String fileType) throws IOException {
        String referer = getReferer();
        if (referer == null || !referer.startsWith(crossOriginUrls)) return new HandleResults().handleResultByCode(403, null, "禁止访问");
        if (!utilityService.checkImageValidExtension(file) && ("avatar".equals(fileType) || "images".equals(fileType))) return new HandleResults().handleResultByCode(400, null, "不支持的文件类型");
        if (file.isEmpty()) return new HandleResults().handleResultByCode(400, null, "禁止上传空文件");
        return uploadService.clientUploadFile(entity.getHeaders(), file, fileType);
    }

    @PostMapping("/file")
    public Map<String, Object> clientUploadFile(HttpEntity<String> entity, @RequestParam("file") MultipartFile file) throws IOException {
        return uploadFile(entity, file, "files");
    }

    @PostMapping("/avatar")
    public Map<String, Object> clientUploadAvatar(HttpEntity<String> entity, @RequestParam("file") MultipartFile file) throws IOException {
        return uploadFile(entity, file, "avatar");
    }

    @PostMapping("/images")
    public Map<String, Object> clientUploadImages(HttpEntity<String> entity, @RequestParam("file") MultipartFile file) throws IOException {
        return uploadFile(entity, file, "images");
    }

    private ResponseEntity<Resource> downloadFile(String uid, String md5, String filename, String directory) {
        try {
            Path filePath = Paths.get(directory).resolve(uid).resolve(md5).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                        .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                        .body(resource);
            }
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/download/{type}/{uid}/{md5}/{filename:.+}")
    public ResponseEntity<Resource> download(@PathVariable String type, @PathVariable String uid, @PathVariable String md5, @PathVariable String filename) {
        String referer = getReferer(), directory;
        if (referer == null || !referer.startsWith(crossOriginUrls)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        switch (type) {
            case "file":
                directory = UPLOAD_DIRECTORIES[0];
                break;
            case "avatar":
                directory = UPLOAD_DIRECTORIES[1];
                break;
            case "images":
                directory = UPLOAD_DIRECTORIES[2];
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return downloadFile(uid, md5, filename, directory);
    }
}