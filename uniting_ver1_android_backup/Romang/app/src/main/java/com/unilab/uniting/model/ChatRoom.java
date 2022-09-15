package com.unilab.uniting.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatRoom implements Serializable {
    private String roomId;
    private String createDate; //채팅 시작 시간
    private long createTimestamp; //채팅 시작 시간 (millisecond 단위)
    private long recentTimestamp; //최근메세지 온시간
    private String recentMessage;
    private String senderUid;
    private String from; //어디서연결된 건지(미팅 , 등)
    private ArrayList<String> userUidList;
    private User user0;
    private User user1;
    private String interactionId;
    private boolean started;
    private boolean deleted;
    private boolean expired;
    private boolean refunded;
    private boolean voiceTalkOn;
    private String callerUid;
    private long callingTime;
    private String uidPaid;
    private String uidDelete; //먼저 나간 유저 uid
    private String uidBanned; //상대방 나가고 나만 남았을 때. 닫고 나올때 uid 적고 나옴.

    public ChatRoom() {
    }


    public ChatRoom(String roomId, String createDate, long createTimestamp, long recentTimestamp, String recentMessage, String senderUid, String from, ArrayList<String> userUidList, User user0, User user1, String interactionId, boolean started, boolean deleted, boolean expired, boolean refunded, boolean voiceTalkOn, String callerUid, long callingTime, String uidPaid, String uidDelete, String uidBanned) {
        this.roomId = roomId;
        this.createDate = createDate;
        this.createTimestamp = createTimestamp;
        this.recentTimestamp = recentTimestamp;
        this.recentMessage = recentMessage;
        this.senderUid = senderUid;
        this.from = from;
        this.userUidList = userUidList;
        this.user0 = user0;
        this.user1 = user1;
        this.interactionId = interactionId;
        this.started = started;
        this.deleted = deleted;
        this.expired = expired;
        this.refunded = refunded;
        this.voiceTalkOn = voiceTalkOn;
        this.callerUid = callerUid;
        this.callingTime = callingTime;
        this.uidPaid = uidPaid;
        this.uidDelete = uidDelete;
        this.uidBanned = uidBanned;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

    public long getRecentTimestamp() {
        return recentTimestamp;
    }

    public void setRecentTimestamp(long recentTimestamp) {
        this.recentTimestamp = recentTimestamp;
    }

    public String getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(String recentMessage) {
        this.recentMessage = recentMessage;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public ArrayList<String> getUserUidList() {
        return userUidList;
    }

    public void setUserUidList(ArrayList<String> userUidList) {
        this.userUidList = userUidList;
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

    public String getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(String interactionId) {
        this.interactionId = interactionId;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
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

    public boolean isVoiceTalkOn() {
        return voiceTalkOn;
    }

    public void setVoiceTalkOn(boolean voiceTalkOn) {
        this.voiceTalkOn = voiceTalkOn;
    }

    public String getCallerUid() {
        return callerUid;
    }

    public void setCallerUid(String callerUid) {
        this.callerUid = callerUid;
    }

    public long getCallingTime() {
        return callingTime;
    }

    public void setCallingTime(long callingTime) {
        this.callingTime = callingTime;
    }
    

    public String getUidPaid() {
        return uidPaid;
    }

    public void setUidPaid(String uidPaid) {
        this.uidPaid = uidPaid;
    }

    public String getUidDelete() {
        return uidDelete;
    }

    public void setUidDelete(String uidDelete) {
        this.uidDelete = uidDelete;
    }

    public String getUidBanned() {
        return uidBanned;
    }

    public void setUidBanned(String uidBanned) {
        this.uidBanned = uidBanned;
    }

    public boolean isRefunded() {
        return refunded;
    }

    public void setRefunded(boolean refunded) {
        this.refunded = refunded;
    }


}
