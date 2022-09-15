package com.unilab.uniting.model;

public class Suggestion {
    private String content;
    private boolean done;
    private String uid;
    private String date;
    private String email;
    private String nickname;

    public Suggestion(String content, boolean done, String uid, String date, String email, String nickname) {
        this.content = content;
        this.done = done;
        this.uid = uid;
        this.date = date;
        this.email = email;
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
