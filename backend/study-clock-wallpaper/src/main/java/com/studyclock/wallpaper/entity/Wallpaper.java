package com.studyclock.wallpaper.entity;

import com.studyclock.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "wallpapers")
public class Wallpaper extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "bg_color", length = 20)
    private String bgColor;

    @Column(length = 20, nullable = false)
    private String type; // IMAGE, COLOR

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "sort_order")
    private Integer sortOrder;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getBgColor() { return bgColor; }
    public void setBgColor(String bgColor) { this.bgColor = bgColor; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
