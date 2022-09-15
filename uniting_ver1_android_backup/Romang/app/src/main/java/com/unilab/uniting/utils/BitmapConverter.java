package com.unilab.uniting.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class BitmapConverter {

    public static int MegaByte = 1000000;

    /*
     * String형을 BitMap으로 변환시켜주는 함수
     * */
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    /*
     * Bitmap을 String형으로 변환
     * */
    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }

    /*
     * Bitmap을 byte배열로 변환
     * */
    public static byte[] BitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return baos.toByteArray();
    }

    public static byte[] UriToByteArray(Uri uri) { //컴프레션까지 처리. (
        int compressQuality = 100;
        long size = new File(uri.getPath()).length();

        if (size < MegaByte) {
            compressQuality = 100;
        } else if (MegaByte <= size && size <= 18 * MegaByte) {
            compressQuality = 104 - (int)(long) (5 * size/ MegaByte);
        } else {
            compressQuality = 10;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, baos);
        return baos.toByteArray();
    }

    public static UploadTask getUploadTask(StorageReference ref, Uri uri){
        UploadTask uploadTask;
        long size = new File(uri.getPath()).length();
        if (size > BitmapConverter.MegaByte) { //1메가 바이트보다 큰 경우
            byte[] data = BitmapConverter.UriToByteArray(uri);
            uploadTask = ref.putBytes(data);
        } else { //1메가바이트보다 작은 경우 그대로 업로
            uploadTask = ref.putFile(uri);
        }

        return uploadTask;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
