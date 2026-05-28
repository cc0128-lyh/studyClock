package com.studyclock.web.controller;

import com.studyclock.common.dto.ApiResult;
import com.studyclock.wallpaper.entity.Wallpaper;
import com.studyclock.wallpaper.service.WallpaperService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallpapers")
public class WallpaperController {

    private final WallpaperService service;

    @Value("${studyclock.wallpaper-dir:./wallpapers}")
    private String wallpaperDir;

    public WallpaperController(WallpaperService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResult<List<Wallpaper>> list() {
        return ApiResult.success(service.listAll());
    }

    @GetMapping("/active")
    public ApiResult<Wallpaper> active() {
        return ApiResult.success(service.getActive());
    }

    @PostMapping
    public ApiResult<Wallpaper> create(@RequestBody Wallpaper wallpaper) {
        return ApiResult.success(service.save(wallpaper));
    }

    @PostMapping("/upload")
    public ApiResult<Wallpaper> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResult.error("File is empty");
        }
        try {
            Path uploadPath = Paths.get(wallpaperDir).toAbsolutePath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = "wp_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            String urlPath = "/wallpapers/" + fileName;

            Wallpaper wallpaper = new Wallpaper();
            wallpaper.setName(originalName != null ? originalName.replace(ext, "") : "壁纸");
            wallpaper.setFilePath(urlPath);
            wallpaper.setType("IMAGE");
            wallpaper.setIsActive(false);

            return ApiResult.success(service.save(wallpaper));
        } catch (IOException e) {
            return ApiResult.error("Upload failed: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/active")
    public ApiResult<Wallpaper> setActive(@PathVariable Long id) {
        return ApiResult.success(service.setActive(id));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResult.success();
    }
}
