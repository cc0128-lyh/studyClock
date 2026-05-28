package com.studyclock.wallpaper.service;

import com.studyclock.wallpaper.entity.Wallpaper;
import com.studyclock.wallpaper.repository.WallpaperRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class WallpaperService {

    private final WallpaperRepository repository;

    public WallpaperService(WallpaperRepository repository) {
        this.repository = repository;
    }

    public List<Wallpaper> listAll() {
        return repository.findAllByOrderBySortOrderAsc();
    }

    public Wallpaper getActive() {
        return repository.findByIsActiveTrue().orElse(null);
    }

    public Wallpaper getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallpaper not found: " + id));
    }

    public Wallpaper save(Wallpaper wallpaper) {
        return repository.save(wallpaper);
    }

    public Wallpaper setActive(Long id) {
        repository.findAll().forEach(w -> {
            w.setIsActive(w.getId().equals(id));
            repository.save(w);
        });
        return getById(id);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
