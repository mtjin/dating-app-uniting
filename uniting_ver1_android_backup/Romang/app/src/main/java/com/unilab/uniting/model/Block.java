package com.unilab.uniting.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Block implements Serializable {
    private boolean isFacebookBlocked;
    private ArrayList<String> facebookList;
    private String myFacebookUid;
    private boolean isContactBlocked;
    private ArrayList<String> contactList;
    private String myPhoneNumber;
    private boolean isUniversityBlocked;
    private String university;
    private ArrayList<String> blockMeList;
    private ArrayList<String> blockUserList;

    public Block() {
    }

    public Block(boolean isFacebookBlocked, ArrayList<String> facebookList, String myFacebookUid, boolean isContactBlocked, ArrayList<String> contactList, String myPhoneNumber, boolean isUniversityBlocked, String university, ArrayList<String> blockMeList, ArrayList<String> blockUserList) {
        this.isFacebookBlocked = isFacebookBlocked;
        this.facebookList = facebookList;
        this.myFacebookUid = myFacebookUid;
        this.isContactBlocked = isContactBlocked;
        this.contactList = contactList;
        this.myPhoneNumber = myPhoneNumber;
        this.isUniversityBlocked = isUniversityBlocked;
        this.university = university;
        this.blockMeList = blockMeList;
        this.blockUserList = blockUserList;
    }

    public boolean isFacebookBlocked() {
        return isFacebookBlocked;
    }

    public void setFacebookBlocked(boolean facebookBlocked) {
        isFacebookBlocked = facebookBlocked;
    }

    public boolean isUniversityBlocked() {
        return isUniversityBlocked;
    }

    public void setUniversityBlocked(boolean universityBlocked) {
        isUniversityBlocked = universityBlocked;
    }

    public boolean isContactBlocked() {
        return isContactBlocked;
    }

    public void setContactBlocked(boolean contactBlocked) {
        isContactBlocked = contactBlocked;
    }

    public ArrayList<String> getFacebookList() {
        return facebookList;
    }

    public void setFacebookList(ArrayList<String> facebookList) {
        this.facebookList = facebookList;
    }

    public ArrayList<String> getContactList() {
        return contactList;
    }

    public void setContactList(ArrayList<String> contactList) {
        this.contactList = contactList;
    }

    public String getMyFacebookUid() {
        return myFacebookUid;
    }

    public void setMyFacebookUid(String myFacebookUid) {
        this.myFacebookUid = myFacebookUid;
    }

    public String getMyPhoneNumber() {
        return myPhoneNumber;
    }

    public void setMyPhoneNumber(String myPhoneNumber) {
        this.myPhoneNumber = myPhoneNumber;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public ArrayList<String> getBlockMeList() {
        return blockMeList;
    }

    public void setBlockMeList(ArrayList<String> blockMeList) {
        this.blockMeList = blockMeList;
    }

    public ArrayList<String> getBlockUserList() {
        return blockUserList;
    }

    public void setBlockUserList(ArrayList<String> blockUserList) {
        this.blockUserList = blockUserList;
    }
}



