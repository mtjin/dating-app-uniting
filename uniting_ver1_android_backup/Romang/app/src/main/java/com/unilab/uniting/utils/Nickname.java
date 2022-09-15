package com.unilab.uniting.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Nickname  {
    public  List<String> wordList1 = Arrays.asList("놀라운",
            "잘생긴",
            "힙한",
            "지루한",
            "도전적인",
            "실망한",
            "기쁜",
            "행복한",
            "우울한",
            "건장한",
            "마른",
            "걱정하는",
            "피곤한",
            "흥분한",
            "자신있는",
            "어린",
            "늙은",
            "현명한",
            "지혜로운",
            "사랑스런",
            "근육질의",
            "글래머한",
            "통통한",
            "커다란",
            "작은",
            "든든한",
            "패셔너블",
            "블링한",
            "귀여운",
            "사랑스런",
            "뿜빰뿜빰",
            "하트뿅뿅",
            "우락부락",
            "만족스런",
            "평온한",
            "고풍스런",
            "세련된");

    public List<String> wordList2 = Arrays.asList("호랑이",
            "오랑우탄",
            "오징어",
            "고등어",
            "사자",
            "원숭이",
            "사자",
            "염소",
            "양",
            "젖소",
            "말",
            "낙타",
            "코뿔소",
            "하마",
            "기린",
            "코끼리",
            "고슴도치",
            "두더지",
            "생쥐",
            "북극곰",
            "표범",
            "하이에나",
            "오소리",
            "여우",
            "늑대",
            "스컹크",
            "족제비",
            "바다표범",
            "돌고래",
            "범고래",
            "햄스터",
            "기니피그",
            "산토끼",
            "다람쥐",
            "코알라",
            "캥거루",
            "침팬지",
            "청개구리",
            "벌새",
            "앵무새",
            "제비",
            "딱따구리",
            "독수리",
            "펭귄",
            "공작",
            "무당벌레",
            "카멜레온",
            "연어",
            "참방게",
            "고양이",
            "황소",
            "강아지");

    public Nickname(){

    }

    public String getRandomNickname(){
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int adjectiveIndex = random.nextInt(wordList1.size());
        int nickNameIndex = random.nextInt(wordList1.size());
        return  wordList1.get(adjectiveIndex) + " " + wordList2.get(nickNameIndex);
    }
}
