package com.unilab.uniting.model;

import java.io.Serializable;

public class Fcm implements Serializable {
    private String toUid;
    private String title;
    private String body;
    private Object object;
    private String objectId;
    private String type;
    private String location;
    private String date;

    public Fcm() {
    }

    public Fcm(String toUid, String title, String body, Object object, String objectId, String type, String location, String date) {
        this.toUid = toUid;
        this.title = title;
        this.body = body;
        this.object = object;
        this.objectId = objectId;
        this.type = type;
        this.location = location;
        this.date = date;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}



