package com.unilab.uniting.utils;

import com.unilab.uniting.model.MyGeoPoint;
import com.unilab.uniting.model.User;
import com.unilab.uniting.model.UserForEdit;

import java.util.ArrayList;

public class MyProfile {
    private String uid = ""; //고유토큰
    private String di = ""; //고유토큰
    private String nationality = ""; //고유토큰
    private String email= ""; //이메일(아이디)
    private String membership= ""; //인증단계 - 이메일 가입 직 후 값: 0, 학생증 업로드 후 가입승인 대기중 값 : 1, 운영진이 가입 최종 승인 후 값: 2 (추후 튜토리얼 강제화 할 예정인데 이 경우, 튜토리얼까지 비로소 완료해야 값 3되면서 최종 승인)
    private String dateOfSignUp= ""; //가입신청날짜
    private String gender= ""; //성별
    private int birthYear= 2020; //생년월일
    private String age= ""; //나이
    private String nickname= ""; //닉네임
    private String university= ""; //학교
    private String major= ""; //전공
    private String location= ""; //지역
    private MyGeoPoint geoPoint= new MyGeoPoint(37.492631,127.041548);
    private String geoHash= "";
    private boolean geoPermitted= false;
    private String height= ""; //키
    private ArrayList<String> personality = new ArrayList<>(); //성격
    private String bloodType= ""; //혈액형
    private String smoking= ""; //흡연
    private String drinking= ""; //음주
    private String religion= ""; //종교
    private ArrayList<String> photoUrl= new ArrayList<>(); //스토리지 사진 url
    private String selfIntroduction= ""; //자기 소개글
    private String story0= ""; //첫번재 스토리
    private String story1= ""; //두번째 스토리
    private String story2= ""; //세번째 스토리
    private String inviteCode= ""; //초대 코드
    private String facebookUid= "";
    private String appleUid= "";
    private String appsflyerUid = "";
    private String officialUniversity= ""; //운영자가 학생증 보고 직접 입력한 공식 학교명 ( 추후 객관식 형태로 고르게 만드는게 나을지도?)
    private String officialMajor= ""; //운영자가 학생증 보고 직접 입력한 공식 전공
    private boolean officialUniversityChecked = false;
    private boolean officialMajorChecked = false;
    private boolean officialUniversityPublic = false;
    private boolean officialMajorPublic = false;
    private boolean officialInfoPublic = false;
    private String certificationType = "";
    private String fcmToken= ""; //푸시알림용 토큰
    private String device = "android";
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
    private double averageOfReceiveScore = 0;
    private double averageOfSendScore = 0;
    private int sizeOfReceiveScore = 0;
    private int sizeOfSendScore = 0;
    private double tierPercent = 0;
    private double tierRecent = 0.5;
    private int tierRecentCount = 0;
    private double screeningScore = 0.5;
    private int screeningScoreCount = 0;
    private double introMannerScore = 0;
    private double meetingMannerScore = 0;
    private long activationTime = 0;
    private boolean appPushOn = false;

    private static final MyProfile ourInstance = new MyProfile();

    public static User getUser( ){
        User user = new User(ourInstance.uid,
                ourInstance.di,
                ourInstance.nationality,
                ourInstance.email,
                ourInstance.membership,
                ourInstance.dateOfSignUp,
                ourInstance.gender,
                ourInstance.birthYear,
                ourInstance.age,
                ourInstance.nickname,
                ourInstance.university,
                ourInstance.major,
                ourInstance.location,
                ourInstance.geoPoint,
                ourInstance.geoHash,
                ourInstance.geoPermitted,
                ourInstance.height,
                ourInstance.personality,
                ourInstance.bloodType,
                ourInstance.smoking,
                ourInstance.drinking,
                ourInstance.religion,
                ourInstance.photoUrl,
                ourInstance.selfIntroduction ,
                ourInstance.story0,
                ourInstance.story1,
                ourInstance.story2,
                ourInstance.inviteCode,
                ourInstance.facebookUid,
                ourInstance.appleUid,
                ourInstance.appsflyerUid,
                ourInstance.officialUniversity,
                ourInstance.officialMajor,
                ourInstance.officialUniversityChecked,
                ourInstance.officialMajorChecked,
                ourInstance.officialUniversityPublic,
                ourInstance.officialMajorPublic,
                ourInstance.officialInfoPublic,
                ourInstance.certificationType,
                ourInstance.fcmToken,
                ourInstance.device,
                ourInstance.signUpProgress,
                ourInstance.stageTodayIntro,
                ourInstance.stageCloseUser,
                ourInstance.stageEvaluation,
                ourInstance.stageMeeting,
                ourInstance.stageMeetingApplicant,
                ourInstance.stageMeetingHost,
                ourInstance.stageCommunity,
                ourInstance.stageTutorial,
                ourInstance.stageGuide,
                ourInstance.stageGuide2,
                ourInstance.averageOfReceiveScore,
                ourInstance.averageOfSendScore,
                ourInstance.sizeOfReceiveScore,
                ourInstance.sizeOfSendScore,
                ourInstance.tierPercent,
                ourInstance.tierRecent,
                ourInstance.tierRecentCount,
                ourInstance.screeningScore,
                ourInstance.screeningScoreCount,
                ourInstance.introMannerScore,
                ourInstance.meetingMannerScore,
                ourInstance.activationTime,
                ourInstance.appPushOn);

        return user;
    }



    public static void init(User user){
        ourInstance.uid = user.getUid();
        ourInstance.di = user.getDi();
        ourInstance.nationality = user.getNationality();
        ourInstance.email = user.getEmail();
        ourInstance.membership = user.getMembership();
        ourInstance.dateOfSignUp = user.getDateOfSignUp();
        ourInstance.gender = user.getGender();
        ourInstance.birthYear = user.getBirthYear();
        ourInstance.age = user.getAge();
        ourInstance.nickname = user.getNickname();
        ourInstance.university = user.getUniversity();
        ourInstance.major = user.getMajor();
        ourInstance.location = user.getLocation();
        ourInstance.geoPoint = user.getGeoPoint();
        ourInstance.geoHash = user.getGeoHash();
        ourInstance.geoPermitted = user.isGeoPermitted();
        ourInstance.height = user.getHeight();
        ourInstance.personality = user.getPersonality();
        ourInstance.bloodType = user.getBloodType();
        ourInstance.smoking = user.getSmoking();
        ourInstance.drinking = user.getDrinking();
        ourInstance.religion = user.getReligion();
        ourInstance.photoUrl = user.getPhotoUrl();
        ourInstance.selfIntroduction = user.getSelfIntroduction();
        ourInstance.story0 = user.getStory0();
        ourInstance.story1 = user.getStory1();
        ourInstance.story2 = user.getStory2();
        ourInstance.inviteCode = user.getInviteCode();
        ourInstance.facebookUid = user.getFacebookUid();
        ourInstance.appleUid = user.getAppleUid();
        ourInstance.officialUniversity = user.getOfficialUniversity();
        if(user.getOfficialMajor()!= null){
            ourInstance.officialMajor = user.getOfficialMajor().trim();
        }else{
            ourInstance.officialMajor = "";
        }
        ourInstance.officialUniversityChecked= user.isOfficialUniversityChecked();
        ourInstance.officialMajorChecked= user.isOfficialMajorChecked();
        ourInstance.officialUniversityPublic= user.isOfficialUniversityPublic();
        ourInstance.officialMajorPublic= user.isOfficialMajorPublic();
        ourInstance.officialInfoPublic= user.isOfficialInfoPublic();
        ourInstance.certificationType= user.getCertificationType();
        ourInstance.fcmToken = user.getFcmToken();
        ourInstance.device = user.getDevice();
        ourInstance.signUpProgress = user.getSignUpProgress();

        ourInstance.stageTodayIntro = user.getStageTodayIntro();
        ourInstance.stageCloseUser = user.getStageCloseUser();
        ourInstance.stageEvaluation = user.getStageEvaluation();
        ourInstance.stageMeeting = user.getStageMeeting();
        ourInstance.stageMeetingApplicant = user.getStageMeetingApplicant();
        ourInstance.stageMeetingHost = user.getStageMeetingHost();
        ourInstance.stageCommunity = user.getStageCommunity();
        ourInstance.stageTutorial = user.getStageTutorial();
        ourInstance.stageGuide = user.getStageGuide();
        ourInstance.stageGuide2 = user.getStageGuide2();
        ourInstance.averageOfReceiveScore = user.getAverageOfReceiveScore();
        ourInstance.averageOfSendScore = user.getAverageOfSendScore();
        ourInstance.sizeOfReceiveScore = user.getSizeOfReceiveScore();
        ourInstance.sizeOfSendScore = user.getSizeOfSendScore();
        ourInstance.tierPercent = user.getTierPercent();
        ourInstance.tierRecent = user.getTierRecent();
        ourInstance.tierRecentCount = user.getTierRecentCount();
        ourInstance.screeningScore = user.getScreeningScore();
        ourInstance.screeningScoreCount = user.getScreeningScoreCount();
        ourInstance.introMannerScore = user.getIntroMannerScore();
        ourInstance.meetingMannerScore = user.getMeetingMannerScore();
        ourInstance.activationTime = user.getActivationTime();
        ourInstance.appPushOn = user.isAppPushOn();


        if(ourInstance.uid == null){
            ourInstance.uid = "";
        }
        if(ourInstance.di == null){
            ourInstance.di = "";
        }
        if(ourInstance.nationality == null){
            ourInstance.nationality = "";
        }
        if(ourInstance.email == null){
            ourInstance.email = "";
        }
        if(ourInstance.membership == null){
            ourInstance.membership = "";
        }
        if(ourInstance.dateOfSignUp == null){
            ourInstance.dateOfSignUp = "";
        }
        if(ourInstance.gender == null){
            ourInstance.gender = "";
        }
        if(ourInstance.age == null){
            ourInstance.age = "";
        }
        if(ourInstance.nickname == null){
            ourInstance.nickname = "";
        }
        if(ourInstance.university == null){
            ourInstance.university = "";
        }
        if(ourInstance.major == null){
            ourInstance.major = "";
        }
        if(ourInstance.location == null){
            ourInstance.location = "";
        }
        if(ourInstance.geoPoint == null){
            ourInstance.geoPoint = FirebaseHelper.seoulMyGeoPoint;
        }
        if(ourInstance.geoHash == null){
            ourInstance.geoHash = "";
        }
        if(ourInstance.personality == null){
            ourInstance.personality = new ArrayList<>();
        }
        if(ourInstance.bloodType == null){
            ourInstance.bloodType = "";
        }


        if(ourInstance.smoking == null){
            ourInstance.smoking = "";
        }
        if(ourInstance.drinking == null){
            ourInstance.drinking = "";
        }
        if(ourInstance.religion == null){
            ourInstance.religion = "";
        }
        if(ourInstance.photoUrl == null){
            ourInstance.photoUrl = new ArrayList<>();
        }

        if(ourInstance.selfIntroduction == null){
            ourInstance.selfIntroduction = "";
        }
        if(ourInstance.story0 == null){
            ourInstance.story0 = "";
        }
        if(ourInstance.story1 == null){
            ourInstance.story1 = "";
        }
        if(ourInstance.story2 == null){
            ourInstance.story2 = "";
        }

        if(ourInstance.inviteCode == null){
            ourInstance.inviteCode = "";
        }
        if(ourInstance.facebookUid == null){
            ourInstance.facebookUid = "";
        }
        if(ourInstance.appleUid == null){
            ourInstance.appleUid = "";
        }

        if(ourInstance.appsflyerUid == null){
            ourInstance.appsflyerUid = "";
        }
        if(ourInstance.officialUniversity == null){
            ourInstance.officialUniversity = "";
        }
        if(ourInstance.certificationType == null){
            ourInstance.certificationType = "";
        }
        if(ourInstance.fcmToken == null){
            ourInstance.fcmToken = "";
        }
        if(ourInstance.device == null){
            ourInstance.device = "";
        }
        if(ourInstance.signUpProgress == null){
            ourInstance.signUpProgress = "";
        }
        if(ourInstance.stageTodayIntro == null){
            ourInstance.stageTodayIntro = "";
        }

        if(ourInstance.stageCloseUser == null){
            ourInstance.stageCloseUser = "";
        }
        if(ourInstance.stageEvaluation == null){
            ourInstance.stageEvaluation = "";
        }
        if(ourInstance.stageMeeting == null){
            ourInstance.stageMeeting = "";
        }
        if(ourInstance.stageMeetingApplicant == null){
            ourInstance.stageMeetingApplicant = "";
        }
        if(ourInstance.stageMeetingHost == null){
            ourInstance.stageMeetingHost = "";
        }
        if(ourInstance.stageCommunity == null){
            ourInstance.stageCommunity = "";
        }
        if(ourInstance.stageTutorial == null){
            ourInstance.stageTutorial = "";
        }
        if(ourInstance.stageGuide == null){
            ourInstance.stageGuide = "";
        }
        if(ourInstance.stageGuide2 == null){
            ourInstance.stageGuide2 = "";
        }

    }

    public static void edit(UserForEdit user){
        ourInstance.nickname = user.getNickName();
        ourInstance.location = user.getLocation();
        ourInstance.height = user.getHeight();
        ourInstance.personality = user.getPersonality();
        ourInstance.bloodType = user.getBloodType();
        ourInstance.smoking = user.getSmoking();
        ourInstance.drinking = user.getDrinking();
        ourInstance.religion = user.getReligion();
        ourInstance.selfIntroduction = user.getSelfIntroduction();
        ourInstance.story0 = user.getStory0();
        ourInstance.story1 = user.getStory1();
        ourInstance.story2 = user.getStory2();
    }

    private MyProfile() {

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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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

    public String getSignUpProgress() {
        return signUpProgress;
    }

    public void setSignUpProgress(String signUpProgress) {
        this.signUpProgress = signUpProgress;
    }


    public static MyProfile getOurInstance() {
        return ourInstance;
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
