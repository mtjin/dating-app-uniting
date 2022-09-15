package com.unilab.uniting.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    public static long dayInMilliSecond = 24 * 60 * 60 * 1000;

    //하트 내역 및 adminTDL date
    public static String getDateSec() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        return dateResult;
    }

    //나머지 모든 date
    public static String getDateMin() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        return dateResult;
    }

    public static int getYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String year = sdf.format(date);
        int yearInt = 2020;
        try {
            yearInt = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            yearInt = 2020;
        }

        return yearInt;
    }


    public static String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        return dateResult;
    }

    public static String getMonthAndDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        return dateResult;
    }


    public static String getDateComment() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        return dateResult;
    }

    //유닉스타임
    public static long getUnixTimeLong(){
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    public static long getUnixTimeByHour(){
        Calendar calendar = Calendar.getInstance();
        long unixTime = calendar.getTimeInMillis();
        long unixTimeByHour = unixTime/(60 * 60 * 1000);
        return unixTimeByHour;
    }

    //타임스탬프 : 하트내역 uid로 사용
    public static String getTimeStampUnix(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        String timestamp = Long.toString(calendar.getTimeInMillis());
        return dateResult + " " + timestamp;
    }

    public static String getUnixToDate(long unixTime){
        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        dateSdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        Date date = new Date(unixTime);
        String fullDateResult = dateSdf.format(date); //yyyy-mm-dd
        return fullDateResult;
    }

    public static String getUnixToTime(long unixTime){
        Date date = new Date(unixTime);
        SimpleDateFormat todayDateSdf = new SimpleDateFormat("a hh:mm", Locale.KOREA);
        todayDateSdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String todayDateResult = todayDateSdf.format(date); //오전 8:20
        return todayDateResult;
    }

    public static String getDayFromNow(long standardUnix) {
        String standardDate = getUnixToDate(standardUnix);
        String day = standardDate;
        long dateOfCreate = (standardUnix + 9 * 60 * 60 * 1000) / (24 * 60 * 60 * 1000);
        long dateOfNow = (getUnixTimeLong() + 9 * 60 * 60 * 1000) / (24 * 60 * 60 * 1000);
        long difference = dateOfNow - dateOfCreate;
        int diff = (int) difference;

        switch (diff) {
            case 0:
                day = "오늘";
                break;
            case 1:
                day = "어제";
                break;
            case 2:
                day = "2일 전";
                break;
            case 3:
                day = "3일 전";
                break;
            default:
                day = standardDate;
                break;
        }
        return day;
    }

    public static String dayForChattingList(long unixTime) {
        String day = unixToTimeAndDayKR(unixTime);

        long dateOfCreate = (unixTime + 9 * 60 * 60 * 1000) / (24 * 60 * 60 * 1000);
        long dateOfNow = (getUnixTimeLong() + 9 * 60 * 60 * 1000) / (24 * 60 * 60 * 1000);
        long difference = dateOfNow - dateOfCreate;

        int diff = (int) difference;

        switch (diff) {
            case 0:
                day = getUnixToTime(unixTime);
                break;
            case 1:
                day = "어제";
                break;
            case 2:
                day = "2일 전";
                break;
            case 3:
                day = "3일 전";
                break;
            default:
                day = unixToDayKR(unixTime);
                break;
        }
        return day;
    }

    public static String dayForCommunityList(long unixTime) {
        String day = unixToTimeAndDayKR(unixTime);

        long dateOfCreate = (unixTime + 9 * 60 * 60 * 1000) / (24 * 60 * 60 * 1000);
        long dateOfNow = (getUnixTimeLong() + 9 * 60 * 60 * 1000) / (24 * 60 * 60 * 1000);
        long difference = dateOfNow - dateOfCreate;

        long minuteOfCreate = (unixTime + 9 * 60 * 60 * 1000) / (60 * 1000);
        long minuteOfNow = (getUnixTimeLong() + 9 * 60 * 60 * 1000) / (60 * 1000);
        long minuteDifference = minuteOfNow - minuteOfCreate;
        long displayMinute = minuteDifference + 1;

        if (difference == 0){
            if (minuteDifference < 60){
                day = displayMinute + "분 전";
            } else {
                day = getUnixToTime(unixTime);
            }
        } else {
            day = unixToTimeAndDayKR(unixTime);
        }
        return day;
    }

    public static String dayForNotice(long unixTime) {
        String day = unixToTimeAndDayKR(unixTime);

        long dateOfCreate = (unixTime + 9 * 60 * 60 * 1000) / (24 * 60 * 60 * 1000);
        long dateOfNow = (getUnixTimeLong() + 9 * 60 * 60 * 1000) / (24 * 60 * 60 * 1000);
        long difference = dateOfNow - dateOfCreate;

        long minuteOfCreate = (unixTime + 9 * 60 * 60 * 1000) / (60 * 1000);
        long minuteOfNow = (getUnixTimeLong() + 9 * 60 * 60 * 1000) / (60 * 1000);
        long minuteDifference = minuteOfNow - minuteOfCreate;
        long displayMinute = minuteDifference + 1;

        if (difference == 0){
            if (minuteDifference < 60){
                day = displayMinute + "분 전";
            } else {
                day = getUnixToTime(unixTime);
            }
        } else {
            day = unixToDayKR(unixTime);
        }
        return day;
    }

    public static String unixToTimeAndDayKR(long unixTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("M월 dd일  a hh:mm", Locale.KOREA);
        Date date = new Date(unixTime);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        return dateResult;
    }

    public static String unixToDayKR(long unixTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("M월 dd일", Locale.KOREA);
        Date date = new Date(unixTime);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        return dateResult;
    }

    public static String convertUnixTimeToDate(long unixTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Date date = new Date(unixTime);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        return dateResult;
    }

}
