package com.studyclock.shortcut.entity;

import com.studyclock.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "shortcuts")
public class Shortcut extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "app_path", length = 500)
    private String appPath;

    @Column(length = 500)
    private String url;

    @Column(length = 500)
    private String icon;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "launch_type", length = 20, nullable = false)
    private String launchType; // APP, URL, COMMAND

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAppPath() { return appPath; }
    public void setAppPath(String appPath) { this.appPath = appPath; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getLaunchType() { return launchType; }
    public void setLaunchType(String launchType) { this.launchType = launchType; }
}
