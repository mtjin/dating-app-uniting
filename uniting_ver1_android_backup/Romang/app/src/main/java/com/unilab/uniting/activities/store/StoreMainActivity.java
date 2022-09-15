package com.unilab.uniting.activities.store;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.setprofile.SetProfile_4UniversityCertification;
import com.unilab.uniting.activities.setprofile.SetProfile_5Invite;
import com.unilab.uniting.model.Dia;
import com.unilab.uniting.model.Purchase;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.RemoteConfig;

public class StoreMainActivity extends BasicActivity implements BillingProcessor.IBillingHandler {

    static final String TAG = "STORE_TAG";

    private RelativeLayout mLoaderLayout;
    private TextView mDiaTextView;
    private LinearLayout mCertificateLayout;
    private LinearLayout mInviteLayout;
    private LinearLayout mBack;
    private BillingProcessor mBillingProcessor;
    private LinearLayout mPurchase0Layout;
    private LinearLayout mPurchase1Layout;
    private LinearLayout mPurchase2Layout;
    private LinearLayout mPurchase3Layout;
    private LinearLayout mPurchase4Layout;
    private LinearLayout mPurchase5Layout;
    private LinearLayout mEvent0Layout;
    private LinearLayout mEvent1Layout;
    private LinearLayout mEvent2Layout;
    private LinearLayout mEvent3Layout;
    private LinearLayout mEvent4Layout;
    private LinearLayout mEvent5Layout;
    private TextView mEvent0TextView;
    private TextView mEvent1TextView;
    private TextView mEvent2TextView;
    private TextView mEvent3TextView;
    private TextView mEvent4TextView;
    private TextView mEvent5TextView;

    private ImageView mDiaEventImgView;
    private LinearLayout mDiaEventLayout;
    private Button mCloseLongBtn;
    private Button mCloseBtn;

    private final String item0 = "uniting.dia30";
    private final String item1 = "uniting.dia50";
    private final String item2 = "uniting.dia100";
    private final String item3 = "uniting.dia300";
    private final String item4 = "uniting.dia500";
    private final String item5 = "uniting.dia1000";

    private boolean isEventOn = false;
    private int eventPercent = 0;
    private int event50 = 0;
    private int event100 = 0;
    private int event300 = 0;
    private int event500 = 0;
    private int event1000 = 0;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_main);

        init();
        checkEvent();
        setOnClickListener();
        getDiaData();
        setEventNotice();


        //인앱결제 초기화 및 생성
        mBillingProcessor = new BillingProcessor(this, getString(R.string.in_app_license_key), this);
        mBillingProcessor.initialize();

    }

    private void setEventNotice() {
        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid, MODE_PRIVATE);
        long dia_event = pref.getLong("dia_event", 0);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference spaceRef = storageRef.child("Notice/event_dia.png");

        spaceRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(StoreMainActivity.this)
                                .load(uri.toString())
                                .into(mDiaEventImgView);

                        if (dia_event < DateUtil.getUnixTimeLong() - 7 * DateUtil.dayInMilliSecond) {
                            mDiaEventLayout.setVisibility(View.VISIBLE);
                        } else {
                            mDiaEventLayout.setVisibility(View.GONE);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    private void getDiaData(){
        //날짜오름차순으로 최신하트개수 하나 불러오게함
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").orderBy(FirebaseHelper.diaId, Query.Direction.DESCENDING).limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            Dia dia = doc.toObject(Dia.class);
                            mDiaTextView.setText(dia.getCurrentDia() + "");
                        }
                    }
                });
    }

    private void init(){
        mDiaTextView = findViewById(R.id.store_tv_heart);
        mCertificateLayout = findViewById(R.id.store_linear_certificate);
        mInviteLayout = findViewById(R.id.store_linear_invite);
        mBack = findViewById(R.id.toolbar_back);
        mPurchase0Layout = findViewById(R.id.store_linear_purchase_4900);
        mPurchase1Layout = findViewById(R.id.store_linear_purchase_7500);
        mPurchase2Layout = findViewById(R.id.store_linear_purchase_14000);
        mPurchase3Layout = findViewById(R.id.store_linear_purchase_37000);
        mPurchase4Layout = findViewById(R.id.store_linear_purchase_59000);
        mPurchase5Layout = findViewById(R.id.store_linear_purchase_105000);
        mEvent0Layout = findViewById(R.id.store_linear_event_4900);
        mEvent1Layout = findViewById(R.id.store_linear_event_7500);
        mEvent2Layout = findViewById(R.id.store_linear_event_14000);
        mEvent3Layout = findViewById(R.id.store_linear_event_37000);
        mEvent4Layout = findViewById(R.id.store_linear_event_59000);
        mEvent5Layout = findViewById(R.id.store_linear_event_105000);
        mEvent0TextView = findViewById(R.id.store_tv_event_4900);
        mEvent1TextView = findViewById(R.id.store_tv_event_7500);
        mEvent2TextView = findViewById(R.id.store_tv_event_14000);
        mEvent3TextView = findViewById(R.id.store_tv_event_37000);
        mEvent4TextView = findViewById(R.id.store_tv_event_59000);
        mEvent5TextView = findViewById(R.id.store_tv_event_105000);
        mDiaEventLayout = findViewById(R.id.store_linear_dia_event);
        mCloseLongBtn = findViewById(R.id.store_btn_close_long);
        mCloseBtn = findViewById(R.id.store_btn_close);
        mDiaEventImgView = findViewById(R.id.store_iv_dia_event);

        //로딩, 툴바 레이아웃
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);
    }

    private void setOnClickListener(){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.store_linear_purchase_4900:
                        purchaseItem(item0);
                        break;
                    case R.id.store_linear_purchase_7500:
                        purchaseItem(item1);
                        break;
                    case R.id.store_linear_purchase_14000:
                        purchaseItem(item2);
                        break;
                    case R.id.store_linear_purchase_37000:
                        purchaseItem(item3);
                        break;
                    case R.id.store_linear_purchase_59000:
                        purchaseItem(item4);
                        break;
                    case R.id.store_linear_purchase_105000:
                        purchaseItem(item5);
                        break;
                }
            }
        };

        mPurchase0Layout.setOnClickListener(onClickListener);
        mPurchase1Layout.setOnClickListener(onClickListener);
        mPurchase2Layout.setOnClickListener(onClickListener);
        mPurchase3Layout.setOnClickListener(onClickListener);
        mPurchase4Layout.setOnClickListener(onClickListener);
        mPurchase5Layout.setOnClickListener(onClickListener);

        mInviteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StoreMainActivity.this, SetProfile_5Invite.class));
            }
        });

        mCertificateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StoreMainActivity.this, SetProfile_4UniversityCertification.class));
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mCloseLongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putLong("dia_event", DateUtil.getUnixTimeLong());
                editor.apply();
                mDiaEventLayout.setVisibility(View.GONE);
            }
        });

        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDiaEventLayout.setVisibility(View.GONE);
            }
        });

    }


    private void checkEvent() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d(TAG, "Config params updated: " + updated);
                            //ABTest 저장
                            String eventOn = mFirebaseRemoteConfig.getString(RemoteConfig.Store.isEventOn);
                            switch (eventOn) {
                                case "on":
                                    isEventOn = true;
                                    break;
                                default:
                                    isEventOn = false;
                                    break;
                            }

                            String percent = mFirebaseRemoteConfig.getString(RemoteConfig.Store.eventPercent);
                            try {
                                eventPercent = Integer.parseInt(percent);
                            } catch (NumberFormatException e) {
                                eventPercent = 0;
                            }

                            if (eventPercent == 0 ){
                                isEventOn = false;
                            } else {
                                event50 = (int) (0.5 * eventPercent);
                                event100 = eventPercent;
                                event300 = 3 * eventPercent;
                                event500 = 5 * eventPercent;
                                event1000 = 10 * eventPercent;
                            }
                        }
                        updateUI();
                    }
                });
    }

    private void updateUI(){
        if (isEventOn){
            mEvent1Layout.setVisibility(View.VISIBLE);
            mEvent2Layout.setVisibility(View.VISIBLE);
            mEvent3Layout.setVisibility(View.VISIBLE);
            mEvent4Layout.setVisibility(View.VISIBLE);
            mEvent5Layout.setVisibility(View.VISIBLE);
            mEvent1TextView.setText("+" + event50);
            mEvent2TextView.setText("+" + event100);
            mEvent3TextView.setText("+" + event300);
            mEvent4TextView.setText("+" + event500);
            mEvent5TextView.setText("+" + event1000);
        }

    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        // * 구매 완료시 호출
        // productId: 구매한 sku (ex) no_ads)
        // details: 결제 관련 정보
        SkuDetails skuDetails = mBillingProcessor.getPurchaseListingDetails(productId);

        String timeStamp = DateUtil.getTimeStampUnix();
        String date = DateUtil.getDateSec();
        int dia = 0;
        switch (productId){
            case item0:
                dia = 30;
                break;
            case item1:
                dia = 50;
                if (isEventOn){
                    dia = 50 + event50;
                }
                break;
            case item2:
                dia = 100;
                if (isEventOn){
                    dia = 50 + event100;
                }
                break;
            case item3:
                dia = 300;
                if (isEventOn){
                    dia = 300 + event300;
                }
                break;
            case item4:
                dia = 500;
                if (isEventOn){
                    dia = 500 + event500;
                }
                break;
            case item5:
                dia = 1000;
                if (isEventOn){
                    dia = 1000 + event1000;
                }
                break;
        }


        Purchase purchase = new Purchase(FirebaseHelper.mUid, date, productId, details.purchaseInfo.purchaseData.purchaseToken, details.purchaseInfo.purchaseData.orderId, dia, skuDetails.priceLong, FirebaseHelper.android);
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("PurchaseDia").document(timeStamp)
                .set(purchase)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        mLoaderLayout.setVisibility(View.GONE);
                        if(mBillingProcessor.isPurchased(productId)){
                            // 구매하였으면 소비하여 없애기
                             mBillingProcessor.consumePurchase(productId);
                        }

                        Bundle bundle = new Bundle();
                        bundle.putInt(LogData.dia, purchase.getDia());
                        bundle.putLong(LogData.price, purchase.getPrice());
                        bundle.putString(LogData.location, "store");
                        LogData.customLog(LogData.purchaseDia, bundle, StoreMainActivity.this);

                        Toast.makeText(StoreMainActivity.this, "결제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        mLoaderLayout.setVisibility(View.GONE);
                        Toast.makeText(StoreMainActivity.this, "결제 중 오류가 발생했습니다. 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();

                        Bundle bundle = new Bundle();
                        bundle.putInt(LogData.dia, 0);
                        bundle.putString(LogData.errorCode, "purchaseDiaError");
                        LogData.customLog(LogData.purchaseError, bundle, StoreMainActivity.this);
                    }
                });
    }

    @Override
    public void onPurchaseHistoryRestored() {
        // * 구매 정보가 복원되었을때 호출
        // bp.loadOwnedPurchasesFromGoogle() 하면 호출 가능
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        // * 구매 오류시 호출
        // errorCode == Constants.BILLING_RESPONSE_RESULT_USER_CANCELED 일때는
        // 사용자가 단순히 구매 창을 닫은것임으로 이것 제외하고 핸들링하기.
        Log.d("test001","onBillingError : "+ error + ", Error Code : " + errorCode);
        Log.d("test000", errorCode+"");
        mLoaderLayout.setVisibility(View.GONE);

        if (errorCode != Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();

            Bundle bundle = new Bundle();
            bundle.putInt(LogData.dia, 0);
            bundle.putInt(LogData.errorCode, errorCode);
            LogData.customLog(LogData.purchaseError, bundle, StoreMainActivity.this);

        } else {
            Toast.makeText(this, "결제를 취소하였습니다.", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onBillingInitialized() {
        // * 처음에 초기화됬을때.
        SkuDetails mProduct = mBillingProcessor.getPurchaseListingDetails(item0);

        Log.d("test002", mProduct + "init");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!mBillingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onDestroy() {
        if (mBillingProcessor != null) {
            mBillingProcessor.release();
        }
        super.onDestroy();
    }

    private void purchaseItem(String productId) {
        mLoaderLayout.setVisibility(View.VISIBLE);

        //구글플레이콘솔 사용할 수 있는 상태인지 체크
        boolean isOneTimePurchaseSupported = mBillingProcessor.isOneTimePurchaseSupported();

        if(mBillingProcessor.isPurchased(productId)){
            // 구매하였으면 소비하여 없앤 후 다시 구매하게 하는 로직. 만약 1번 구매 후 계속 이어지게 할 것이면 아래 함수는 주석처리.
            mBillingProcessor.consumePurchase(productId);
        }

        Log.d("test003", isOneTimePurchaseSupported + "");
        Log.d("test004", mBillingProcessor.isPurchased(productId) + "");
//        if(isOneTimePurchaseSupported) {
//            // launch payment flow
//            return;
//        }

        mBillingProcessor.purchase(this, productId);
    }

    @Override
    public void onBackPressed() {
        if(mLoaderLayout.getVisibility() == View.VISIBLE){
            Toast.makeText(this, "결제가 진행중입니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
          return;
        }

        super.onBackPressed();
    }
}
