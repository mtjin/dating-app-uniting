package com.unilab.uniting.model;

import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;

import java.io.Serializable;
import java.util.ArrayList;
/*
 *  대학교증명사진 관련은 스토리지에 올리기만하고 모델클래스에는 추가하지않았다.(딱히 필요없을 것 같아서)
 * */


public class User implements Serializable {
    private String uid = ""; //고유토큰
    private String di = ""; //주민등록번호 기반 암호화 고유번호
    private String nationality = ""; //국적
    private String email = ""; //이메일(아이디)
    private String membership = ""; //인증단계 - 이메일 가입 직 후 값: 0, 학생증 업로드 후 가입승인 대기중 값 : 1, 운영진이 가입 최종 승인 후 값: 2
    private String dateOfSignUp = ""; //가입 심사 시작일
    private String gender  = ""; //성별
    private int birthYear = 2020;//생년월일
    private String age = ""; //나이
    private String nickname  = "";//닉네임
    private String university = ""; //학교
    private String major = ""; //전공
    private String location = ""; //지역
    private MyGeoPoint geoPoint = FirebaseHelper.seoulMyGeoPoint;
    private String geoHash = "";
    private boolean geoPermitted = false;
    private String height = ""; //키
    private ArrayList<String> personality = new ArrayList<>(); //성격
    private String bloodType = ""; //혈액형
    private String smoking = ""; //흡연
    private String drinking = ""; //음주
    private String religion = ""; //종교
    private ArrayList<String> photoUrl = new ArrayList<>(); //스토리지 사진 url
    private String selfIntroduction = ""; //자기 소개글
    private String story0 = ""; //첫번재 스토리
    private String story1 = ""; //두번째 스토리
    private String story2 = ""; //세번째 스토리
    private String inviteCode = ""; //초대 코드
    private String facebookUid = "";
    private String appleUid = "";
    private String appsflyerUid = "";
    private String officialUniversity = ""; //운영자가 학생증 보고 직접 입력한 공식 학교명 ( 추후 객관식 형태로 고르게 만드는게 나을지도?)
    private String officialMajor = ""; //운영자가 학생증 보고 직접 입력한 공식 전공
    private boolean officialUniversityChecked = false;
    private boolean officialMajorChecked = false;
    private boolean officialUniversityPublic = true;
    private boolean officialMajorPublic = false;
    private boolean officialInfoPublic = false;
    private String certificationType = "";
    private String fcmToken = "";
    private String device = "";
    private String signUpProgress = "";
    private String stageTodayIntro = "";
    private String stageCloseUser = "";
    private String stageEvaluation = "";
    private String stageMeeting = "";
    private String stageMeetingApplicant = "";
    private String stageMeetingHost = "";
    private String stageCommunity = "";
    private String stageTutorial = "";
    private String stageGuide = "";
    private String stageGuide2 = "";
    private double averageOfReceiveScore = 0.5;
    private double averageOfSendScore = 0.5;
    private int sizeOfReceiveScore = 0;
    private int sizeOfSendScore = 0;
    private double tierPercent = 0.5;
    private double tierRecent = 0.5;
    private int tierRecentCount = 0;
    private double screeningScore = 0.5;
    private int screeningScoreCount = 0;
    private double introMannerScore = 0.5;
    private double meetingMannerScore = 0.5;
    private long activationTime = 0;
    private boolean appPushOn = false;

    public User() {
    }

    public User(String uid, String di, String nationality, String email, String membership, String dateOfSignUp, String gender, int birthYear, String age, String nickname, String university, String major, String location, MyGeoPoint geoPoint, String geoHash, boolean geoPermitted, String height, ArrayList<String> personality, String bloodType, String smoking, String drinking, String religion, ArrayList<String> photoUrl, String selfIntroduction, String story0, String story1, String story2, String inviteCode, String facebookUid, String appleUid, String appsflyerUid, String officialUniversity, String officialMajor, boolean officialUniversityChecked, boolean officialMajorChecked, boolean officialUniversityPublic, boolean officialMajorPublic, boolean officialInfoPublic, String certificationType, String fcmToken, String device, String signUpProgress, String stageTodayIntro, String stageCloseUser, String stageEvaluation, String stageMeeting, String stageMeetingApplicant, String stageMeetingHost, String stageCommunity, String stageTutorial, String stageGuide, String stageGuide2, double averageOfReceiveScore, double averageOfSendScore, int sizeOfReceiveScore, int sizeOfSendScore, double tierPercent, double tierRecent, int tierRecentCount, double screeningScore, int screeningScoreCount, double introMannerScore, double meetingMannerScore, long activationTime, boolean appPushOn) {
        this.uid = uid;
        this.di = di;
        this.nationality = nationality;
        this.email = email;
        this.membership = membership;
        this.dateOfSignUp = dateOfSignUp;
        this.gender = gender;
        this.birthYear = birthYear;
        this.age = age;
        this.nickname = nickname;
        this.university = university;
        this.major = major;
        this.location = location;
        this.geoPoint = geoPoint;
        this.geoHash = geoHash;
        this.geoPermitted = geoPermitted;
        this.height = height;
        this.personality = personality;
        this.bloodType = bloodType;
        this.smoking = smoking;
        this.drinking = drinking;
        this.religion = religion;
        this.photoUrl = photoUrl;
        this.selfIntroduction = selfIntroduction;
        this.story0 = story0;
        this.story1 = story1;
        this.story2 = story2;
        this.inviteCode = inviteCode;
        this.facebookUid = facebookUid;
        this.appleUid = appleUid;
        this.appsflyerUid = appsflyerUid;
        this.officialUniversity = officialUniversity;
        this.officialMajor = officialMajor;
        this.officialUniversityChecked = officialUniversityChecked;
        this.officialMajorChecked = officialMajorChecked;
        this.officialUniversityPublic = officialUniversityPublic;
        this.officialMajorPublic = officialMajorPublic;
        this.officialInfoPublic = officialInfoPublic;
        this.certificationType = certificationType;
        this.fcmToken = fcmToken;
        this.device = device;
        this.signUpProgress = signUpProgress;
        this.stageTodayIntro = stageTodayIntro;
        this.stageCloseUser = stageCloseUser;
        this.stageEvaluation = stageEvaluation;
        this.stageMeeting = stageMeeting;
        this.stageMeetingApplicant = stageMeetingApplicant;
        this.stageMeetingHost = stageMeetingHost;
        this.stageCommunity = stageCommunity;
        this.stageTutorial = stageTutorial;
        this.stageGuide = stageGuide;
        this.stageGuide2 = stageGuide2;
        this.averageOfReceiveScore = averageOfReceiveScore;
        this.averageOfSendScore = averageOfSendScore;
        this.sizeOfReceiveScore = sizeOfReceiveScore;
        this.sizeOfSendScore = sizeOfSendScore;
        this.tierPercent = tierPercent;
        this.tierRecent = tierRecent;
        this.tierRecentCount = tierRecentCount;
        this.screeningScore = screeningScore;
        this.screeningScoreCount = screeningScoreCount;
        this.introMannerScore = introMannerScore;
        this.meetingMannerScore = meetingMannerScore;
        this.activationTime = activationTime;
        this.appPushOn = appPushOn;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
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

    public String getDateOfSignUp() {
        return dateOfSignUp;
    }

    public void setDateOfSignUp(String dateOfSignUp) {
        this.dateOfSignUp = dateOfSignUp;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }




    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
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


    public ArrayList<String> getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(ArrayList<String> photoUrl) {
        this.photoUrl = photoUrl;
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

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getFacebookUid() {
        return facebookUid;
    }

    public void setFacebookUid(String facebookUid) {
        this.facebookUid = facebookUid;
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

    public boolean isOfficialUniversityChecked() {
        return officialUniversityChecked;
    }

    public void setOfficialUniversityChecked(boolean officialUniversityChecked) {
        this.officialUniversityChecked = officialUniversityChecked;
    }

    public boolean isOfficialMajorChecked() {
        return officialMajorChecked;
    }

    public void setOfficialMajorChecked(boolean officialMajorChecked) {
        this.officialMajorChecked = officialMajorChecked;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public boolean isOfficialUniversityPublic() {
        return officialUniversityPublic;
    }

    public void setOfficialUniversityPublic(boolean officialUniversityPublic) {
        this.officialUniversityPublic = officialUniversityPublic;
    }

    public boolean isOfficialMajorPublic() {
        return officialMajorPublic;
    }

    public void setOfficialMajorPublic(boolean officialMajorPublic) {
        this.officialMajorPublic = officialMajorPublic;
    }

    public boolean isOfficialInfoPublic() {
        return officialInfoPublic;
    }

    public void setOfficialInfoPublic(boolean officialInfoPublic) {
        this.officialInfoPublic = officialInfoPublic;
    }

    public String getCertificationType() {
        return certificationType;
    }

    public void setCertificationType(String certificationType) {
        this.certificationType = certificationType;
    }

    public String getSignUpProgress() {
        return signUpProgress;
    }

    public void setSignUpProgress(String signUpProgress) {
        this.signUpProgress = signUpProgress;
    }

    public String getDi() {
        return di;
    }

    public void setDi(String di) {
        this.di = di;
    }


    public MyGeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(MyGeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public boolean isGeoPermitted() {
        return geoPermitted;
    }

    public void setGeoPermitted(boolean geoPermitted) {
        this.geoPermitted = geoPermitted;
    }

    public String getAppleUid() {
        return appleUid;
    }

    public void setAppleUid(String appleUid) {
        this.appleUid = appleUid;
    }

    public String getStageTodayIntro() {
        return stageTodayIntro;
    }

    public void setStageTodayIntro(String stageTodayIntro) {
        this.stageTodayIntro = stageTodayIntro;
    }

    public String getStageCloseUser() {
        return stageCloseUser;
    }

    public void setStageCloseUser(String stageCloseUser) {
        this.stageCloseUser = stageCloseUser;
    }

    public String getStageEvaluation() {
        return stageEvaluation;
    }

    public void setStageEvaluation(String stageEvaluation) {
        this.stageEvaluation = stageEvaluation;
    }

    public String getStageMeeting() {
        return stageMeeting;
    }

    public void setStageMeeting(String stageMeeting) {
        this.stageMeeting = stageMeeting;
    }

    public String getStageMeetingApplicant() {
        return stageMeetingApplicant;
    }

    public void setStageMeetingApplicant(String stageMeetingApplicant) {
        this.stageMeetingApplicant = stageMeetingApplicant;
    }

    public String getStageMeetingHost() {
        return stageMeetingHost;
    }

    public void setStageMeetingHost(String stageMeetingHost) {
        this.stageMeetingHost = stageMeetingHost;
    }

    public String getStageCommunity() {
        return stageCommunity;
    }

    public void setStageCommunity(String stageCommunity) {
        this.stageCommunity = stageCommunity;
    }

    public String getStageTutorial() {
        return stageTutorial;
    }

    public void setStageTutorial(String stageTutorial) {
        this.stageTutorial = stageTutorial;
    }

    public String getStageGuide() {
        return stageGuide;
    }

    public void setStageGuide(String stageGuide) {
        this.stageGuide = stageGuide;
    }


    //getAge만 따로 만듦
    public String getAge() {
        return birthToAge(this.birthYear);
    }


    public void setAge(String age) {
        this.age =  birthToAge(this.birthYear);
    }

    public static String birthToAge(int birthInt){
        return String.valueOf(DateUtil.getYear() + 1 - birthInt);
    }


    public String getAppsflyerUid() {
        return appsflyerUid;
    }

    public void setAppsflyerUid(String appsflyerUid) {
        this.appsflyerUid = appsflyerUid;
    }

    public double getAverageOfReceiveScore() {
        return averageOfReceiveScore;
    }

    public void setAverageOfReceiveScore(double averageOfReceiveScore) {
        this.averageOfReceiveScore = averageOfReceiveScore;
    }

    public double getAverageOfSendScore() {
        return averageOfSendScore;
    }

    public void setAverageOfSendScore(double averageOfSendScore) {
        this.averageOfSendScore = averageOfSendScore;
    }

    public int getSizeOfReceiveScore() {
        return sizeOfReceiveScore;
    }

    public void setSizeOfReceiveScore(int sizeOfReceiveScore) {
        this.sizeOfReceiveScore = sizeOfReceiveScore;
    }

    public int getSizeOfSendScore() {
        return sizeOfSendScore;
    }

    public void setSizeOfSendScore(int sizeOfSendScore) {
        this.sizeOfSendScore = sizeOfSendScore;
    }

    public double getTierPercent() {
        return tierPercent;
    }

    public void setTierPercent(double tierPercent) {
        this.tierPercent = tierPercent;
    }

    public double getIntroMannerScore() {
        return introMannerScore;
    }

    public void setIntroMannerScore(double introMannerScore) {
        this.introMannerScore = introMannerScore;
    }

    public double getMeetingMannerScore() {
        return meetingMannerScore;
    }

    public void setMeetingMannerScore(double meetingMannerScore) {
        this.meetingMannerScore = meetingMannerScore;
    }

    public double getTierRecent() {
        return tierRecent;
    }

    public void setTierRecent(double tierRecent) {
        this.tierRecent = tierRecent;
    }

    public int getTierRecentCount() {
        return tierRecentCount;
    }

    public void setTierRecentCount(int tierRecentCount) {
        this.tierRecentCount = tierRecentCount;
    }

    public double getScreeningScore() {
        return screeningScore;
    }

    public void setScreeningScore(double screeningScore) {
        this.screeningScore = screeningScore;
    }

    public int getScreeningScoreCount() {
        return screeningScoreCount;
    }

    public void setScreeningScoreCount(int screeningScoreCount) {
        this.screeningScoreCount = screeningScoreCount;
    }

    public String getStageGuide2() {
        return stageGuide2;
    }

    public void setStageGuide2(String stageGuide2) {
        this.stageGuide2 = stageGuide2;
    }

    public long getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(long activationTime) {
        this.activationTime = activationTime;
    }

    public boolean isAppPushOn() {
        return appPushOn;
    }

    public void setAppPushOn(boolean appPushOn) {
        this.appPushOn = appPushOn;
    }
}
