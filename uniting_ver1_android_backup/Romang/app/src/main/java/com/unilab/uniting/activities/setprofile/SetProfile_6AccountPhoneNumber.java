package com.unilab.uniting.activities.setprofile;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.unilab.uniting.R;
import com.unilab.uniting.model.Nice;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.FirebaseHelper;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class SetProfile_6AccountPhoneNumber extends BasicActivity {

    private WebView mWebView;
    private static final String URL_INFO = "https://www.uniting.kr/uniting_android_main.php"; //휴대폰본인인증 호출하는 URL 입력;

    private TextView mToolbarTitleTextView;
    private LinearLayout mBack;
    private final Handler handler = new Handler();

    private String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_signup_3_certification);

        init();
        setOnClickListener();

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

    private class AndroidBridge {
        @JavascriptInterface
        public void setMessage(final String dataJson) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    Gson gson = new Gson();
                    Nice data = gson.fromJson(dataJson, Nice.class);
                    phoneNumber = data.getPhoneNumber();
                    done();

                }
            });
        }
    }

    private void done(){
        Map<String, String> PhoneNumberData = new HashMap<>();
        PhoneNumberData.put(FirebaseHelper.phoneNumber, phoneNumber);
        WriteBatch batch = FirebaseHelper.db.batch();
        batch.update(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), FirebaseHelper.phoneNumber, phoneNumber);
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("PhoneNumber").document(FirebaseHelper.mUid), PhoneNumberData);
        batch.commit();

    }


    @Override
    public void onBackPressed() {
        if (mWebView.getOriginalUrl() != null && mWebView.getOriginalUrl().equalsIgnoreCase(URL_INFO)) {
            super.onBackPressed();
        } else if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }

    }

    private void init() {
        mBack = findViewById(R.id.toolbar_back);
        mBack.setVisibility(View.VISIBLE);
        mToolbarTitleTextView = findViewById(R.id.toolbar_title);
        mToolbarTitleTextView.setText("전화번호 인증");

    }

    private void setOnClickListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private class DemoWebViewClient extends WebViewClient {

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

