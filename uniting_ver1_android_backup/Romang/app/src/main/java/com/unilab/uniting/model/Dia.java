package com.unilab.uniting.model;

import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.Strings;

public class Dia {
    private int lastDia; //직전 하트 개수
    private int usedDia; //해당 행동에서 사용한 하트 개수
    private int accumulateUsedDia; //누적사용하트개수
    private int purchasedDia; //구매 하트 개수
    private int accumulatePurchasedDia; // 누적 구매 하트 개수
    private int refundedDia;
    private int accumulateRefundedDia;
    private int adminDia; //운영자 또는 이벤트에 의한 추가 하트
    private int accumulateAdminDia; // 누적 운영자 및 이벤트 하트 개수
    private int currentDia; //사용하고 남은 현재 하트 개수
    private String date; //하트가 소모(충전)된 해당일
    private long unixTime;
    private String diaId; //하트 사용내역 고유 아이디
    private String location; //하트가 사용된 위치 : IntroLike, MeetingS1Apply, Chatting, Purchase, SignUp 으로 구분
    private String interactionId;
    private String partnerUid; //하트를 사용한 상대 uid
    private String meetingId; //하트를 사용한 미팅 uid (미팅이 아닐 경우 빈칸 처리?)
    private String roomId; //채팅방 아이
    private int usedFreeCloseUser;
    private long recentFreeCloseUserTime;
    private int usedFreeTier;
    private long recentFreeTierTime;
    private int usedFreeLike;
    private long recentFreeLikeTime; //만약 unixTime과 같으면 사용된거
    private int usedFreeChat;
    private long recentFreeChatTime;
    private int usedRefundedChat;
    private int currentRefundedChat;


    public Dia() {
    }


    public Dia(int lastDia, int usedDia, int accumulateUsedDia, int purchasedDia, int accumulatePurchasedDia, int refundedDia, int accumulateRefundedDia, int adminDia, int accumulateAdminDia, int currentDia, String date, long unixTime, String diaId, String location, String interactionId, String partnerUid, String meetingId, String roomId, int usedFreeCloseUser, long recentFreeCloseUserTime, int usedFreeTier, long recentFreeTierTime, int usedFreeLike, long recentFreeLikeTime, int usedFreeChat, long recentFreeChatTime, int usedRefundedChat, int currentRefundedChat) {
        this.lastDia = lastDia;
        this.usedDia = usedDia;
        this.accumulateUsedDia = accumulateUsedDia;
        this.purchasedDia = purchasedDia;
        this.accumulatePurchasedDia = accumulatePurchasedDia;
        this.refundedDia = refundedDia;
        this.accumulateRefundedDia = accumulateRefundedDia;
        this.adminDia = adminDia;
        this.accumulateAdminDia = accumulateAdminDia;
        this.currentDia = currentDia;
        this.date = date;
        this.unixTime = unixTime;
        this.diaId = diaId;
        this.location = location;
        this.interactionId = interactionId;
        this.partnerUid = partnerUid;
        this.meetingId = meetingId;
        this.roomId = roomId;
        this.usedFreeCloseUser = usedFreeCloseUser;
        this.recentFreeCloseUserTime = recentFreeCloseUserTime;
        this.usedFreeTier = usedFreeTier;
        this.recentFreeTierTime = recentFreeTierTime;
        this.usedFreeLike = usedFreeLike;
        this.recentFreeLikeTime = recentFreeLikeTime;
        this.usedFreeChat = usedFreeChat;
        this.recentFreeChatTime = recentFreeChatTime;
        this.usedRefundedChat = usedRefundedChat;
        this.currentRefundedChat = currentRefundedChat;
    }


    public Dia getUpdatedDia(int usedDia, int purchasedDia, int refundedDia, int adminDia, String location, String interactionId, String partnerUid, String meetingId, String roomId) {
        //하트 변동 내역 계산
        int lastDia = this.currentDia;
        int accumulateUsedDia = this.accumulateUsedDia + usedDia;
        int accumulatePurchasedDia = this.accumulatePurchasedDia + purchasedDia;
        int accumulateRefundedDia = this.accumulateRefundedDia + refundedDia;
        int accumulateAdminDia = this.accumulateAdminDia + adminDia;
        int currentDia = lastDia - usedDia + purchasedDia + refundedDia + adminDia;
        String date = DateUtil.getDateSec();
        long unixTime = DateUtil.getUnixTimeLong();
        String diaId = DateUtil.getTimeStampUnix();
        Dia updatedDia = new Dia(lastDia, usedDia, accumulateUsedDia, purchasedDia, accumulatePurchasedDia, refundedDia, accumulateRefundedDia, adminDia, accumulateAdminDia, currentDia, date, unixTime, diaId, location, interactionId, partnerUid, meetingId, roomId,
                0, this.recentFreeCloseUserTime,
                0, this.recentFreeTierTime,
                0, this.recentFreeLikeTime,
                0, this.recentFreeChatTime,
                0, this.currentRefundedChat);
        return updatedDia;
    }

    public Dia useFreeCloseUser() {
        String date = DateUtil.getDateSec();
        long unixTime = DateUtil.getUnixTimeLong();
        String diaId = DateUtil.getTimeStampUnix();
        Dia updatedDia = new Dia(
                this.currentDia,
                0,
                this.accumulateUsedDia,
                0,
                this.accumulatePurchasedDia,
                0,
                this.accumulateRefundedDia,
                0,
                this.accumulateAdminDia,
                this.currentDia,
                date,
                unixTime,
                diaId,
                Strings.freeCloseUser,
                "",
                "",
                "",
                "",
                1,
                unixTime,
                0,
                this.recentFreeTierTime,
                0,
                this.recentFreeLikeTime,
                0,
                this.recentFreeChatTime,
                0,
                this.currentRefundedChat
        );

        return updatedDia;
    }

    public Dia useFreeTier() {
        String date = DateUtil.getDateSec();
        long unixTime = DateUtil.getUnixTimeLong();
        String diaId = DateUtil.getTimeStampUnix();
        Dia updatedDia = new Dia(
                this.currentDia,
                0,
                this.accumulateUsedDia,
                0,
                this.accumulatePurchasedDia,
                0,
                this.accumulateRefundedDia,
                0,
                this.accumulateAdminDia,
                this.currentDia,
                date,
                unixTime,
                diaId,
                Strings.freeCloseUser,
                "",
                "",
                "",
                "",
                0,
                this.recentFreeCloseUserTime,
                1,
                unixTime,
                0,
                this.recentFreeLikeTime,
                0,
                this.recentFreeChatTime,
                0,
                this.currentRefundedChat
        );

        return updatedDia;
    }

    public Dia useFreeLike(String interactionId, String partnerUid) {
        String date = DateUtil.getDateSec();
        long unixTime = DateUtil.getUnixTimeLong();
        String diaId = DateUtil.getTimeStampUnix();
        Dia updatedDia = new Dia(
                this.currentDia,
                0,
                this.accumulateUsedDia,
                0,
                this.accumulatePurchasedDia,
                0,
                this.accumulateRefundedDia,
                0,
                this.accumulateAdminDia,
                this.currentDia,
                date,
                unixTime,
                diaId,
                Strings.freeLike,
                interactionId,
                partnerUid,
                "",
                "",
                0,
                this.recentFreeCloseUserTime,
                0,
                this.recentFreeTierTime,
                1,
                unixTime,
                0,
                this.recentFreeChatTime,
                0,
                this.currentRefundedChat
        );
        return updatedDia;
    }

    public Dia useFreeChat(String interactionId, String partnerUid, String roomId) {
        String date = DateUtil.getDateSec();
        long unixTime = DateUtil.getUnixTimeLong();
        String diaId = DateUtil.getTimeStampUnix();
        Dia updatedDia = new Dia(
                this.currentDia,
                0,
                this.accumulateUsedDia,
                0,
                this.accumulatePurchasedDia,
                0,
                this.accumulateRefundedDia,
                0,
                this.accumulateAdminDia,
                this.currentDia,
                date,
                unixTime,
                diaId,
                Strings.freeChat,
                interactionId,
                partnerUid,
                "",
                roomId,
                0,
                this.recentFreeCloseUserTime,
                0,
                this.recentFreeTierTime,
                0,
                this.recentFreeLikeTime,
                1,
                unixTime,
                0,
                this.currentRefundedChat
        );
        return updatedDia;
    }

    public Dia useRefundedFreeChat(String interactionId, String partnerUid, String roomId) {
        String date = DateUtil.getDateSec();
        long unixTime = DateUtil.getUnixTimeLong();
        String diaId = DateUtil.getTimeStampUnix();
        Dia updatedDia = new Dia(
                this.currentDia,
                0,
                this.accumulateUsedDia,
                0,
                this.accumulatePurchasedDia,
                0,
                this.accumulateRefundedDia,
                0,
                this.accumulateAdminDia,
                this.currentDia,
                date,
                unixTime,
                diaId,
                Strings.useRefundedFreeChat,
                interactionId,
                partnerUid,
                "",
                roomId,
                0,
                this.recentFreeCloseUserTime,
                0,
                this.recentFreeTierTime,
                0,
                this.recentFreeLikeTime,
                0,
                this.recentFreeChatTime,
                1,
                this.currentRefundedChat - 1
        );
        return updatedDia;
    }

    public int getLastDia() {
        return lastDia;
    }

    public void setLastDia(int lastDia) {
        this.lastDia = lastDia;
    }

    public int getUsedDia() {
        return usedDia;
    }

    public void setUsedDia(int usedDia) {
        this.usedDia = usedDia;
    }

    public int getAccumulateUsedDia() {
        return accumulateUsedDia;
    }

    public void setAccumulateUsedDia(int accumulateUsedDia) {
        this.accumulateUsedDia = accumulateUsedDia;
    }

    public int getPurchasedDia() {
        return purchasedDia;
    }

    public void setPurchasedDia(int purchasedDia) {
        this.purchasedDia = purchasedDia;
    }

    public int getRefundedDia() {
        return refundedDia;
    }

    public void setRefundedDia(int refundedDia) {
        this.refundedDia = refundedDia;
    }

    public int getAccumulateRefundedDia() {
        return accumulateRefundedDia;
    }

    public void setAccumulateRefundedDia(int accumulateRefundedDia) {
        this.accumulateRefundedDia = accumulateRefundedDia;
    }

    public int getAccumulatePurchasedDia() {
        return accumulatePurchasedDia;
    }

    public void setAccumulatePurchasedDia(int accumulatePurchasedDia) {
        this.accumulatePurchasedDia = accumulatePurchasedDia;
    }

    public int getCurrentDia() {
        return currentDia;
    }

    public void setCurrentDia(int currentDia) {
        this.currentDia = currentDia;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAdminDia() {
        return adminDia;
    }

    public void setAdminDia(int adminDia) {
        this.adminDia = adminDia;
    }

    public int getAccumulateAdminDia() {
        return accumulateAdminDia;
    }

    public void setAccumulateAdminDia(int accumulateAdminDia) {
        this.accumulateAdminDia = accumulateAdminDia;
    }

    public String getDiaId() {
        return diaId;
    }

    public void setDiaId(String diaId) {
        this.diaId = diaId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPartnerUid() {
        return partnerUid;
    }

    public void setPartnerUid(String partnerUid) {
        this.partnerUid = partnerUid;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
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

    public int getUsedFreeCloseUser() {
        return usedFreeCloseUser;
    }

    public void setUsedFreeCloseUser(int usedFreeCloseUser) {
        this.usedFreeCloseUser = usedFreeCloseUser;
    }

    public long getRecentFreeCloseUserTime() {
        return recentFreeCloseUserTime;
    }

    public void setRecentFreeCloseUserTime(long recentFreeCloseUserTime) {
        this.recentFreeCloseUserTime = recentFreeCloseUserTime;
    }

    public int getUsedFreeLike() {
        return usedFreeLike;
    }

    public void setUsedFreeLike(int usedFreeLike) {
        this.usedFreeLike = usedFreeLike;
    }

    public long getRecentFreeLikeTime() {
        return recentFreeLikeTime;
    }

    public void setRecentFreeLikeTime(long recentFreeLikeTime) {
        this.recentFreeLikeTime = recentFreeLikeTime;
    }

    public int getUsedFreeChat() {
        return usedFreeChat;
    }

    public void setUsedFreeChat(int usedFreeChat) {
        this.usedFreeChat = usedFreeChat;
    }


    public long getRecentFreeChatTime() {
        return recentFreeChatTime;
    }

    public void setRecentFreeChatTime(long recentFreeChatTime) {
        this.recentFreeChatTime = recentFreeChatTime;
    }

    public int getUsedRefundedChat() {
        return usedRefundedChat;
    }

    public void setUsedRefundedChat(int usedRefundedChat) {
        this.usedRefundedChat = usedRefundedChat;
    }

    public int getCurrentRefundedChat() {
        return currentRefundedChat;
    }

    public void setCurrentRefundedChat(int currentRefundedChat) {
        this.currentRefundedChat = currentRefundedChat;
    }

    public String getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(String interactionId) {
        this.interactionId = interactionId;
    }

    public int getUsedFreeTier() {
        return usedFreeTier;
    }

    public void setUsedFreeTier(int usedFreeTier) {
        this.usedFreeTier = usedFreeTier;
    }

    public long getRecentFreeTierTime() {
        return recentFreeTierTime;
    }

    public void setRecentFreeTierTime(long recentFreeTierTime) {
        this.recentFreeTierTime = recentFreeTierTime;
    }
}
