package com.unilab.uniting.model;

import java.util.ArrayList;
import java.util.Map;

public class InteractionHistory {
    private Map<String,Double> sendScoreList;
    private Map<String,Double> receiveScoreList;
    private ArrayList<String> sendHighScoreList;
    private ArrayList<String> sendLowScoreList;
    private ArrayList<String> receiveHighScoreList;
    private ArrayList<String> receiveLowScoreList;
    private ArrayList<String> sendLikeList;
    private ArrayList<String> receiveLikeList;
    private ArrayList<String> acceptLikeList;
    private ArrayList<String> likeAcceptedList;
    private ArrayList<String> startChatList; //TodayIntro 채팅
    private ArrayList<String> chatStartedList;
    private long accumulatedMainTime;
    private long recentMainTime;
    private long recentDormantTime;
    private Map<Long,String> membershipChangeDict;
    private ArrayList<String> meetingStep1ApplyList;
    private ArrayList<String> meetingStep1AppliedList;
    private ArrayList<String> meetingStep1SendPassList;
    private ArrayList<String> meetingStep1ReceivePassList;
    private ArrayList<String> meetingStep1SendFailList;
    private ArrayList<String> meetingStep1ReceiveFailList;
    private ArrayList<String> meetingStep2ApplyList;
    private ArrayList<String> meetingStep2AppliedList;
    private ArrayList<String> meetingStep2SendPassList;
    private ArrayList<String> meetingStep2ReceivePassList;
    private ArrayList<String> meetingStep2SendFailList;
    private ArrayList<String> meetingStep2ReceiveFailList;

    public InteractionHistory() {
    }

    public InteractionHistory(Map<String, Double> sendScoreList, Map<String, Double> receiveScoreList, ArrayList<String> sendHighScoreList, ArrayList<String> sendLowScoreList, ArrayList<String> receiveHighScoreList, ArrayList<String> receiveLowScoreList, ArrayList<String> sendLikeList, ArrayList<String> receiveLikeList, ArrayList<String> acceptLikeList, ArrayList<String> likeAcceptedList, ArrayList<String> startChatList, ArrayList<String> chatStartedList, long accumulatedMainTime, long recentMainTime, long recentDormantTime, Map<Long, String> membershipChangeDict, ArrayList<String> meetingStep1ApplyList, ArrayList<String> meetingStep1AppliedList, ArrayList<String> meetingStep1SendPassList, ArrayList<String> meetingStep1ReceivePassList, ArrayList<String> meetingStep1SendFailList, ArrayList<String> meetingStep1ReceiveFailList, ArrayList<String> meetingStep2ApplyList, ArrayList<String> meetingStep2AppliedList, ArrayList<String> meetingStep2SendPassList, ArrayList<String> meetingStep2ReceivePassList, ArrayList<String> meetingStep2SendFailList, ArrayList<String> meetingStep2ReceiveFailList) {
        this.sendScoreList = sendScoreList;
        this.receiveScoreList = receiveScoreList;
        this.sendHighScoreList = sendHighScoreList;
        this.sendLowScoreList = sendLowScoreList;
        this.receiveHighScoreList = receiveHighScoreList;
        this.receiveLowScoreList = receiveLowScoreList;
        this.sendLikeList = sendLikeList;
        this.receiveLikeList = receiveLikeList;
        this.acceptLikeList = acceptLikeList;
        this.likeAcceptedList = likeAcceptedList;
        this.startChatList = startChatList;
        this.chatStartedList = chatStartedList;
        this.accumulatedMainTime = accumulatedMainTime;
        this.recentMainTime = recentMainTime;
        this.recentDormantTime = recentDormantTime;
        this.membershipChangeDict = membershipChangeDict;
        this.meetingStep1ApplyList = meetingStep1ApplyList;
        this.meetingStep1AppliedList = meetingStep1AppliedList;
        this.meetingStep1SendPassList = meetingStep1SendPassList;
        this.meetingStep1ReceivePassList = meetingStep1ReceivePassList;
        this.meetingStep1SendFailList = meetingStep1SendFailList;
        this.meetingStep1ReceiveFailList = meetingStep1ReceiveFailList;
        this.meetingStep2ApplyList = meetingStep2ApplyList;
        this.meetingStep2AppliedList = meetingStep2AppliedList;
        this.meetingStep2SendPassList = meetingStep2SendPassList;
        this.meetingStep2ReceivePassList = meetingStep2ReceivePassList;
        this.meetingStep2SendFailList = meetingStep2SendFailList;
        this.meetingStep2ReceiveFailList = meetingStep2ReceiveFailList;
    }

    public Map<String, Double> getSendScoreList() {
        return sendScoreList;
    }

    public void setSendScoreList(Map<String, Double> sendScoreList) {
        this.sendScoreList = sendScoreList;
    }

    public Map<String, Double> getReceiveScoreList() {
        return receiveScoreList;
    }

    public void setReceiveScoreList(Map<String, Double> receiveScoreList) {
        this.receiveScoreList = receiveScoreList;
    }

    public ArrayList<String> getSendHighScoreList() {
        return sendHighScoreList;
    }

    public void setSendHighScoreList(ArrayList<String> sendHighScoreList) {
        this.sendHighScoreList = sendHighScoreList;
    }

    public ArrayList<String> getSendLowScoreList() {
        return sendLowScoreList;
    }

    public void setSendLowScoreList(ArrayList<String> sendLowScoreList) {
        this.sendLowScoreList = sendLowScoreList;
    }

    public ArrayList<String> getReceiveHighScoreList() {
        return receiveHighScoreList;
    }

    public void setReceiveHighScoreList(ArrayList<String> receiveHighScoreList) {
        this.receiveHighScoreList = receiveHighScoreList;
    }

    public ArrayList<String> getReceiveLowScoreList() {
        return receiveLowScoreList;
    }

    public void setReceiveLowScoreList(ArrayList<String> receiveLowScoreList) {
        this.receiveLowScoreList = receiveLowScoreList;
    }

    public ArrayList<String> getSendLikeList() {
        return sendLikeList;
    }

    public void setSendLikeList(ArrayList<String> sendLikeList) {
        this.sendLikeList = sendLikeList;
    }

    public ArrayList<String> getReceiveLikeList() {
        return receiveLikeList;
    }

    public void setReceiveLikeList(ArrayList<String> receiveLikeList) {
        this.receiveLikeList = receiveLikeList;
    }

    public ArrayList<String> getAcceptLikeList() {
        return acceptLikeList;
    }

    public void setAcceptLikeList(ArrayList<String> acceptLikeList) {
        this.acceptLikeList = acceptLikeList;
    }

    public ArrayList<String> getLikeAcceptedList() {
        return likeAcceptedList;
    }

    public void setLikeAcceptedList(ArrayList<String> likeAcceptedList) {
        this.likeAcceptedList = likeAcceptedList;
    }

    public ArrayList<String> getStartChatList() {
        return startChatList;
    }

    public void setStartChatList(ArrayList<String> startChatList) {
        this.startChatList = startChatList;
    }

    public ArrayList<String> getChatStartedList() {
        return chatStartedList;
    }

    public void setChatStartedList(ArrayList<String> chatStartedList) {
        this.chatStartedList = chatStartedList;
    }

    public long getAccumulatedMainTime() {
        return accumulatedMainTime;
    }

    public void setAccumulatedMainTime(long accumulatedMainTime) {
        this.accumulatedMainTime = accumulatedMainTime;
    }

    public long getRecentMainTime() {
        return recentMainTime;
    }

    public void setRecentMainTime(long recentMainTime) {
        this.recentMainTime = recentMainTime;
    }

    public long getRecentDormantTime() {
        return recentDormantTime;
    }

    public void setRecentDormantTime(long recentDormantTime) {
        this.recentDormantTime = recentDormantTime;
    }

    public Map<Long, String> getMembershipChangeDict() {
        return membershipChangeDict;
    }

    public void setMembershipChangeDict(Map<Long, String> membershipChangeDict) {
        this.membershipChangeDict = membershipChangeDict;
    }

    public ArrayList<String> getMeetingStep1ApplyList() {
        return meetingStep1ApplyList;
    }

    public void setMeetingStep1ApplyList(ArrayList<String> meetingStep1ApplyList) {
        this.meetingStep1ApplyList = meetingStep1ApplyList;
    }

    public ArrayList<String> getMeetingStep1AppliedList() {
        return meetingStep1AppliedList;
    }

    public void setMeetingStep1AppliedList(ArrayList<String> meetingStep1AppliedList) {
        this.meetingStep1AppliedList = meetingStep1AppliedList;
    }

    public ArrayList<String> getMeetingStep1SendPassList() {
        return meetingStep1SendPassList;
    }

    public void setMeetingStep1SendPassList(ArrayList<String> meetingStep1SendPassList) {
        this.meetingStep1SendPassList = meetingStep1SendPassList;
    }

    public ArrayList<String> getMeetingStep1ReceivePassList() {
        return meetingStep1ReceivePassList;
    }

    public void setMeetingStep1ReceivePassList(ArrayList<String> meetingStep1ReceivePassList) {
        this.meetingStep1ReceivePassList = meetingStep1ReceivePassList;
    }

    public ArrayList<String> getMeetingStep1SendFailList() {
        return meetingStep1SendFailList;
    }

    public void setMeetingStep1SendFailList(ArrayList<String> meetingStep1SendFailList) {
        this.meetingStep1SendFailList = meetingStep1SendFailList;
    }

    public ArrayList<String> getMeetingStep1ReceiveFailList() {
        return meetingStep1ReceiveFailList;
    }

    public void setMeetingStep1ReceiveFailList(ArrayList<String> meetingStep1ReceiveFailList) {
        this.meetingStep1ReceiveFailList = meetingStep1ReceiveFailList;
    }

    public ArrayList<String> getMeetingStep2ApplyList() {
        return meetingStep2ApplyList;
    }

    public void setMeetingStep2ApplyList(ArrayList<String> meetingStep2ApplyList) {
        this.meetingStep2ApplyList = meetingStep2ApplyList;
    }

    public ArrayList<String> getMeetingStep2AppliedList() {
        return meetingStep2AppliedList;
    }

    public void setMeetingStep2AppliedList(ArrayList<String> meetingStep2AppliedList) {
        this.meetingStep2AppliedList = meetingStep2AppliedList;
    }

    public ArrayList<String> getMeetingStep2SendPassList() {
        return meetingStep2SendPassList;
    }

    public void setMeetingStep2SendPassList(ArrayList<String> meetingStep2SendPassList) {
        this.meetingStep2SendPassList = meetingStep2SendPassList;
    }

    public ArrayList<String> getMeetingStep2ReceivePassList() {
        return meetingStep2ReceivePassList;
    }

    public void setMeetingStep2ReceivePassList(ArrayList<String> meetingStep2ReceivePassList) {
        this.meetingStep2ReceivePassList = meetingStep2ReceivePassList;
    }

    public ArrayList<String> getMeetingStep2SendFailList() {
        return meetingStep2SendFailList;
    }

    public void setMeetingStep2SendFailList(ArrayList<String> meetingStep2SendFailList) {
        this.meetingStep2SendFailList = meetingStep2SendFailList;
    }

    public ArrayList<String> getMeetingStep2ReceiveFailList() {
        return meetingStep2ReceiveFailList;
    }

    public void setMeetingStep2ReceiveFailList(ArrayList<String> meetingStep2ReceiveFailList) {
        this.meetingStep2ReceiveFailList = meetingStep2ReceiveFailList;
    }
}
