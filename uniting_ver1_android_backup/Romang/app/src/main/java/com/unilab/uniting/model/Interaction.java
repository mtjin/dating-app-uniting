package com.unilab.uniting.model;

import java.util.ArrayList;

public class Interaction {
    private String interactionId;
    private String uid0;
    private String uid1;
    private ArrayList<String> uidList;
    private double scoreUser0to1; //0점 또는 1점. -1점이면 아직 scoring 안한거
    private double scoreUser1to0;
    private boolean likeUser0to1;
    private boolean likeUser1to0;
    private String likeApplicant;
    private String message;
    private String roomId;
    private boolean blocked;
    private String uidBlock;
    private User user0;
    private User user1;
    private long createTime;
    private long recentTime;

    public Interaction (){

    }


    public Interaction(String interactionId, String uid0, String uid1, ArrayList<String> uidList, double scoreUser0to1, double scoreUser1to0, boolean likeUser0to1, boolean likeUser1to0, String likeApplicant, String message, String roomId, boolean blocked, String uidBlock, User user0, User user1, long createTime, long recentTime) {
        this.interactionId = interactionId;
        this.uid0 = uid0;
        this.uid1 = uid1;
        this.uidList = uidList;
        this.scoreUser0to1 = scoreUser0to1;
        this.scoreUser1to0 = scoreUser1to0;
        this.likeUser0to1 = likeUser0to1;
        this.likeUser1to0 = likeUser1to0;
        this.likeApplicant = likeApplicant;
        this.message = message;
        this.roomId = roomId;
        this.blocked = blocked;
        this.uidBlock = uidBlock;
        this.user0 = user0;
        this.user1 = user1;
        this.createTime = createTime;
        this.recentTime = recentTime;
    }

    public String getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(String interactionId) {
        this.interactionId = interactionId;
    }

    public String getUid0() {
        return uid0;
    }

    public void setUid0(String uid0) {
        this.uid0 = uid0;
    }

    public String getUid1() {
        return uid1;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public ArrayList<String> getUidList() {
        return uidList;
    }

    public void setUidList(ArrayList<String> uidList) {
        this.uidList = uidList;
    }

    public double getScoreUser0to1() {
        return scoreUser0to1;
    }

    public void setScoreUser0to1(double scoreUser0to1) {
        this.scoreUser0to1 = scoreUser0to1;
    }

    public double getScoreUser1to0() {
        return scoreUser1to0;
    }

    public void setScoreUser1to0(double scoreUser1to0) {
        this.scoreUser1to0 = scoreUser1to0;
    }

    public boolean isLikeUser0to1() {
        return likeUser0to1;
    }

    public void setLikeUser0to1(boolean likeUser0to1) {
        this.likeUser0to1 = likeUser0to1;
    }

    public boolean isLikeUser1to0() {
        return likeUser1to0;
    }

    public void setLikeUser1to0(boolean likeUser1to0) {
        this.likeUser1to0 = likeUser1to0;
    }

    public String getLikeApplicant() {
        return likeApplicant;
    }

    public void setLikeApplicant(String likeApplicant) {
        this.likeApplicant = likeApplicant;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getUidBlock() {
        return uidBlock;
    }

    public void setUidBlock(String uidBlock) {
        this.uidBlock = uidBlock;
    }

    public User getUser0() {
        return user0;
    }

    public void setUser0(User user0) {
        this.user0 = user0;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getRecentTime() {
        return recentTime;
    }

    public void setRecentTime(long recentTime) {
        this.recentTime = recentTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public static double PRE_SCORE = -1;
    public static double LOW_SCORE = 0;
    public static double HIGH_SCORE = 1;
}

