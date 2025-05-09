package org.example.entity;

import java.util.Date;

public class ActivityReviewReply {
    private Integer id;
    private Integer reviewId;      // 评价ID
    private String content;        // 回复内容
    private Integer replyerId;     // 回复者ID
    private String replyerName;    // 回复者姓名
    private Date createTime;       // 创建时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getReplyerId() {
        return replyerId;
    }

    public void setReplyerId(Integer replyerId) {
        this.replyerId = replyerId;
    }

    public String getReplyerName() {
        return replyerName;
    }

    public void setReplyerName(String replyerName) {
        this.replyerName = replyerName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
} 