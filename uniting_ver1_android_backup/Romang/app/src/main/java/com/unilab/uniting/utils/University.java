package com.unilab.uniting.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class University {

    public static List<String> universityList = Arrays.asList(
            "경희대",
            "연세대 신촌캠",
            "중앙대",
            "성균관대",
            "경북대",
            "고려대 서울캠",
            "단국대",
            "영남대",
            "한양대 서울캠",
            "계명대",
            "부산대",
            "전남대",
            "한국외대",
            "강원대",
            "전북대",
            "인하대",
            "건국대 서울캠",
            "서울대",
            "이화여대",
            "동아대",
            "가천대",
            "국민대",
            "충남대",
            "동국대 서울캠",
            "숭실대",
            "홍익대 서울캠",
            "대구대",
            "부경대",
            "명지대",
            "경기대",
            "숙명여대",
            "충북대",
            "경상대",
            "조선대",
            "동의대",
            "인천대",
            "울산대",
            "성신여대",
            "세종대",
            "경성대",
            "백석대",
            "대구가톨릭대",
            "아주대",
            "공주대",
            "원광대",
            "광운대",
            "호서대",
            "서울시립대",
            "한양대 ERICA캠",
            "서울과기대",
            "서강대",
            "한남대",
            "순천향대",
            "경남대",
            "서울여대",
            "제주대",
            "수원대",
            "연세대 미래캠",
            "동서대",
            "청주대",
            "가톨릭대",
            "인제대",
            "한림대",
            "상명대 서울캠",
            "남서울대",
            "동국대 경주캠",
            "대전대",
            "부산외대",
            "강릉원주대",
            "고려대 세종캠",
            "건국대 GLOCAL캠",
            "가톨릭관동대",
            "덕성여대",
            "창원대",
            "전주대",
            "홍익대 세종캠",
            "대진대",
            "ICT폴리텍대",
            "KAIST",
            "KC대",
            "가야대",
            "가톨릭상지대",
            "감리교신학대",
            "강남대",
            "강동대",
            "강릉영동대",
            "강원관광대",
            "강원도립대",
            "거제대",
            "건양대",
            "건양사이버대",
            "경기과기대",
            "경남과기대",
            "경남도립거창대",
            "경남도립남해대",
            "경남정보대",
            "경동대",
            "경민대",
            "경복대",
            "경북과학대",
            "경북도립대",
            "경북보건대",
            "경북전문대",
            "경운대",
            "경인교대",
            "경인여대",
            "경일대",
            "경주대",
            "경찰대",
            "경희사이버대",
            "계명문화대",
            "계원예대",
            "고구려대",
            "고려사이버대",
            "고신대",
            "공주교대",
            "광신대",
            "광양보건대",
            "광주가톨릭대",
            "광주과기원",
            "광주교대",
            "광주대",
            "광주보건대",
            "광주여대",
            "구미대",
            "국제대",
            "국제사이버대",
            "국제예대",
            "군산간호대",
            "군산대",
            "군장대",
            "극동대",
            "글로벌사이버대",
            "금강대",
            "금오공대",
            "기독간호대",
            "김천대",
            "김포대",
            "김해대",
            "꽃동네대",
            "나사렛대",
            "남부대",
            "농협대",
            "대경대",
            "대구경북과기원",
            "대구공업대",
            "대구과학대",
            "대구교대",
            "대구보건대",
            "대구사이버대",
            "대구예대",
            "대구한의대",
            "대덕대",
            "대동대",
            "대림대",
            "대신대",
            "대원대",
            "대전가톨릭대",
            "대전과기대",
            "대전보건대",
            "대전신학대",
            "동강대",
            "동남보건대",
            "동덕여대",
            "동명대",
            "동부산대",
            "동서울대",
            "동신대",
            "동아방송예대",
            "동아보건대",
            "동양대",
            "동양미래대",
            "동원과기대",
            "동원대",
            "동의과학대",
            "동주대",
            "두원공대",
            "디지털서울문화예대",
            "라사라패션직업전문학교",
            "루터대",
            "마산대",
            "명지전문대",
            "목원대",
            "목포가톨릭대",
            "목포과학대",
            "목포대",
            "목포해양대",
            "문경대",
            "배재대",
            "배화여대",
            "백석문화대",
            "백석예대",
            "백제예대",
            "부산가톨릭대",
            "부산경상대",
            "부산과기대",
            "부산교대",
            "부산디지털대",
            "부산여대",
            "부산예대",
            "부산장신대",
            "부천대",
            "사이버한국외대",
            "삼육대",
            "삼육보건대",
            "상명대 천안캠",
            "상지대",
            "상지영서대",
            "서경대",
            "서라벌대",
            "서영대",
            "서울교대",
            "서울기독대",
            "서울디지털대",
            "서울사이버대",
            "서울신학대",
            "서울여자간호대",
            "서울예대",
            "서울장신대",
            "서울한영대",
            "서울현대전문학교",
            "서울호텔종합전문학교",
            "서원대",
            "서일대",
            "서정대",
            "서해대",
            "선린대",
            "선문대",
            "성결대",
            "성공회대",
            "성덕대",
            "세경대",
            "세명대",
            "세종사이버대",
            "세한대",
            "송곡대",
            "송원대",
            "송호대",
            "수성대",
            "수원가톨릭대",
            "수원과학대",
            "수원여대",
            "순복음총회신학교",
            "순천대",
            "순천제일대",
            "숭실사이버대",
            "숭의여대",
            "신경대",
            "신구대",
            "신라대",
            "신성대",
            "신안산대",
            "신한대",
            "아세아연합신학대",
            "아주자동차대",
            "안동과학대",
            "안동대",
            "안산대",
            "안양대",
            "여주대",
            "연성대",
            "연암공대",
            "연암대",
            "영남신학대",
            "영남외대",
            "영남이공대",
            "영산대",
            "영산선학대",
            "영진사이버대",
            "영진전문대",
            "예수대",
            "예원예대",
            "오산대",
            "용인대",
            "용인송담대",
            "우석대",
            "우송대",
            "우송정보대",
            "울산과기원",
            "울산과학대",
            "웅지세무대",
            "원광디지털대",
            "원광보건대",
            "위덕대",
            "유원대",
            "유한대",
            "을지대",
            "인덕대",
            "인천가톨릭대",
            "인천재능대",
            "인하공업전문대",
            "장로회신학대",
            "장안대",
            "전남과학대",
            "전남도립대",
            "전북과학대",
            "전주교대",
            "전주기전대",
            "전주비전대",
            "정석대",
            "정화예대",
            "제주관광대",
            "제주국제대",
            "제주한라대",
            "조선간호대",
            "조선이공대",
            "중부대",
            "중앙승가대",
            "중원대",
            "진주교대",
            "진주보건대",
            "차의과학대",
            "창신대",
            "창원문성대",
            "청강문화산업대",
            "청암대",
            "청운대",
            "청주교대",
            "초당대",
            "총신대",
            "추계예대",
            "춘천교대",
            "춘해보건대",
            "충남도립대",
            "충북도립대",
            "충북보건과학대",
            "충청대",
            "침례신학대",
            "칼빈대",
            "평택대",
            "포항공대",
            "포항대",
            "한경대",
            "한국골프대",
            "한국관광대",
            "한국교원대",
            "한국교통대",
            "한국국제대",
            "한국기술교대",
            "한국농수산대",
            "한국뉴욕주립대",
            "한국방송통신대",
            "한국복지대",
            "한국복지사이버대",
            "한국산기대",
            "한국성서대",
            "한국승강기대",
            "한국열린사이버대",
            "한국영상대",
            "한국예술종합학교",
            "한국전통문화대",
            "한국체대",
            "한국폴리텍대",
            "한국항공대",
            "한국해양대",
            "한동대",
            "한라대",
            "한려대",
            "한림성심대",
            "한밭대",
            "한서대",
            "한성대",
            "한세대",
            "한신대",
            "한양사이버대",
            "한양여대",
            "한영대",
            "한일장신대",
            "협성대",
            "혜전대",
            "호남대",
            "호남신학대",
            "호산대",
            "호원대",
            "화신사이버대",
            "해외대");

    public static Map<String, String> universityDict = new HashMap<>();
    public static Set<String> universityOfficialSet = universityDict.keySet();
    public static ArrayList<String> universityOfficialList = new ArrayList<>(universityOfficialSet);

    public static void init() {
        universityDict.put("경희대학교 국제캠퍼스","경희대");
        universityDict.put("경희대학교 서울캠퍼스","경희대");
        universityDict.put("연세대학교 신촌캠퍼스","연세대 신촌캠");
        universityDict.put("중앙대학교 서울캠퍼스","중앙대");
        universityDict.put("중앙대학교 안성캠퍼스","중앙대");
        universityDict.put("성균관대학교 인사캠퍼스","성균관대");
        universityDict.put("성균관대학교 자과캠퍼스","성균관대");
        universityDict.put("경북대학교 대구캠퍼스","경북대");
        universityDict.put("경북대학교 상주캠퍼스","경북대");
        universityDict.put("고려대학교 서울캠퍼스","고려대 서울캠");
        universityDict.put("단국대학교 죽전캠퍼스","단국대");
        universityDict.put("단국대학교 천안캠퍼스","단국대");
        universityDict.put("영남대학교","영남대");
        universityDict.put("한양대학교 서울캠퍼스","한양대 서울캠");
        universityDict.put("계명대학교","계명대");
        universityDict.put("부산대학교","부산대");
        universityDict.put("전남대학교 광주캠퍼스","전남대");
        universityDict.put("전남대학교 여수캠퍼스","전남대");
        universityDict.put("한국외국어대학교 서울캠퍼스","한국외대");
        universityDict.put("한국외국어대학교 글로벌캠퍼스","한국외대");
        universityDict.put("강원대학교 춘천캠퍼스","강원대");
        universityDict.put("강원대학교 삼척도계캠퍼스","강원대");
        universityDict.put("전북대학교","전북대");
        universityDict.put("인하대학교","인하대");
        universityDict.put("건국대학교 서울캠퍼스","건국대 서울캠");
        universityDict.put("서울대학교","서울대");
        universityDict.put("이화여자대학교","이화여대");
        universityDict.put("동아대학교","동아대");
        universityDict.put("가천대학교","가천대");
        universityDict.put("국민대학교","국민대");
        universityDict.put("충남대학교","충남대");
        universityDict.put("동국대학교 서울캠퍼스","동국대 서울캠");
        universityDict.put("숭실대학교","숭실대");
        universityDict.put("홍익대학교 서울캠퍼스","홍익대 서울캠");
        universityDict.put("대구대학교","대구대");
        universityDict.put("부경대학교","부경대");
        universityDict.put("명지대학교 인문캠퍼스","명지대");
        universityDict.put("명지대학교 자연캠퍼스","명지대");
        universityDict.put("경기대학교 수원캠퍼스","경기대");
        universityDict.put("경기대학교 서울캠퍼스","경기대");
        universityDict.put("숙명여자대학교","숙명여대");
        universityDict.put("충북대학교","충북대");
        universityDict.put("경상대학교","경상대");
        universityDict.put("조선대학교","조선대");
        universityDict.put("동의대학교","동의대");
        universityDict.put("인천대학교","인천대");
        universityDict.put("울산대학교","울산대");
        universityDict.put("성신여자대학교","성신여대");
        universityDict.put("세종대학교","세종대");
        universityDict.put("경성대학교","경성대");
        universityDict.put("백석대학교","백석대");
        universityDict.put("대구가톨릭대학교","대구가톨릭대");
        universityDict.put("아주대학교","아주대");
        universityDict.put("공주대학교","공주대");
        universityDict.put("원광대학교","원광대");
        universityDict.put("광운대학교","광운대");
        universityDict.put("호서대학교","호서대");
        universityDict.put("서울시립대학교","서울시립대");
        universityDict.put("한양대학교 ERICA캠퍼스","한양대 ERICA캠");
        universityDict.put("서울과학기술대학교","서울과기대");
        universityDict.put("서강대학교","서강대");
        universityDict.put("한남대학교","한남대");
        universityDict.put("순천향대학교","순천향대");
        universityDict.put("경남대학교","경남대");
        universityDict.put("서울여자대학교","서울여대");
        universityDict.put("제주대학교","제주대");
        universityDict.put("수원대학교","수원대");
        universityDict.put("연세대학교 미래캠퍼스","연세대 미래캠");
        universityDict.put("동서대학교","동서대");
        universityDict.put("청주대학교","청주대");
        universityDict.put("가톨릭대학교","가톨릭대");
        universityDict.put("인제대학교","인제대");
        universityDict.put("한림대학교","한림대");
        universityDict.put("상명대학교 서울캠퍼스","상명대 서울캠");
        universityDict.put("남서울대학교","남서울대");
        universityDict.put("동국대학교 경주캠퍼스","동국대 경주캠");
        universityDict.put("대전대학교","대전대");
        universityDict.put("부산외국어대학교","부산외대");
        universityDict.put("강릉원주대학교 강릉캠퍼스","강릉원주대");
        universityDict.put("강릉원주대학교 원주캠퍼스","강릉원주대");
        universityDict.put("고려대학교 세종캠퍼스","고려대 세종캠");
        universityDict.put("건국대학교 GLOCAL캠퍼스","건국대 GLOCAL캠");
        universityDict.put("가톨릭관동대학교","가톨릭관동대");
        universityDict.put("덕성여자대학교","덕성여대");
        universityDict.put("창원대학교","창원대");
        universityDict.put("전주대학교","전주대");
        universityDict.put("홍익대학교 세종캠퍼스","홍익대 세종캠");
        universityDict.put("대진대학교","대진대");
        universityDict.put("ICT폴리텍대학","ICT폴리텍대");
        universityDict.put("KAIST(카이스트)","KAIST");
        universityDict.put("KC대학교","KC대");
        universityDict.put("가야대학교","가야대");
        universityDict.put("가톨릭상지대학교","가톨릭상지대");
        universityDict.put("감리교신학대학교","감리교신학대");
        universityDict.put("강남대학교","강남대");
        universityDict.put("강동대학교","강동대");
        universityDict.put("강릉영동대학교","강릉영동대");
        universityDict.put("강원관광대학교","강원관광대");
        universityDict.put("강원도립대학교","강원도립대");
        universityDict.put("거제대학교","거제대");
        universityDict.put("건양대학교","건양대");
        universityDict.put("건양사이버대학교","건양사이버대");
        universityDict.put("경기과학기술대학교","경기과기대");
        universityDict.put("경남과학기술대학교","경남과기대");
        universityDict.put("경남도립거창대학교","경남도립거창대");
        universityDict.put("경남도립남해대학교","경남도립남해대");
        universityDict.put("경남정보대학교","경남정보대");
        universityDict.put("경동대학교 양주캠퍼스","경동대");
        universityDict.put("경동대학교 고성캠퍼스","경동대");
        universityDict.put("경동대학교 원주캠퍼스","경동대");
        universityDict.put("경민대학교","경민대");
        universityDict.put("경복대학교","경복대");
        universityDict.put("경북과학대학교","경북과학대");
        universityDict.put("경북도립대학교","경북도립대");
        universityDict.put("경북보건대학교","경북보건대");
        universityDict.put("경북전문대학교","경북전문대");
        universityDict.put("경운대학교","경운대");
        universityDict.put("경인교육대학교","경인교대");
        universityDict.put("경인여자대학교","경인여대");
        universityDict.put("경일대학교","경일대");
        universityDict.put("경주대학교","경주대");
        universityDict.put("경찰대학교","경찰대");
        universityDict.put("경희사이버대학교","경희사이버대");
        universityDict.put("계명문화대학교","계명문화대");
        universityDict.put("계원예술대학교","계원예대");
        universityDict.put("고구려대학교","고구려대");
        universityDict.put("고려사이버대학교","고려사이버대");
        universityDict.put("고신대학교","고신대");
        universityDict.put("공주교육대학교","공주교대");
        universityDict.put("광신대학교","광신대");
        universityDict.put("광양보건대학교","광양보건대");
        universityDict.put("광주가톨릭대학교","광주가톨릭대");
        universityDict.put("광주과학기술원","광주과기원");
        universityDict.put("광주교육대학교","광주교대");
        universityDict.put("광주대학교","광주대");
        universityDict.put("광주보건대학교","광주보건대");
        universityDict.put("광주여자대학교","광주여대");
        universityDict.put("구미대학교","구미대");
        universityDict.put("국제대학교","국제대");
        universityDict.put("국제사이버대학교","국제사이버대");
        universityDict.put("국제예술대학","국제예대");
        universityDict.put("군산간호대학교","군산간호대");
        universityDict.put("군산대학교","군산대");
        universityDict.put("군장대학교","군장대");
        universityDict.put("극동대학교","극동대");
        universityDict.put("글로벌사이버대학교","글로벌사이버대");
        universityDict.put("금강대학교","금강대");
        universityDict.put("금오공과대학교","금오공대");
        universityDict.put("기독간호대학교","기독간호대");
        universityDict.put("김천대학교","김천대");
        universityDict.put("김포대학교","김포대");
        universityDict.put("김해대학교","김해대");
        universityDict.put("꽃동네대학교","꽃동네대");
        universityDict.put("나사렛대학교","나사렛대");
        universityDict.put("남부대학교","남부대");
        universityDict.put("농협대학교","농협대");
        universityDict.put("대경대학교","대경대");
        universityDict.put("대구경북과학기술원","대구경북과기원");
        universityDict.put("대구공업대학교","대구공업대");
        universityDict.put("대구과학대학교","대구과학대");
        universityDict.put("대구교육대학교","대구교대");
        universityDict.put("대구보건대학교","대구보건대");
        universityDict.put("대구사이버대학교","대구사이버대");
        universityDict.put("대구예술대학교","대구예대");
        universityDict.put("대구한의대학교","대구한의대");
        universityDict.put("대덕대학교","대덕대");
        universityDict.put("대동대학교","대동대");
        universityDict.put("대림대학교","대림대");
        universityDict.put("대신대학교","대신대");
        universityDict.put("대원대학교","대원대");
        universityDict.put("대전가톨릭대학교","대전가톨릭대");
        universityDict.put("대전과학기술대학교","대전과기대");
        universityDict.put("대전보건대학교","대전보건대");
        universityDict.put("대전신학대학교","대전신학대");
        universityDict.put("동강대학교","동강대");
        universityDict.put("동남보건대학교","동남보건대");
        universityDict.put("동덕여자대학교","동덕여대");
        universityDict.put("동명대학교","동명대");
        universityDict.put("동부산대학교","동부산대");
        universityDict.put("동서울대학교","동서울대");
        universityDict.put("동신대학교","동신대");
        universityDict.put("동아방송예술대학교","동아방송예대");
        universityDict.put("동아보건대학교","동아보건대");
        universityDict.put("동양대학교","동양대");
        universityDict.put("동양미래대학교","동양미래대");
        universityDict.put("동원과학기술대학교","동원과기대");
        universityDict.put("동원대학교","동원대");
        universityDict.put("동의과학대학교","동원대");
        universityDict.put("동주대학교","동주대");
        universityDict.put("두원공과대학교","두원공대");
        universityDict.put("디지털서울문화예술대학교","디지털서울문화예대");
        universityDict.put("라사라패션직업전문학교","라사라패션직업전문학교");
        universityDict.put("루터대학교","루터대");
        universityDict.put("마산대학교","마산대");
        universityDict.put("명지전문대학","명지전문대");
        universityDict.put("목원대학교","목원대");
        universityDict.put("목포가톨릭대학교","목포가톨릭대");
        universityDict.put("목포과학대학교","목포과학대");
        universityDict.put("목포대학교","목포대");
        universityDict.put("목포해양대학교","목포해양대");
        universityDict.put("문경대학교","문경대");
        universityDict.put("배재대학교","배재대");
        universityDict.put("배화여자대학교","배화여대");
        universityDict.put("백석문화대학교","백석문화대");
        universityDict.put("백석예술대학교","백석예대");
        universityDict.put("백제예술대학교","백제예대");
        universityDict.put("부산가톨릭대학교","부산가톨릭대");
        universityDict.put("부산경상대학교","부산경상대");
        universityDict.put("부산과학기술대학교","부산과기대");
        universityDict.put("부산교육대학교","부산교대");
        universityDict.put("부산디지털대학교","부산디지털대");
        universityDict.put("부산여자대학교","부산여대");
        universityDict.put("부산예술대학교","부산예대");
        universityDict.put("부산장신대학교","부산장신대");
        universityDict.put("부천대학교","부천대");
        universityDict.put("사이버한국외국어대학교","사이버한국외대");
        universityDict.put("삼육대학교","삼육대");
        universityDict.put("삼육보건대학교","삼육보건대");
        universityDict.put("상명대학교 천안캠퍼스","상명대 천안캠");
        universityDict.put("상지대학교","상지대");
        universityDict.put("상지영서대학교","상지영서대");
        universityDict.put("서경대학교","서경대");
        universityDict.put("서라벌대학교","서라벌대");
        universityDict.put("서영대학교 파주캠퍼스","서영대");
        universityDict.put("서영대학교 광주캠퍼스","서영대");
        universityDict.put("서울교육대학교","서울교대");
        universityDict.put("서울기독대학교","서울기독대");
        universityDict.put("서울디지털대학교","서울디지털대");
        universityDict.put("서울사이버대학교","서울사이버대");
        universityDict.put("서울신학대학교","서울신학대");
        universityDict.put("서울여자간호대학교","서울여자간호대");
        universityDict.put("서울예술대학교","서울예대");
        universityDict.put("서울장신대학교","서울장신대");
        universityDict.put("서울한영대학교","서울한영대");
        universityDict.put("서울현대전문학교","서울현대전문학교");
        universityDict.put("서울호텔종합전문학교","서울호텔종합전문학교");
        universityDict.put("서원대학교","서원대");
        universityDict.put("서일대학교","서일대");
        universityDict.put("서정대학교","서정대");
        universityDict.put("서해대학교","서해대");
        universityDict.put("선린대학교","선린대");
        universityDict.put("선문대학교","선문대");
        universityDict.put("성결대학교","성결대");
        universityDict.put("성공회대학교","성공회대");
        universityDict.put("성덕대학교","성덕대");
        universityDict.put("세경대학교","세경대");
        universityDict.put("세명대학교","세명대");
        universityDict.put("세종사이버대학교","세종사이버대");
        universityDict.put("세한대학교 영암캠퍼스","세한대");
        universityDict.put("세한대학교 당진캠퍼스","세한대");
        universityDict.put("송곡대학교","송곡대");
        universityDict.put("송원대학교","송원대");
        universityDict.put("송호대학교","송호대");
        universityDict.put("수성대학교","수성대");
        universityDict.put("수원가톨릭대학교","수원가톨릭대");
        universityDict.put("수원과학대학교","수원과학대");
        universityDict.put("수원여자대학교","수원여대");
        universityDict.put("순복음총회신학교","순복음총회신학교");
        universityDict.put("순천대학교","순천대");
        universityDict.put("순천제일대학교","순천제일대");
        universityDict.put("숭실사이버대학교","숭실사이버대");
        universityDict.put("숭의여자대학교","숭의여대");
        universityDict.put("신경대학교","신경대");
        universityDict.put("신구대학교","신구대");
        universityDict.put("신라대학교","신라대");
        universityDict.put("신성대학교","신성대");
        universityDict.put("신안산대학교","신안산대");
        universityDict.put("신한대학교","신한대");
        universityDict.put("아세아연합신학대학교","아세아연합신학대");
        universityDict.put("아주자동차대학교","아주자동차대");
        universityDict.put("안동과학대학교","안동과학대");
        universityDict.put("안동대학교","안동대");
        universityDict.put("안산대학교","안산대");
        universityDict.put("안양대학교","안양대");
        universityDict.put("여주대학교","여주대");
        universityDict.put("연성대학교","연성대");
        universityDict.put("연암공과대학교","연암공대");
        universityDict.put("연암대학교","연암대");
        universityDict.put("영남신학대학교","영남신학대");
        universityDict.put("영남외국어대학","영남외대");
        universityDict.put("영남이공대학교","영남이공대");
        universityDict.put("영산대학교","영산대");
        universityDict.put("영산선학대학교","영산선학대");
        universityDict.put("영진사이버대학교","영진사이버대");
        universityDict.put("영진전문대학","영진전문대");
        universityDict.put("예수대학교","예수대");
        universityDict.put("예원예술대학교 양주캠퍼스","예원예대");
        universityDict.put("예원예술대학교 임실캠퍼스","예원예대");
        universityDict.put("오산대학교","오산대");
        universityDict.put("용인대학교","용인대");
        universityDict.put("용인송담대학교","용인송담대");
        universityDict.put("우석대학교 진천캠퍼스","우석대");
        universityDict.put("우석대학교 전주캠퍼스","우석대");
        universityDict.put("우송대학교","우송대");
        universityDict.put("우송정보대학교","우송정보대");
        universityDict.put("울산과학기술원","울산과기원");
        universityDict.put("울산과학대학교","울산과학대");
        universityDict.put("웅지세무대학교","웅지세무대");
        universityDict.put("원광디지털대학교","원광디지털대");
        universityDict.put("원광보건대학교","원광보건대");
        universityDict.put("위덕대학교","위덕대");
        universityDict.put("유원대학교","유원대");
        universityDict.put("유한대학교","유한대");
        universityDict.put("을지대학교 성남캠퍼스","을지대");
        universityDict.put("을지대학교 대전캠퍼스","을지대");
        universityDict.put("인덕대학교","인덕대");
        universityDict.put("인천가톨릭대학교","인천가톨릭대");
        universityDict.put("인천재능대학교","인천재능대");
        universityDict.put("인하공업전문대학교","인하공업전문대");
        universityDict.put("장로회신학대학교","장로회신학대");
        universityDict.put("장안대학교","장안대");
        universityDict.put("전남과학대학교","전남과학대");
        universityDict.put("전남도립대학교","전남도립대");
        universityDict.put("전북과학대학교","전북과학대");
        universityDict.put("전주교육대학교","전주교대");
        universityDict.put("전주기전대학교","전주기전대");
        universityDict.put("전주비전대학교","전주비전대");
        universityDict.put("정석대학교","정석대");
        universityDict.put("정화예술대학교","정화예대");
        universityDict.put("제주관광대학교","제주관광대");
        universityDict.put("제주국제대학교","제주국제대");
        universityDict.put("제주한라대학교","제주한라대");
        universityDict.put("조선간호대학교","조선간호대");
        universityDict.put("조선이공대학교","조선이공대");
        universityDict.put("중부대학교 충청캠퍼스","중부대");
        universityDict.put("중부대학교 고양캠퍼스","중부대");
        universityDict.put("중앙승가대학교","중앙승가대");
        universityDict.put("중원대학교","중원대");
        universityDict.put("진주교육대학교","진주교대");
        universityDict.put("진주보건대학교","진주보건대");
        universityDict.put("차의과학대학교","차의과학대");
        universityDict.put("창신대학교","창신대");
        universityDict.put("창원문성대학교","창원문성대");
        universityDict.put("청강문화산업대학교","청강문화산업대");
        universityDict.put("청암대학교","청암대");
        universityDict.put("청운대학교 인천캠퍼스","청운대");
        universityDict.put("청운대학교 홍성캠퍼스","청운대");
        universityDict.put("청주교육대학교","청주교대");
        universityDict.put("초당대학교","초당대");
        universityDict.put("총신대학교","총신대");
        universityDict.put("추계예술대학교","추계예대");
        universityDict.put("춘천교육대학교","춘천교대");
        universityDict.put("춘해보건대학교","춘해보건대");
        universityDict.put("충남도립대학교","충남도립대");
        universityDict.put("충북도립대학교","충북도립대");
        universityDict.put("충북보건과학대학교","충북보건과학대");
        universityDict.put("충청대학교","충청대");
        universityDict.put("침례신학대학교","침례신학대");
        universityDict.put("칼빈대학교","칼빈대");
        universityDict.put("평택대학교","평택대");
        universityDict.put("포항공과대학교","포항공대");
        universityDict.put("포항대학교","포항대");
        universityDict.put("한경대학교","한경대");
        universityDict.put("한국골프대학교","한국골프대");
        universityDict.put("한국관광대학교","한국관광대");
        universityDict.put("한국교원대학교","한국교원대");
        universityDict.put("한국교통대학교","한국교통대");
        universityDict.put("한국국제대학교","한국국제대");
        universityDict.put("한국기술교육대학교","한국기술교대");
        universityDict.put("한국농수산대학교","한국농수산대");
        universityDict.put("한국뉴욕주립대학교","한국뉴욕주립대");
        universityDict.put("한국방송통신대학교","한국방송통신대");
        universityDict.put("한국복지대학교","한국복지대");
        universityDict.put("한국복지사이버대학교","한국복지사이버대");
        universityDict.put("한국산업기술대학교","한국산기대");
        universityDict.put("한국성서대학교","한국성서대");
        universityDict.put("한국승강기대학교","한국승강기대");
        universityDict.put("한국열린사이버대학교","한국열린사이버대");
        universityDict.put("한국영상대학교","한국영상대");
        universityDict.put("한국예술종합학교","한국예술종합학교");
        universityDict.put("한국전통문화대학교","한국전통문화대");
        universityDict.put("한국체육대학교","한국체대");
        universityDict.put("한국폴리텍대학교","한국폴리텍대");
        universityDict.put("한국항공대학교","한국항공대");
        universityDict.put("한국해양대학교","한국해양대");
        universityDict.put("한동대학교","한동대");
        universityDict.put("한라대학교","한라대");
        universityDict.put("한려대학교","한려대");
        universityDict.put("한림성심대학교","한림성심대");
        universityDict.put("한밭대학교","한밭대");
        universityDict.put("한서대학교","한서대");
        universityDict.put("한성대학교","한성대");
        universityDict.put("한세대학교","한세대");
        universityDict.put("한신대학교","한신대");
        universityDict.put("한양사이버대학교","한양사이버대");
        universityDict.put("한양여자대학교","한양여대");
        universityDict.put("한영대학교","한영대");
        universityDict.put("한일장신대학교","한일장신대");
        universityDict.put("협성대학교","협성대");
        universityDict.put("혜전대학교","혜전대");
        universityDict.put("호남대학교","호남대");
        universityDict.put("호남신학대학교","호남신학대");
        universityDict.put("호산대학교","호산대");
        universityDict.put("호원대학교","호원대");
        universityDict.put("화신사이버대학교","화신사이버대");
        universityDict.put("해외대","해외대");

        universityOfficialSet = universityDict.keySet();
        universityOfficialList = new ArrayList<>(universityOfficialSet);
        Collections.sort(universityOfficialList);
    }


}
