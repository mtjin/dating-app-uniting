package com.unilab.uniting.model;

import java.io.Serializable;

public class Nice implements Serializable {
    private String date;
    private String name;
    private String birth;
    private String gender;
    private String phoneNumber;
    private String DI; //이건 NICE쪽에서 보내주는거라 대문자로 하는 수밖에 없음? 이 아니라 내가 바꾸면 될듯?
    private String nationality;

    public Nice(){}

    public Nice(String date, String name, String birth, String gender, String phoneNumber, String DI, String nationality) {
        this.date = date;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.DI = DI;
        this.nationality = nationality;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDI() {
        return DI;
    }

    public void setDI(String DI) {
        this.DI = DI;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}



