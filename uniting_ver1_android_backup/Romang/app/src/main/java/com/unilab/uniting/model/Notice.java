package com.unilab.uniting.model;

public class Notice {
    private String title;
    private String content;
    private String date;
    private long unixTime;

    public Notice(){}

    public Notice(String title, String content, String date, long unixTime) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.unixTime = unixTime;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }
}
