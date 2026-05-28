package com.studyclock.wallpaper.repository;

import com.studyclock.wallpaper.entity.Wallpaper;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WallpaperRepository extends JpaRepository<Wallpaper, Long> {

    List<Wallpaper> findAllByOrderBySortOrderAsc();

    Optional<Wallpaper> findByIsActiveTrue();

    List<Wallpaper> findByTypeOrderBySortOrderAsc(String type);
}
