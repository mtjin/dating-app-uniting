package com.unilab.uniting.model;

import java.io.Serializable;
import java.util.ArrayList;
/*
 *  대학교증명사진 관련은 스토리지에 올리기만하고 모델클래스에는 추가하지않았다.(딱히 필요없을 것 같아서)
 * */


public class ProfilePhotoScreening implements Serializable {
    private ArrayList<String> screeningPhotoUrl; //심사중인 사진의 url 리스트
    private ArrayList<String> screeningResult; //심사결과 (0,1,2)의 리스트
    private ArrayList<String> screeningFileName; //심사중인 사진의 storage 파일 이름


    public ProfilePhotoScreening() {
    }

    public ProfilePhotoScreening(ArrayList<String> screeningPhotoUrl, ArrayList<String> screeningResult, ArrayList<String> screeningFileName) {
        this.screeningPhotoUrl = screeningPhotoUrl;
        this.screeningResult = screeningResult;
        this.screeningFileName = screeningFileName;
    }

    public ArrayList<String> getScreeningPhotoUrl() {
        return screeningPhotoUrl;
    }

    public void setScreeningPhotoUrl(ArrayList<String> screeningPhotoUrl) {
        this.screeningPhotoUrl = screeningPhotoUrl;
    }

    public ArrayList<String> getScreeningResult() {
        return screeningResult;
    }

    public void setScreeningResult(ArrayList<String> screeningResult) {
        this.screeningResult = screeningResult;
    }

    public ArrayList<String> getScreeningFileName() {
        return screeningFileName;
    }

    public void setScreeningFileName(ArrayList<String> screeningFileName) {
        this.screeningFileName = screeningFileName;
    }
}
