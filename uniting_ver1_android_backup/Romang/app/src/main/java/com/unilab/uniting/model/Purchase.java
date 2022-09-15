package com.unilab.uniting.model;

import java.io.Serializable;

public class Purchase implements Serializable {
    private String uid;
    private String date;
    private String productId;
    private String purchaseToken;
    private String orderId;
    private int dia;
    private long price;
    private String device;


    public Purchase() {
    }


    public Purchase(String uid, String date, String productId, String purchaseToken, String orderId, int dia, long price, String device) {
        this.uid = uid;
        this.date = date;
        this.productId = productId;
        this.purchaseToken = purchaseToken;
        this.orderId = orderId;
        this.dia = dia;
        this.price = price;
        this.device = device;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}



