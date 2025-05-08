package org.example.entity;

import java.util.Date;

/**
 * 系统公告实体类
 */
public class Announcement {
    private Integer id;
    private String title; // 公告标题
    private String content; // 公告内容
    private String type; // 公告类型（政策变更、重要通知等）
    private Date createTime; // 创建时间
    private Integer creatorId; // 创建者ID
    private String creatorName; // 创建者名称

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
} 