package com.studyclock.web.controller;

import com.studyclock.common.dto.ApiResult;
import com.studyclock.settings.service.AppSettingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final AppSettingService service;
    private static final String UPLOAD_DIR = "uploads/avatars/";

    public SettingsController(AppSettingService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResult<Map<String, String>> getAll() {
        return ApiResult.success(service.getAll());
    }

    @GetMapping("/{key}")
    public ApiResult<String> get(@PathVariable String key) {
        return ApiResult.success(service.get(key));
    }

    @PutMapping("/{key}")
    public ApiResult<Void> set(@PathVariable String key, @RequestBody String value) {
        service.set(key, value);
        return ApiResult.success();
    }

    @DeleteMapping("/{key}")
    public ApiResult<Void> delete(@PathVariable String key) {
        service.delete(key);
        return ApiResult.success();
    }

    @PostMapping("/upload/avatar")
    public ApiResult<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResult.error("File is empty");
        }
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = "avatar_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            String urlPath = "/uploads/avatars/" + fileName;
            service.set("userAvatar", urlPath);

            return ApiResult.success(urlPath);
        } catch (IOException e) {
            return ApiResult.error("Upload failed: " + e.getMessage());
        }
    }
}
