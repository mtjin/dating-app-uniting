package com.unilab.uniting.activities.launch.signup;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.unilab.uniting.R;
import com.unilab.uniting.fragments.dialog.DialogOkNoFragment;
import com.unilab.uniting.fragments.launch.DialogCertificationBackFragment;
import com.unilab.uniting.model.DIModel;
import com.unilab.uniting.model.Nice;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.RemoteConfig;
import com.unilab.uniting.utils.Strings;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LaunchSignup_3Certification extends BasicActivity implements DialogCertificationBackFragment.CertificationListener, DialogOkNoFragment.DialogSignUpBackListener {

    private WebView mWebView;
    private static final String URL_INFO = "https://www.uniting.kr/uniting_android_main.php"; //휴대폰본인인증 호출하는 URL 입력;

    private static final String ban = "ban";
    private static final String withdraw = "withdraw";
    private static final String active = "active";
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    //로딩중 레이아웃
    private RelativeLayout mLoaderLayout;

    private TextView mToolbarTitleTextView;
    private LinearLayout mBack;
    private FrameLayout mOkLayout;
    private LinearLayout mLoadingBackgroundLayout;
    private Button mNextBtn;
    private final Handler handler = new Handler();

    private String name = "";
    private String birth = "";
    private String gender = "";
    private String phoneNumber = "";
    private String di = "";
    private int ageInt = 2020;
    private String age = "";
    private String nationality = "";
    private long dateOfWithdraw = 0;
    private String membership = "";
    private String uid = "";
    private String birthYear = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_signup_3_certification);

        setLogData();
        init();
        setOnClickListener();
        setWebView();

    }

    private void setWebView(){
        mWebView = (WebView) findViewById(R.id.signup_certification_wv);

        //웹뷰의 설정을 다음과 같이 맞춰주시기 바랍니다.
        mWebView.getSettings().setJavaScriptEnabled(true);    //필수설정(true)
        mWebView.getSettings().setDomStorageEnabled(true);        //필수설정(true)
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);    //필수설정(true)

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);

        WebChromeClient testChromeClient = new WebChromeClient();
        mWebView.setWebChromeClient(testChromeClient);

        AndroidBridge androidBridge = new AndroidBridge();
        mWebView.addJavascriptInterface(androidBridge,"android");

        /**
         !필수사항!

         웹뷰 내 앱링크를 사용하려면 WebViewClient를 반드시 설정하여 주시기바랍니다. (하단 DemoWebViewClient 참고)
         **/
        mWebView.setWebViewClient(new DemoWebViewClient());

        mWebView.loadUrl(URL_INFO);
    }

    private void setLogData(){
        LogData.eventLog(LogData.SignUp3_Certi, this);
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.sign_up_progress, 3);
        LogData.customLog(LogData.SignUp,  bundle, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoadingBackgroundLayout.setVisibility(View.GONE);
        mLoaderLayout.setVisibility(View.GONE);

        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid, MODE_PRIVATE);
        String phoneNumber = pref.getString(FirebaseHelper.phoneNumber, "");

        if (phoneNumber.equals("")){
            mWebView.setVisibility(View.VISIBLE);
            mOkLayout.setVisibility(View.GONE);
        } else {
            mWebView.setVisibility(View.GONE);
            mOkLayout.setVisibility(View.VISIBLE);
        }

    }

    private void init() {
        mBack = findViewById(R.id.toolbar_back);
        mOkLayout = findViewById(R.id.signup_certification_linear_ok);
        mNextBtn = findViewById(R.id.signup_certification_btn_ok);
        mBack.setVisibility(View.VISIBLE);
        mToolbarTitleTextView = findViewById(R.id.toolbar_title);
        mToolbarTitleTextView.setText("본인 인증");

        //로딩중
        mLoadingBackgroundLayout = findViewById(R.id.signup_certification_linear_loading);
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);

    }


    private void setOnClickListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LaunchSignup_3Certification.this, LaunchSignup_4Profile.class));
            }
        });
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setMessage(final String dataJson) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    mLoadingBackgroundLayout.setVisibility(View.VISIBLE);
                    mLoaderLayout.setVisibility(View.VISIBLE);

                    Gson gson = new Gson();
                    Nice data = gson.fromJson(dataJson, Nice.class);
                    Log.d("test000", data.getDI());
                    name = data.getName();
                    birth = data.getBirth();
                    phoneNumber = data.getPhoneNumber();
                    di = data.getDI();

                    try {
                        name = URLDecoder.decode(data.getName(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    gender = "남자";
                    if (data.getGender().equals("0")){
                        gender = "여자";
                    }

                    nationality = "한국인";
                    if (data.getNationality().equals("1")){
                        nationality = "외국인";
                    }

                    di = di.replaceAll("[^a-zA-Z0-9]", "");

                    ageInt = getAge(data.getBirth());
                    age = String.valueOf(ageInt);
                    birthYear = getbirthYear(data.getBirth());

                    if(ageInt > 2000) {
                        mLoadingBackgroundLayout.setVisibility(View.GONE);
                        mLoaderLayout.setVisibility(View.GONE);
                        Toast.makeText(LaunchSignup_3Certification.this, "오류 발생. 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
                        LaunchSignup_3Certification.super.onBackPressed();
                        return;
                    }

                    if(ageInt > RemoteConfig.standardAge) {
                        eventLogOldAge();
                        //withdraw("유니팅은 "+ standardAge +"세까지만 가입 가능합니다.");
                        return;
                    }

                    checkDI();

                }
            });
        }
    }

    private void withdraw(String message){
        Map<String, Object> withdrawData = new HashMap<>();
        withdrawData.put(FirebaseHelper.membership, LaunchUtil.Withdraw);
        withdrawData.put(FirebaseHelper.dateOfWithdraw, 0);
        withdrawData.put(FirebaseHelper.uid, FirebaseHelper.mUid);

        mLoaderLayout.setVisibility(View.VISIBLE);
        mLoadingBackgroundLayout.setVisibility(View.VISIBLE);
        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), withdrawData, SetOptions.merge());
        batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), withdrawData, SetOptions.merge());
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mLoaderLayout.setVisibility(View.GONE);
                mLoadingBackgroundLayout.setVisibility(View.GONE);
                if (!message.equals("")){
                    Toast.makeText(LaunchSignup_3Certification.this, message, Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(LaunchSignup_3Certification.this, LaunchSignup_0.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoaderLayout.setVisibility(View.GONE);
                mLoadingBackgroundLayout.setVisibility(View.GONE);
                Toast.makeText(LaunchSignup_3Certification.this, message + "\n 탈퇴 처리 오류. 고객센터로 문의주세요.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LaunchSignup_3Certification.this, LaunchSignup_0.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void back() {
        mLoaderLayout.setVisibility(View.GONE);
        mLoadingBackgroundLayout.setVisibility(View.GONE);
        super.onBackPressed();
    }


    @Override
    public void stopSignUp(String from) {
        switch (from){
            case ban:
                withdraw("");
                break;
            case withdraw:
            case active:
                FirebaseHelper.auth.signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(LaunchSignup_3Certification.this, LaunchSignup_0.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    private void eventLogOldAge(){
        if(di == null || di.equals("")){
            return;
        }

        FirebaseHelper.db.collection("UserDI").document(di)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "가입");
                    } else {
                        LogData.eventLog("SignUpOver" + RemoteConfig.standardAge, LaunchSignup_3Certification.this);
                        Bundle params = new Bundle();
                        params.putInt("age", ageInt);
                        LogData.customLog(LogData.SignUpAge, params, LaunchSignup_3Certification.this);


                        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.apply();


                        String email = FirebaseHelper.mEmail;
                        String facebookUid = pref.getString(FirebaseHelper.facebookUid, "");
                        DIModel diModel = new DIModel(di, FirebaseHelper.mUid, facebookUid, "", email, LaunchUtil.Withdraw, "", 0);

                        WriteBatch batch = FirebaseHelper.db.batch();
                        batch.set(FirebaseHelper.db.collection("UserDI").document(di), diModel, SetOptions.merge());
                        batch.update(FirebaseHelper.db.collection("UserDI").document(di), "age", ageInt);
                        batch.commit();
                    }
                    withdraw("유니팅은 "+ RemoteConfig.standardAge +"세까지만 가입 가능합니다.");
                } else {
                    withdraw("유니팅은 "+ RemoteConfig.standardAge +"세까지만 가입 가능합니다.");
                }


            }
        });
    }


    private void checkDI(){
        mLoaderLayout.setVisibility(View.VISIBLE);
        mLoadingBackgroundLayout.setVisibility(View.VISIBLE);
         FirebaseHelper.db.collection("UserDI").document(di)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.get(FirebaseHelper.dateOfWithdraw) != null ){
                            try{
                                dateOfWithdraw = Long.parseLong(document.get(FirebaseHelper.dateOfWithdraw).toString());
                            }catch (Exception err){
                                dateOfWithdraw = 0;
                            }
                        }
                        if (document.get(FirebaseHelper.membership) != null ){
                            membership = (String) document.get(FirebaseHelper.membership);
                        }

                        if (document.get(FirebaseHelper.uid) != null ){
                            uid = (String) document.get(FirebaseHelper.uid);
                        }


                        if (membership.equals(LaunchUtil.Ban)) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Strings.EXTRA_CONTENT, "가입이 불가능한 회원입니다.");
                            bundle.putString(Strings.from, ban);
                            DialogCertificationBackFragment dialog = DialogCertificationBackFragment.getInstance();
                            dialog.setArguments(bundle);
                            dialog.show(getSupportFragmentManager(), DialogCertificationBackFragment.TAG_MEETING_DIALOG2);
                            return;
                        }

                        if(membership.equals(LaunchUtil.Withdraw)){
                            long unixTimeOfEnd = dateOfWithdraw + 3 * 24 * 60 * 60 * 1000; //3일뒤
                            if(unixTimeOfEnd < DateUtil.getUnixTimeLong()){
                                done();
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putString(Strings.EXTRA_CONTENT, "탈퇴 후 3일간 재가입할 수 없습니다.");
                                bundle.putString(Strings.from, withdraw);
                                DialogCertificationBackFragment dialog = DialogCertificationBackFragment.getInstance();
                                dialog.setArguments(bundle);
                                dialog.show(getSupportFragmentManager(), DialogCertificationBackFragment.TAG_MEETING_DIALOG2);
                            }
                            return;
                        }

                        if(!membership.equals(LaunchUtil.SignUp) && !membership.equals("")){
                            Bundle bundle = new Bundle();
                            bundle.putString(Strings.EXTRA_CONTENT, "이미 가입된 회원입니다.");
                            bundle.putString(Strings.from, active);
                            DialogCertificationBackFragment dialog = DialogCertificationBackFragment.getInstance();
                            dialog.setArguments(bundle);
                            dialog.show(getSupportFragmentManager(), DialogCertificationBackFragment.TAG_MEETING_DIALOG2);
                            return;
                        }

                        done();

                    } else {
                        LogData.eventLog("SignUpUnder" + RemoteConfig.standardAge, LaunchSignup_3Certification.this);
                        Bundle params = new Bundle();
                        params.putInt("age", ageInt);
                        LogData.customLog(LogData.SignUpAge,  params, LaunchSignup_3Certification.this);

                        done();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "오류 발생. 오류 반복시 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
                    mLoaderLayout.setVisibility(View.GONE);
                    mLoadingBackgroundLayout.setVisibility(View.GONE);
                    LaunchSignup_3Certification.super.onBackPressed();
                }

            }
        });

    }

    private void done(){
        //User Property 설정
        String gender_custom = LogData.male;
        if(gender.equals("여자")){
            gender_custom = LogData.female;
        }

        Map<String,String> user_properties = new HashMap<>();
        user_properties.put(LogData.gender_custom, gender_custom);
        user_properties.put(LogData.birth_year, birthYear);
        LogData.setUserProperties(user_properties, LaunchSignup_3Certification.this);


        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(FirebaseHelper.name, name);
        editor.putString(FirebaseHelper.birth, birth);
        editor.putString(FirebaseHelper.gender, gender);
        editor.putString(FirebaseHelper.phoneNumber, phoneNumber);
        editor.putString(FirebaseHelper.di, di);
        editor.putString(FirebaseHelper.nationality, nationality);
        editor.putInt(FirebaseHelper.birthYear, Integer.parseInt(birthYear));
        editor.apply();


        String email = FirebaseHelper.mEmail;
        String facebookUid = pref.getString(FirebaseHelper.facebookUid, "");
        DIModel diModel = new DIModel(di, FirebaseHelper.mUid, facebookUid, "", email, LaunchUtil.SignUp, "", 0);

        Map<String,Object> blockData = new HashMap<>();
        blockData.put(FirebaseHelper.isFacebookBlocked, false);
        blockData.put(FirebaseHelper.isContactBlocked, false);
        blockData.put(FirebaseHelper.isUniversityBlocked, false);
        blockData.put(FirebaseHelper.facebookList, new ArrayList<>());
        blockData.put(FirebaseHelper.contactList, new ArrayList<>());
        blockData.put(FirebaseHelper.myFacebookUid, MyProfile.getUser().getFacebookUid());
        blockData.put(FirebaseHelper.myPhoneNumber, phoneNumber);
        blockData.put(FirebaseHelper.blockMeList, new ArrayList<>());
        blockData.put(FirebaseHelper.blockUserList, new ArrayList<>());
        blockData.put(FirebaseHelper.university, "");

        Map<String, String> PhoneNumberData = new HashMap<>();
        PhoneNumberData.put(FirebaseHelper.phoneNumber, phoneNumber);

        Map<String, Object> progressData = new HashMap<>();
        progressData.put(FirebaseHelper.signUpProgress, Strings.SignUpPrgoress.Profile);
        progressData.put(FirebaseHelper.di, di);
        progressData.put(FirebaseHelper.gender, gender);
        progressData.put(FirebaseHelper.birthYear, Integer.parseInt(birthYear));

        MyProfile.getOurInstance().setSignUpProgress( Strings.SignUpPrgoress.Profile);
        MyProfile.getOurInstance().setDi(di);
        MyProfile.getOurInstance().setGender(gender);
        MyProfile.getOurInstance().setBirthYear(Integer.parseInt(birthYear));

        Map<String, String> adminData = new HashMap<>();
        adminData.put(FirebaseHelper.signUpProgress, Strings.SignUpPrgoress.Profile);
        adminData.put(FirebaseHelper.di, di);
        adminData.put(FirebaseHelper.name, name);
        adminData.put(FirebaseHelper.gender, gender);
        adminData.put(FirebaseHelper.birth, birth);
        adminData.put(FirebaseHelper.nationality, nationality);
        adminData.put(FirebaseHelper.phoneNumber, phoneNumber);

        mLoaderLayout.setVisibility(View.VISIBLE);
        mLoadingBackgroundLayout.setVisibility(View.VISIBLE);


        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set(FirebaseHelper.db.collection("UserDI").document(di), diModel);
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("PhoneNumber").document(FirebaseHelper.mUid), PhoneNumberData);
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Block").document(FirebaseHelper.mUid), blockData);
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid),progressData, SetOptions.merge());
        batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid),adminData, SetOptions.merge());
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(LaunchSignup_3Certification.this, LaunchSignup_4Profile.class));

                Map<String,String> userProps = new HashMap<>();
                userProps.put(LogData.sign_up_progress, Strings.SignUpPrgoress.Profile);
                LogData.setUserProperties(userProps, LaunchSignup_3Certification.this);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "오류 발생. 오류 반복시 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
                LaunchSignup_3Certification.super.onBackPressed();

            }
        });

    }

    private int getAge (String birth) {
        if(birth.length() < 5 ){
            return 2021;
        }

        String year = birth.substring(0,4);
        int yearInt = 0;

        try {
            yearInt = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            yearInt = 0;
        }

        int age = DateUtil.getYear() - yearInt + 1;
        return age;
    }


    private String getbirthYear(String birth) {
        if(birth.length() < 5 ){
            return "2021";
        }

        String year = birth.substring(0,4);
        int yearInt = 0;

        try {
            yearInt = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            yearInt = 0;
        }

        return String.valueOf(yearInt);
    }


    @Override
    public void onBackPressed() {
        if (mWebView.getOriginalUrl() != null && mWebView.getOriginalUrl().equalsIgnoreCase(URL_INFO)) {
            Bundle bundle = new Bundle();
            bundle.putString(Strings.title, "이전 단계로 돌아가시겠어요?");
            bundle.putString(Strings.content, "1분이면 가입이 완료됩니다.");
            bundle.putString(Strings.ok, "이전 단계로");
            bundle.putString(Strings.no, "가입 계속하기");
            DialogOkNoFragment dialog = DialogOkNoFragment.getInstance();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), DialogOkNoFragment.TAG_EVENT_DIALOG);
        } else if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(Strings.title, "이전 단계로 돌아가시겠어요?");
            bundle.putString(Strings.content, "1분이면 가입이 완료됩니다.");
            bundle.putString(Strings.ok, "이전 단계로");
            bundle.putString(Strings.no, "가입 계속하기");
            DialogOkNoFragment dialog = DialogOkNoFragment.getInstance();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), DialogOkNoFragment.TAG_EVENT_DIALOG);
        }


    }



    public class DemoWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //웹뷰 내 표준창에서 외부앱(통신사 인증앱)을 호출하려면 intent:// URI를 별도로 처리해줘야 합니다.
            //다음 소스를 적용 해주세요.
            if (url.startsWith("intent://")) {
                Intent intent = null;
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent != null) {
                        //앱실행
                        startActivity(intent);
                    }
                } catch (URISyntaxException e) {
                    //URI 문법 오류 시 처리 구간

                } catch (ActivityNotFoundException e) {
                    String packageName = intent.getPackage();
                    if (!packageName.equals("")) {
                        // 앱이 설치되어 있지 않을 경우 구글마켓 이동
                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                        Toast.makeText(LaunchSignup_3Certification.this, "PASS 앱이 없으신 경우 하단의 문자 인증 버튼을 클릭해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                //return  값을 반드시 true로 해야 합니다.
                return true;

            } else if (url.startsWith("https://play.google.com/store/apps/details?id=") || url.startsWith("market://details?id=")) {
                //표준창 내 앱설치하기 버튼 클릭 시 PlayStore 앱으로 연결하기 위한 로직
                Uri uri = Uri.parse(url);
                String packageName = uri.getQueryParameter("id");
                if (packageName != null && !packageName.equals("")) {
                    // 구글마켓 이동
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                }
                //return  값을 반드시 true로 해야 합니다.
                return true;
            }

            //return  값을 반드시 false로 해야 합니다.
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }

}
