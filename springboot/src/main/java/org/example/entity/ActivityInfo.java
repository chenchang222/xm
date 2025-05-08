package org.example.entity;

public class ActivityInfo {
    private Integer id;
    private Integer activityId;
    private String activityName;
    private Integer num;
    private Integer volunteerId;
    private String volunteerName;
    private String qq;
    private String status; // 审核状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝
    private String rejectReason; // 拒绝理由
    private Integer signedIn; // 是否签到 0-未签到 1-已签到

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Integer volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public void setVolunteerName(String volunteerName) {
        this.volunteerName = volunteerName;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Integer getSignedIn() {
        return signedIn;
    }

    public void setSignedIn(Integer signedIn) {
        this.signedIn = signedIn;
    }
} 