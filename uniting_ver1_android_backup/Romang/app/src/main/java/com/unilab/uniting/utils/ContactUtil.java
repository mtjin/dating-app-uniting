package com.unilab.uniting.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class ContactUtil {

    private Context context;

    public ContactUtil(Context context){
        this.context = context;
    }

    /**
     * 내장 연락처 받기
     * ID , 이름 , 번호 추출
     * @return
     */
    public ArrayList<String> getContactList() {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID, // 연락처 ID -> 사진 정보 가져오는데 사용
                ContactsContract.CommonDataKinds.Phone.NUMBER,        // 연락처
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME }; // 연락처 이름.

        String[] selectionArgs = null;

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        Cursor contactCursor = context.getContentResolver().query(uri, projection, null, selectionArgs, sortOrder);

        ArrayList<String> contactList = new ArrayList<String>();

        if (contactCursor.moveToFirst()) {
            do {
                String phonenumber = contactCursor.getString(1).replaceAll("-","");
                if(phonenumber.startsWith("+82")){
                    phonenumber = phonenumber.replace("+82", "0"); // +8210xxxxyyyy 로 시작되는 번호
                }
                contactList.add(phonenumber);

            } while (contactCursor.moveToNext());
        }

        return contactList;

    }

    public ArrayList<String> simplifyContactList(ArrayList<String> contactList) {

        ArrayList<String> newContactList = new ArrayList<String>();

        for(String contact: contactList){
            String phonenumber = contact.replaceAll("-","");
            if(phonenumber.startsWith("+82")){
                phonenumber = phonenumber.replace("+82", "0"); // +8210xxxxyyyy 로 시작되는 번호
            }

            newContactList.add(phonenumber);
        }
        return newContactList;

    }
}
