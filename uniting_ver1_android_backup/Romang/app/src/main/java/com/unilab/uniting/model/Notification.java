package com.unilab.uniting.model;

public class Notification {
    private String type;
    private CommunityPost communityPost;
    private Meeting meeting;
    private Invite invite;
    private User user;
    private ChatRoom chatRoom;
    private String date;
    private long unixTime;
    private String nickname;
    private String content;
    private String toUid; //상대 uid
    private boolean check;

    public Notification() {
    }

    public Notification(String type, Object object, String date, long unixTime, String nickname, String content, String toUid, boolean check) {
        this.type = type;
        switch (type){
            case "댓글":
            case "대댓글":
            case "게시글":
                this.communityPost = (CommunityPost) object;
                break;
            case "프로필열람신청"://미팅 Step1 신청
            case "프로필열람수락": //미팅 Step1 수락
            case "미팅": //미팅 Step1 수락
            case "미팅신청": //미팅 Step2 유료 신청
            case "미팅수락"://미팅 Step2 수락
                this.meeting = (Meeting) object;
                break;
            case "좋아요보냄":
            case "좋아요확인":
            case "유저":
                this.user = (User) object;
                break;
            case "초대":
                this.invite = (Invite) object;
                break;
            case "채팅":
            case "채팅연결":
            case "좋아요수락":
                this.chatRoom = (ChatRoom) object;
                break;
        }
        this.date = date;
        this.unixTime = unixTime;
        this.content = content;
        this.nickname = nickname;
        this.toUid = toUid;
        this.check = check;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CommunityPost getCommunityPost() {
        return communityPost;
    }

    public void setCommunityPost(CommunityPost communityPost) {
        this.communityPost = communityPost;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public Invite getInvite() {
        return invite;
    }

    public void setInvite(Invite invite) {
        this.invite = invite;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
