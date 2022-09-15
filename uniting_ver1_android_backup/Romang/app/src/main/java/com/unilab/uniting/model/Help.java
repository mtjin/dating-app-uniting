package com.unilab.uniting.model;

public class Help {
    private String title; //어플로 초대한 사람
    private String content; //어플에 초대된 사람 (코드를 사용한 사람)

    public Help() {
    }

    public Help(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
