package com.unilab.uniting.model;

public class RefundedDia {
    private int dia;
    private String diaId;
    private String refundedUid;
    private String partnerUid;
    private String meetingId;
    private String roomId;
    private String location;
    private String date;
    private long unixTime;

    public RefundedDia() {
    }

    public RefundedDia(int dia, String diaId, String refundedUid, String partnerUid, String meetingId, String roomId, String location, String date, long unixTime) {
        this.dia = dia;
        this.diaId = diaId;
        this.refundedUid = refundedUid;
        this.partnerUid = partnerUid;
        this.meetingId = meetingId;
        this.roomId = roomId;
        this.location = location;
        this.date = date;
        this.unixTime = unixTime;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public String getDiaId() {
        return diaId;
    }

    public void setDiaId(String diaId) {
        this.diaId = diaId;
    }

    public String getPartnerUid() {
        return partnerUid;
    }

    public void setPartnerUid(String partnerUid) {
        this.partnerUid = partnerUid;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getRefundedUid() {
        return refundedUid;
    }

    public void setRefundedUid(String refundedUid) {
        this.refundedUid = refundedUid;
    }
}
