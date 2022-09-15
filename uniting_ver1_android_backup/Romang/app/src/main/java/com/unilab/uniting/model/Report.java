package com.unilab.uniting.model;

public class Report {
    private String reportId; //신고 고유 Id
    private String reporterUid; //신고한 사람 uid
    private String reportedUid; //신고 당한 사람 uid
    private String reportLocation; //신고 위치 (미팅, 채팅방, 게시물, 댓글, 프로필)
    private String reportObjectId; //신고 대상(미팅, 채팅방, 게시물, 댓글, 프로필) Id (프로필일 경우 reportedUid와 같음)
    private String reportObject2Id; //신고 대상이 댓글인 경우, post의 Id
    private String date; //신고 시간
    private String reportType; //신고 유형
    private String content; //신고 내용
    private String photoUrl; //신고첨부사진
    private boolean done;

    public Report() {
    }

    public Report(String reportId, String reporterUid, String reportedUid, String reportLocation, String reportObjectId, String reportObject2Id, String date, String reportType, String content, String photoUrl, boolean done) {
        this.reportId = reportId;
        this.reporterUid = reporterUid;
        this.reportedUid = reportedUid;
        this.reportLocation = reportLocation;
        this.reportObjectId = reportObjectId;
        this.reportObject2Id = reportObject2Id;
        this.date = date;
        this.reportType = reportType;
        this.content = content;
        this.photoUrl = photoUrl;
        this.done = done;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReporterUid() {
        return reporterUid;
    }

    public void setReporterUid(String reporterUid) {
        this.reporterUid = reporterUid;
    }

    public String getReportedUid() {
        return reportedUid;
    }

    public void setReportedUid(String reportedUid) {
        this.reportedUid = reportedUid;
    }

    public String getReportLocation() {
        return reportLocation;
    }

    public void setReportLocation(String reportLocation) {
        this.reportLocation = reportLocation;
    }

    public String getReportObjectId() {
        return reportObjectId;
    }

    public void setReportObjectId(String reportObjectId) {
        this.reportObjectId = reportObjectId;
    }

    public String getReportObject2Id() {
        return reportObject2Id;
    }

    public void setReportObject2Id(String reportObject2Id) {
        this.reportObject2Id = reportObject2Id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
