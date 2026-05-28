package com.studyclock.quote.entity;

import com.studyclock.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "motivational_quotes")
public class MotivationalQuote extends BaseEntity {

    @Column(nullable = false, length = 500)
    private String content;

    @Column(length = 50)
    private String category; // FOCUS, BREAK, STUDY, GENERAL

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
