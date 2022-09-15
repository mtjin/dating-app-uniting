package com.unilab.uniting.model;

import java.io.Serializable;
/*
 *  대학교증명사진 관련은 스토리지에 올리기만하고 모델클래스에는 추가하지않았다.(딱히 필요없을 것 같아서)
 * */


public class UserAdmin implements Serializable {
    private String uid; //고유토큰
    private String di;
    private String facebookUid;
    private String email; //이메일(아이디)
    private String password; //패스워드
    private String membership;
    private String dateOfSignUp; //가입 심사 시작일
    private String name; //실제이름
    private String gender; //성별
    private String phoneNumber; //전화번호
    private String birth; //생년월일
    private String nickname; //닉네임
    private String officialUniversity; //학교
    private String officialMajor; //전공
    private String signUpProgress;

    public UserAdmin() {
    }

    public UserAdmin(String uid, String di, String facebookUid, String email, String password, String membership, String dateOfSignUp, String name, String gender, String phoneNumber, String birth, String nickname, String officialUniversity, String officialMajor, String signUpProgress) {
        this.uid = uid;
        this.di = di;
        this.facebookUid = facebookUid;
        this.email = email;
        this.password = password;
        this.membership = membership;
        this.dateOfSignUp = dateOfSignUp;
        this.name = name;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.birth = birth;
        this.nickname = nickname;
        this.officialUniversity = officialUniversity;
        this.officialMajor = officialMajor;
        this.signUpProgress = signUpProgress;
    }

    public String getSignUpProgress() {
        return signUpProgress;
    }

    public void setSignUpProgress(String signUpProgress) {
        this.signUpProgress = signUpProgress;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDi() {
        return di;
    }

    public void setDi(String di) {
        this.di = di;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public String getDateOfSignUp() {
        return dateOfSignUp;
    }

    public void setDateOfSignUp(String dateOfSignUp) {
        this.dateOfSignUp = dateOfSignUp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getOfficialUniversity() {
        return officialUniversity;
    }

    public void setOfficialUniversity(String officialUniversity) {
        this.officialUniversity = officialUniversity;
    }

    public String getOfficialMajor() {
        return officialMajor;
    }

    public void setOfficialMajor(String officialMajor) {
        this.officialMajor = officialMajor;
    }

}
