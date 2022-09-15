package com.unilab.uniting.model;

public class ChatMessage {
    private String messageId;
    private String roomId;
    private long timestamp;
    private String date;
    private String message;
    private String senderNickname;
    private String senderUid;
    private String receiverNickname;
    private String receiverUid;
    private String admin;


    public ChatMessage() {
    }


    public ChatMessage(String messageId, String roomId, long timestamp, String date, String message, String senderNickname, String senderUid, String receiverNickname, String receiverUid, String admin) {
        this.messageId = messageId;
        this.roomId = roomId;
        this.timestamp = timestamp;
        this.date = date;
        this.message = message;
        this.senderNickname = senderNickname;
        this.senderUid = senderUid;
        this.receiverNickname = receiverNickname;
        this.receiverUid = receiverUid;
        this.admin = admin;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverNickname() {
        return receiverNickname;
    }

    public void setReceiverNickname(String receiverNickname) {
        this.receiverNickname = receiverNickname;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public static String start = "start";
    public static String end = "end";

}
