package com.studyclock.web.controller;

import com.studyclock.common.dto.ApiResult;
import com.studyclock.quote.repository.MotivationalQuoteRepository;
import com.studyclock.settings.repository.AppSettingRepository;
import com.studyclock.shortcut.repository.ShortcutRepository;
import com.studyclock.timer.repository.FocusSessionRepository;
import com.studyclock.timer.repository.SubjectRepository;
import com.studyclock.wallpaper.repository.WallpaperRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final FocusSessionRepository focusSessionRepository;
    private final SubjectRepository subjectRepository;
    private final ShortcutRepository shortcutRepository;
    private final AppSettingRepository appSettingRepository;
    private final WallpaperRepository wallpaperRepository;
    private final MotivationalQuoteRepository motivationalQuoteRepository;

    public DataController(FocusSessionRepository focusSessionRepository,
                          SubjectRepository subjectRepository,
                          ShortcutRepository shortcutRepository,
                          AppSettingRepository appSettingRepository,
                          WallpaperRepository wallpaperRepository,
                          MotivationalQuoteRepository motivationalQuoteRepository) {
        this.focusSessionRepository = focusSessionRepository;
        this.subjectRepository = subjectRepository;
        this.shortcutRepository = shortcutRepository;
        this.appSettingRepository = appSettingRepository;
        this.wallpaperRepository = wallpaperRepository;
        this.motivationalQuoteRepository = motivationalQuoteRepository;
    }

    @DeleteMapping("/clear-all")
    @Transactional
    public ApiResult<Void> clearAll() {
        focusSessionRepository.deleteAll();
        subjectRepository.deleteAll();
        shortcutRepository.deleteAll();
        appSettingRepository.deleteAll();
        wallpaperRepository.deleteAll();
        motivationalQuoteRepository.deleteAll();
        return ApiResult.success();
    }
}
