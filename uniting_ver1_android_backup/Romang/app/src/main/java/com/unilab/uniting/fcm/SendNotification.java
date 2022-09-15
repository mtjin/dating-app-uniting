package com.unilab.uniting.fcm;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.model.CommunityPost;
import com.unilab.uniting.model.Invite;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.model.User;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNotification {
    final static String TAG = "SendNotificationT";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static void sendNotification(String regToken, String title, String message, Object object, String from){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... parms) {
                try {
                    Log.d(TAG, "상대방토큰 : " + regToken);
                    Log.d(TAG, "제목 : " + title);
                    Log.d(TAG, "메세지 : " + message);
                    String type = "";
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    //데이터 필드 담기
                    JSONObject dataJson = new JSONObject();
                    Gson gson =new Gson();
                    String data = gson.toJson(object); //객체JSON으로 변형해서 전달할거
                    dataJson.put("object", data);

                    //type담을 예정
                    if(object instanceof Meeting){
                        type = "meeting";
                        dataJson.put("type",type );
                    }else if(object instanceof CommunityPost){
                        type = "communityPost";
                        dataJson.put("type",type );
                    }else if(object instanceof Invite){
                        type = "invite";
                        dataJson.put("type",type );
                    }else if(object instanceof User){
                        type = "user";
                        dataJson.put("type",type );
                    }else if(object instanceof ChatRoom){
                        type = "chatting";
                        dataJson.put("type",type );
                    }

                    //title, body, from  담을 예정
                    dataJson.put("title", title);
                    dataJson.put("body", message);
                    dataJson.put("location", from);
                    //json에 puy
                    json.put("data", dataJson); //이건 데이터필드니깐 키필드값을 data로 해주어야한다.
                    Log.d(TAG, "노티 데이터 페이로드===> " + data);
                    Log.d(TAG, "노티  데이터타입 페이로드===> " + type);
                    Log.d("JSON" , json.toString());

                    //토큰 필드 담기
                    json.put("to", regToken);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + "AAAAZrRF5os:APA91bHb-iU8RFQJQSJvCMP79nzh4eeRmWvZDMQQg6-335WWN8aK1I9KeGEp74x-1bq-K_toStweUQ1L2hVjkK_IIbLg17X8Ft0vuo2aHgZ6eyr5ZIA_CKChFZ56IcsgLzDL6H_Lh5X5")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    Log.d("TAG", finalResponse);
                }catch (Exception e){
                    Log.d("error", e+"");
                }
                return  null;
            }
        }.execute();
    }
}
