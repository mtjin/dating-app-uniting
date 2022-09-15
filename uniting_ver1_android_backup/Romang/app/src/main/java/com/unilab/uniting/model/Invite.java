package com.unilab.uniting.model;

public class Invite {
    private String hostDI; //어플로 초대한 사람
    private String guestDI; //어플에 초대된 사람 (코드를 사용한 사람)

    public Invite() {
    }


    public Invite(String hostDI, String guestDI) {
        this.hostDI = hostDI;
        this.guestDI = guestDI;
    }

    public String getHostDI() {
        return hostDI;
    }

    public void setHostDI(String hostDI) {
        this.hostDI = hostDI;
    }

    public String getGuestDI() {
        return guestDI;
    }

    public void setGuestDI(String guestDI) {
        this.guestDI = guestDI;
    }
}
