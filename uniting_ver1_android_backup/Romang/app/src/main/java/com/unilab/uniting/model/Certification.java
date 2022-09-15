package com.unilab.uniting.model;

import java.io.Serializable;

public class Certification implements Serializable {
    private String uid;
    private String photoUrl;
    private String fileName;
    private String result;
    private String adminComment;
    private String date;
    private String timeStamp;
    private String university;
    private String major;
    private String officialUniversity;
    private String officialMajor;
    private boolean officialUniversityChecked;
    private boolean officialMajorChecked;
    private String certificationType;
    private String universityEmail;
    private String location;


    public Certification() {
    }

    public Certification(String uid, String photoUrl, String fileName, String result, String adminComment, String date, String timeStamp, String university, String major, String officialUniversity, String officialMajor, boolean officialUniversityChecked, boolean officialMajorChecked, String certificationType, String universityEmail, String location) {
        this.uid = uid;
        this.photoUrl = photoUrl;
        this.fileName = fileName;
        this.result = result;
        this.adminComment = adminComment;
        this.date = date;
        this.timeStamp = timeStamp;
        this.university = university;
        this.major = major;
        this.officialUniversity = officialUniversity;
        this.officialMajor = officialMajor;
        this.officialUniversityChecked = officialUniversityChecked;
        this.officialMajorChecked = officialMajorChecked;
        this.certificationType = certificationType;
        this.universityEmail = universityEmail;
        this.location = location;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getOfficialUniversity() {
        return officialUniversity;
    }

    public void setOfficialUniversity(String officialUniversity) {
        this.officialUniversity = officialUniversity;
    }

    public String getOfficialMajor() {
        return officialMajor;
    }

    public void setOfficialMajor(String officialMajor) {
        this.officialMajor = officialMajor;
    }

    public boolean isOfficialUniversityChecked() {
        return officialUniversityChecked;
    }

    public void setOfficialUniversityChecked(boolean officialUniversityChecked) {
        this.officialUniversityChecked = officialUniversityChecked;
    }

    public boolean isOfficialMajorChecked() {
        return officialMajorChecked;
    }

    public void setOfficialMajorChecked(boolean officialMajorChecked) {
        this.officialMajorChecked = officialMajorChecked;
    }

    public String getCertificationType() {
        return certificationType;
    }

    public void setCertificationType(String certificationType) {
        this.certificationType = certificationType;
    }

    public String getUniversityEmail() {
        return universityEmail;
    }

    public void setUniversityEmail(String universityEmail) {
        this.universityEmail = universityEmail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}



