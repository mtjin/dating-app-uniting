package com.unilab.uniting.model;

public class PushNotification {
    private boolean all;
    private boolean sound;
    private boolean todayIntro;
    private boolean receiveLike;
    private boolean checkSendLike;
    private boolean highScore;
    private boolean chatConnect;
    private boolean meeting;
    private boolean communityComment;
    private boolean chatMessage;


    public PushNotification() {
    }

    public PushNotification(boolean all, boolean sound, boolean todayIntro, boolean receiveLike, boolean checkSendLike, boolean highScore, boolean chatConnect, boolean meeting, boolean communityComment, boolean chatMessage) {
        this.all = all;
        this.sound = sound;
        this.todayIntro = todayIntro;
        this.receiveLike = receiveLike;
        this.checkSendLike = checkSendLike;
        this.highScore = highScore;
        this.chatConnect = chatConnect;
        this.meeting = meeting;
        this.communityComment = communityComment;
        this.chatMessage = chatMessage;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isTodayIntro() {
        return todayIntro;
    }

    public void setTodayIntro(boolean todayIntro) {
        this.todayIntro = todayIntro;
    }

    public boolean isReceiveLike() {
        return receiveLike;
    }

    public void setReceiveLike(boolean receiveLike) {
        this.receiveLike = receiveLike;
    }

    public boolean isCheckSendLike() {
        return checkSendLike;
    }

    public void setCheckSendLike(boolean checkSendLike) {
        this.checkSendLike = checkSendLike;
    }

    public boolean isHighScore() {
        return highScore;
    }

    public void setHighScore(boolean highScore) {
        this.highScore = highScore;
    }

    public boolean isChatConnect() {
        return chatConnect;
    }

    public void setChatConnect(boolean chatConnect) {
        this.chatConnect = chatConnect;
    }

    public boolean isMeeting() {
        return meeting;
    }

    public void setMeeting(boolean meeting) {
        this.meeting = meeting;
    }

    public boolean isCommunityComment() {
        return communityComment;
    }

    public void setCommunityComment(boolean communityComment) {
        this.communityComment = communityComment;
    }

    public boolean isChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(boolean chatMessage) {
        this.chatMessage = chatMessage;
    }
}
