package com.unilab.uniting.model;

public class DIModel {
    private String di;
    private String uid;
    private String facebookUid;
    private String appleUid;
    private String email;
    private String membership;
    private String inviteCode;
    private long dateOfWithdraw;

    public DIModel() {
    }


    public DIModel(String di, String uid, String facebookUid, String appleUid, String email, String membership, String inviteCode, long dateOfWithdraw) {
        this.di = di;
        this.uid = uid;
        this.facebookUid = facebookUid;
        this.appleUid = appleUid;
        this.email = email;
        this.membership = membership;
        this.inviteCode = inviteCode;
        this.dateOfWithdraw = dateOfWithdraw;
    }

    public String getDi() {
        return di;
    }

    public void setDi(String di) {
        this.di = di;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFacebookUid() {
        return facebookUid;
    }

    public void setFacebookUid(String facebookUid) {
        this.facebookUid = facebookUid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public long getDateOfWithdraw() {
        return dateOfWithdraw;
    }

    public void setDateOfWithdraw(long dateOfWithdraw) {
        this.dateOfWithdraw = dateOfWithdraw;
    }

    public String getAppleUid() {
        return appleUid;
    }

    public void setAppleUid(String appleUid) {
        this.appleUid = appleUid;
    }
}
