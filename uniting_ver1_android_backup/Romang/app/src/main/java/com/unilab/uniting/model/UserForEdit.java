package com.unilab.uniting.model;

import java.io.Serializable;
import java.util.ArrayList;
/*
 *  대학교증명사진 관련은 스토리지에 올리기만하고 모델클래스에는 추가하지않았다.(딱히 필요없을 것 같아서)
 * */


public class UserForEdit implements Serializable {
    private String nickName; //닉네임
    private String location; //지역
    private String height; //키
    private ArrayList<String> personality; //성격
    private String bloodType; //혈액형
    private String smoking; //흡연
    private String drinking; //음주
    private String religion; //종교
    private String selfIntroduction; //자기 소개글
    private String story0; //첫번재 스토리
    private String story1; //두번째 스토리
    private String story2; //세번째 스토리


    public UserForEdit() {
    }

    public UserForEdit(String nickName, String location, String height, ArrayList<String> personality, String bloodType, String smoking, String drinking, String religion, String selfIntroduction, String story0, String story1, String story2) {
        this.nickName = nickName;
        this.location = location;
        this.height = height;
        this.personality = personality;
        this.bloodType = bloodType;
        this.smoking = smoking;
        this.drinking = drinking;
        this.religion = religion;
        this.selfIntroduction = selfIntroduction;
        this.story0 = story0;
        this.story1 = story1;
        this.story2 = story2;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public ArrayList<String> getPersonality() {
        return personality;
    }

    public void setPersonality(ArrayList<String> personality) {
        this.personality = personality;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getSmoking() {
        return smoking;
    }

    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    public String getDrinking() {
        return drinking;
    }

    public void setDrinking(String drinking) {
        this.drinking = drinking;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getSelfIntroduction() {
        return selfIntroduction;
    }

    public void setSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
    }

    public String getStory0() {
        return story0;
    }

    public void setStory0(String story0) {
        this.story0 = story0;
    }

    public String getStory1() {
        return story1;
    }

    public void setStory1(String story1) {
        this.story1 = story1;
    }

    public String getStory2() {
        return story2;
    }

    public void setStory2(String story2) {
        this.story2 = story2;
    }
}
