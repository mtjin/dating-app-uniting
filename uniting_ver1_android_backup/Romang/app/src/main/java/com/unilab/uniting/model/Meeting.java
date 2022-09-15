package com.unilab.uniting.model;

import java.io.Serializable;
import java.util.List;

public class Meeting implements Serializable {
    private String meetingId; //미팅 고유토큰
    private String writer; //글쓴이 1회용닉네임
    private String hostUid; //주최자 고유토큰
    private String hostGender; //주최자 성별
    private String hostAge; //주최자 나이
    private String hostUniversity; //주최자 학교
    private double hostTierPercent;
    private double hostIntroMannerScore;
    private double hostMeetingMannerScore;
    private boolean blurred;
    private String createDate; //작성 시간 2019-06-10
    private long createTimestamp; //작성 시간 포맷 millisecond
    private String title; //미팅 제목
    private String content; //미팅 내용
    private String photoUrl; //사진url
    private List<String> applicantUidList;
    private boolean deleted; // 유저가 삭제한 미팅. true인 경우 제목/내용에 "삭제되었습니다."를 띄운다.
    private boolean expired; //true인 경우 기간(7일)이 지나서 아얘 삭제된 미팅. DB에는 그대로 존재.

    public Meeting() {
    }

    public Meeting(String meetingId, String writer, String hostUid, String hostGender, String hostAge, String hostUniversity, double hostTierPercent, double hostIntroMannerScore, double hostMeetingMannerScore, boolean blurred, String createDate, long createTimestamp, String title, String content, String photoUrl, List<String> applicantUidList, boolean deleted, boolean expired) {
        this.meetingId = meetingId;
        this.writer = writer;
        this.hostUid = hostUid;
        this.hostGender = hostGender;
        this.hostAge = hostAge;
        this.hostUniversity = hostUniversity;
        this.hostTierPercent = hostTierPercent;
        this.hostIntroMannerScore = hostIntroMannerScore;
        this.hostMeetingMannerScore = hostMeetingMannerScore;
        this.blurred = blurred;
        this.createDate = createDate;
        this.createTimestamp = createTimestamp;
        this.title = title;
        this.content = content;
        this.photoUrl = photoUrl;
        this.applicantUidList = applicantUidList;
        this.deleted = deleted;
        this.expired = expired;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getHostUid() {
        return hostUid;
    }

    public void setHostUid(String hostUid) {
        this.hostUid = hostUid;
    }

    public String getHostGender() {
        return hostGender;
    }

    public void setHostGender(String hostGender) {
        this.hostGender = hostGender;
    }

    public String getHostAge() {
        return hostAge;
    }

    public void setHostAge(String hostAge) {
        this.hostAge = hostAge;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    public List<String> getApplicantUidList() {
        return applicantUidList;
    }

    public void setApplicantUidList(List<String> applicantUidList) {
        this.applicantUidList = applicantUidList;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getHostUniversity() {
        return hostUniversity;
    }

    public void setHostUniversity(String hostUniversity) {
        this.hostUniversity = hostUniversity;
    }


    public boolean isBlurred() {
        return blurred;
    }

    public void setBlurred(boolean blurred) {
        this.blurred = blurred;
    }

    public double getHostTierPercent() {
        return hostTierPercent;
    }

    public void setHostTierPercent(double hostTierPercent) {
        this.hostTierPercent = hostTierPercent;
    }

    public double getHostIntroMannerScore() {
        return hostIntroMannerScore;
    }

    public void setHostIntroMannerScore(double hostIntroMannerScore) {
        this.hostIntroMannerScore = hostIntroMannerScore;
    }

    public double getHostMeetingMannerScore() {
        return hostMeetingMannerScore;
    }

    public void setHostMeetingMannerScore(double hostMeetingMannerScore) {
        this.hostMeetingMannerScore = hostMeetingMannerScore;
    }
}



