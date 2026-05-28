package com.studyclock.web.controller;

import com.studyclock.common.dto.ApiResult;
import com.studyclock.wallpaper.entity.Wallpaper;
import com.studyclock.wallpaper.service.WallpaperService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/wallpapers")
public class WallpaperController {

    private final WallpaperService service;

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
