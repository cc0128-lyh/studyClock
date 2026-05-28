package com.studyclock.timer.entity;

import com.studyclock.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class Subject extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
