package com.unilab.uniting.activities.setprofile;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;
import com.unilab.uniting.R;
import com.unilab.uniting.fragments.setprofile.DialogBlockFacebookFragment;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.ContactUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetProfile_3BlockFriend extends BasicActivity implements DialogBlockFacebookFragment.CancelListener {

    //xml
    private LinearLayout mToolbarBack;
    private Switch mFacebookSwitch;
    private Switch mUniversitySwitch;
    private Switch mPhoneNumberSwitch;
    private TextView mFacebookTextView;
    private TextView mUniversityTextView;
    private TextView mPhoneNumberTextView;
    private Button mFacebookUpdateBtn;
    private Button mPhoneNumberUpdateBtn;
    private RelativeLayout mLoaderLayout; //??????????????????

    //??????, ??????
    final static String TAG = "SET_PROFILE_BLOCK";
    final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 11;
    ContactUtil contactUtil;

    //????????????
    CallbackManager mCallbackManager;

    private ArrayList<String> oldFacebookList = new ArrayList<>();
    private ArrayList<String> newFacebookList = new ArrayList<>();
    private String facebookFriendCount = "?????????";
    private boolean isFacebookBlocked = false;
    private boolean isFacebookListOutdated = false;
    private boolean isFacebookDataDownloaded = false;
    private boolean isFBLinkedAlready = false;

    private ArrayList<String> oldContactList = new ArrayList<>();
    private ArrayList<String> newContactList = new ArrayList<>();
    private String  contactCount = "?????????";
    private boolean isContactBlocked = false;
    private boolean isContactOutdated = false;

    private String  university = "??????";
    private boolean isUniversityBlocked = false;

    private boolean isFacebookPermitted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_3_block_friend);

        init();
        checkFBLinked();
        checkContact();
        getBlockData();
        setOnClickListener();
        checkPermission();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if ((accessToken != null && !accessToken.isExpired()) && isFacebookPermitted) {
            getFacebookFriends(accessToken, false);
            return;
        }
        updateUI();

    }

    private void checkPermission(){
        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
        isFacebookPermitted = pref.getBoolean(Strings.facebookPermission, false);
    }

    private void setPermission(){
        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Strings.facebookPermission, true);
        isFacebookPermitted = true;
        editor.commit();
    }

    private void init(){
        mToolbarBack = findViewById(R.id.toolbar_back);
        mFacebookSwitch = findViewById(R.id.setprofile_blockFriend_switch_blockFB);
        mUniversitySwitch = findViewById(R.id.setprofile_blockFriend_switch_blockUniversity);
        mPhoneNumberSwitch = findViewById(R.id.setprofile_blockFriend_switch_blockPhoneNumber);
        mFacebookTextView = findViewById(R.id.setprofile_blockFriend_tv_facebook_result);
        mUniversityTextView = findViewById(R.id.setprofile_blockFriend_tv_university_result);
        mPhoneNumberTextView = findViewById(R.id.setprofile_blockFriend_tv_contact_result);
        mFacebookUpdateBtn = findViewById(R.id.setprofile_blockFriend_btn_updateFB);
        mPhoneNumberUpdateBtn = findViewById(R.id.setprofile_blockFriend_btn_updatePhoneNumber);

        //?????? ????????????
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);
    }

    private void checkFBLinked(){
        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals(FirebaseHelper.providerID_FB)) { //?????? ????????? ??????????????? ?????????????????? ??????.
                Log.d("xx_xx_provider_info", "User is signed in with Facebook");
                isFBLinkedAlready = true;
            }
        }
    }

    private void checkContact(){
        if (ContextCompat.checkSelfPermission(SetProfile_3BlockFriend.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            contactUtil = new ContactUtil(SetProfile_3BlockFriend.this);
            newContactList = contactUtil.getContactList();
            contactCount = newContactList.size() + "???";
        } else {
            contactCount = "?????? ??????";
            mPhoneNumberTextView.setText("???????????? ????????? ??? ?????? ????????? ????????????.");
        }
    }

    private void updateUI(){
        mFacebookSwitch.setChecked(isFacebookBlocked);
        mUniversitySwitch.setChecked(isUniversityBlocked);
        mPhoneNumberSwitch.setChecked(isContactBlocked);

        Set<String> newFacebookSet = new HashSet<String>(newFacebookList);
        Set<String> oldFacebookSet = new HashSet<String>(oldFacebookList);
        Set<String> newContactSet = new HashSet<String>(newContactList);
        Set<String> oldContactSet = new HashSet<String>(oldContactList);

        if (oldFacebookSet.containsAll(newFacebookSet)) {
            isFacebookListOutdated = false;
        } else {
            isFacebookListOutdated = true;
        }

        if (oldContactSet.containsAll(newContactSet)) {
            isContactOutdated = false;
        } else {
            isContactOutdated = true;
        }

        String fbNeedUpdate = "";
        if(isFacebookListOutdated && isFacebookBlocked){
            mFacebookUpdateBtn.setVisibility(View.VISIBLE);
            mFacebookSwitch.setVisibility(View.GONE);
            fbNeedUpdate = ", ???????????? ??????";
        }else {
            mFacebookUpdateBtn.setVisibility(View.GONE);
            mFacebookSwitch.setVisibility(View.VISIBLE);
            fbNeedUpdate = "";
        }

        String contactNeedUpdate = "";
        if(isContactOutdated && isContactBlocked){
            mPhoneNumberUpdateBtn.setVisibility(View.VISIBLE);
            mPhoneNumberSwitch.setVisibility(View.GONE);
            contactNeedUpdate = ", ???????????? ??????";
        }else {
            mPhoneNumberUpdateBtn.setVisibility(View.GONE);
            mPhoneNumberSwitch.setVisibility(View.VISIBLE);
            contactNeedUpdate = "";
        }

        mFacebookTextView.setText("(?????????: "+ facebookFriendCount + ", " + setBlockToString(isFacebookBlocked) + fbNeedUpdate +")");
        mUniversityTextView.setText("(????????? ??????: " + university + ", " + setBlockToString(isUniversityBlocked) + ")");
        mPhoneNumberTextView.setText("(?????????: "+ contactCount + ", " + setBlockToString(isContactBlocked) + contactNeedUpdate +")");
    }

    private String setBlockToString(boolean isBlocked){
        if (isBlocked) {
            return "?????????";
        } else{
            return "?????? ?????????";
        }
    }

    //???????????? ??????
    private void getBlockData() {
        mLoaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Block").document(FirebaseHelper.mUid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());

                            //1. ?????? ?????? ?????? ??????
                            if (MyProfile.getUser().isOfficialUniversityChecked()) {
                                if (snapshot.get(FirebaseHelper.isUniversityBlocked) != null) {
                                    isUniversityBlocked = (boolean) snapshot.get(FirebaseHelper.isUniversityBlocked);
                                }

                                if (snapshot.get(FirebaseHelper.university) != null) {
                                    university = (String) snapshot.get(FirebaseHelper.university);
                                }
                            }

                            //2. ????????? ??????
                            if (snapshot.get(FirebaseHelper.isContactBlocked) != null) {
                                isContactBlocked = (boolean) snapshot.get(FirebaseHelper.isContactBlocked);

                            }

                            if (snapshot.get(FirebaseHelper.contactList) != null) {
                                oldContactList = (ArrayList<String>) snapshot.get(FirebaseHelper.contactList);

                            }

                            //3. ???????????? ??????
                            if (snapshot.get(FirebaseHelper.isFacebookBlocked) != null) {
                                isFacebookBlocked = (boolean) snapshot.get(FirebaseHelper.isFacebookBlocked);
                            }

                            if (snapshot.get(FirebaseHelper.facebookList) != null) {
                                oldFacebookList = (ArrayList<String>) snapshot.get(FirebaseHelper.facebookList);
                            }

                        } else {
                            Log.d(TAG, "Current data: null");
                        }

                        updateUI();
                        mLoaderLayout.setVisibility(View.GONE);
                    }
                });

    }


    private void setOnClickListener() {
        mToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //1. ???????????? ?????? ???????????? ?????? ??????
        mFacebookSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFacebookSwitch.isChecked()) {
                    permissionDialog();
                } else {
                    mLoaderLayout.setVisibility(View.VISIBLE);
                    Map<String, Object> facebookData = new HashMap<>();
                    facebookData.put(FirebaseHelper.isFacebookBlocked, false);
                    FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Block").document(FirebaseHelper.mUid).update(facebookData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "???????????? ????????? ?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                    isFacebookBlocked = false;
                                    updateUI();
                                    mLoaderLayout.setVisibility(View.GONE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SetProfile_3BlockFriend.this, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                    isFacebookBlocked = true;
                                    updateUI();
                                    mLoaderLayout.setVisibility(View.GONE);
                                }
                            });
                }

            }
        });

        mFacebookUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionDialog();
            }
        });


        //2. ???????????? ???????????? ?????? ??????
        mUniversitySwitch.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (!MyProfile.getUser().isOfficialUniversityChecked()) {
                    mUniversitySwitch.setChecked(false);
                    Toast.makeText(SetProfile_3BlockFriend.this, "????????? ????????? ???????????? ??????????????? ???????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mLoaderLayout.setVisibility(View.VISIBLE);
                Log.d("test123", "??????0");
                if (mUniversitySwitch.isChecked()) {
                    Map<String, Object> universityData = new HashMap<>();
                    universityData.put(FirebaseHelper.university, MyProfile.getUser().getUniversity());
                    universityData.put(FirebaseHelper.isUniversityBlocked, true);

                    Log.d("test123", "??????1");
                    FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Block").document(FirebaseHelper.mUid).update(universityData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    isUniversityBlocked = true;
                                    university = MyProfile.getUser().getUniversity();
                                    updateUI();
                                    Toast.makeText(SetProfile_3BlockFriend.this, "?????? ?????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                    mLoaderLayout.setVisibility(View.GONE);
                                    Log.d("test123", "??????2");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    isUniversityBlocked = false;
                                    updateUI();
                                    Toast.makeText(SetProfile_3BlockFriend.this, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                    mLoaderLayout.setVisibility(View.GONE);
                                    Log.d("test123", "??????3");

                                }
                            });
                } else {
                    Log.d("test123", "??????4");
                    Map<String, Object> universityData = new HashMap<>();
                    universityData.put(FirebaseHelper.isUniversityBlocked, false);
                    FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Block").document(FirebaseHelper.mUid).update(universityData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    isUniversityBlocked = false;
                                    updateUI();
                                    Toast.makeText(SetProfile_3BlockFriend.this, "?????? ?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                    mLoaderLayout.setVisibility(View.GONE);
                                    Log.d("test123", "??????5");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    isUniversityBlocked = true;
                                    updateUI();
                                    Toast.makeText(SetProfile_3BlockFriend.this, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                    mLoaderLayout.setVisibility(View.GONE);
                                    Log.d("test123", "??????6");
                                }
                            });

                }
            }
        });

        //3. ?????????(????????????) ??????
        mPhoneNumberSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhoneNumberSwitch.isChecked()) {
                    blockContact();
                } else {
                    mLoaderLayout.setVisibility(View.VISIBLE);
                    Map<String, Object> contactData = new HashMap<>();
                    contactData.put(FirebaseHelper.isContactBlocked, false);
                    FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Block").document(FirebaseHelper.mUid).update(contactData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    isContactBlocked = false;
                                    updateUI();
                                    Toast.makeText(getApplicationContext(), "????????? ??????????????? ?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                    mLoaderLayout.setVisibility(View.GONE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    isContactBlocked = true;
                                    updateUI();
                                    Toast.makeText(getApplicationContext(), "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                    mLoaderLayout.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });

        mPhoneNumberUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockContact();
            }
        });
    }

    private void permissionDialog(){
        if (isFacebookPermitted){
            blockFB();
        } else {
            DialogBlockFacebookFragment dialog = DialogBlockFacebookFragment.getInstance();
            dialog.show(getSupportFragmentManager(), DialogBlockFacebookFragment.TAG_EVENT_DIALOG);
        }
    }

    private void blockFB(){
        mLoaderLayout.setVisibility(View.VISIBLE);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if ((accessToken != null && !accessToken.isExpired())) {
            setPermission();
            linkFacebookAccount(accessToken);  //????????? ????????????.
            if (isFacebookDataDownloaded) {
                setFacebookBlockDataToDB(accessToken);
            } else {
                getFacebookFriends(accessToken, true);//???????????? ???????????? (???????????? ???????????? ????????? ????????????????????? linkFacebookAccount??? ?????? ??????)
            }
            return;
        }

        mLoaderLayout.setVisibility(View.GONE);
        LoginManager.getInstance().logInWithReadPermissions(SetProfile_3BlockFriend.this, Arrays.asList("email","user_friends")); // ??????????????? ?????? permission
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                setPermission();
                mLoaderLayout.setVisibility(View.VISIBLE);
                linkFacebookAccount(loginResult.getAccessToken()); //?????? ??????
                if (isFacebookDataDownloaded) {
                    setFacebookBlockDataToDB(loginResult.getAccessToken());
                } else {
                    getFacebookFriends(loginResult.getAccessToken(), true);//???????????? ???????????? (???????????? ???????????? ????????? ????????????????????? linkFacebookAccount??? ?????? ??????)
                }
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(SetProfile_3BlockFriend.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                isFacebookBlocked = false;
                updateUI();
                mLoaderLayout.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(SetProfile_3BlockFriend.this, "???????????? ????????? ??????", Toast.LENGTH_SHORT).show();
                isFacebookBlocked = false;
                updateUI();
                mLoaderLayout.setVisibility(View.GONE);
            }
        });
    }

    private void blockContact(){
        if (ContextCompat.checkSelfPermission(SetProfile_3BlockFriend.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SetProfile_3BlockFriend.this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        /*                    if (ActivityCompat.shouldShowRequestPermissionRationale(SetProfile_3BlockFriend.this, Manifest.permission.READ_CONTACTS)) {
                        Toast.makeText(getApplicationContext(), "??????????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    }*/
            return;
        }

        mLoaderLayout.setVisibility(View.VISIBLE);
        contactUtil = new ContactUtil(getApplicationContext());
        newContactList = contactUtil.getContactList();

        //??????????????? ????????? ????????? arrayList ????????? ??????
        Map<String, Object> contactData = new HashMap<>();
        contactData.put(FirebaseHelper.contactList, newContactList);
        contactData.put(FirebaseHelper.isContactBlocked, true);

        //DB??? ?????????
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Block").document(FirebaseHelper.mUid).update(contactData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isContactBlocked = true;
                        contactCount = newContactList.size() + "???";
                        updateUI();
                        Toast.makeText(getApplicationContext(), "????????? ??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        mLoaderLayout.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isContactBlocked = false;
                        updateUI();
                        Toast.makeText(getApplicationContext(), "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        mLoaderLayout.setVisibility(View.GONE);
                    }
                });
    }

    //1. ????????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void linkFacebookAccount(AccessToken token) {
        if (isFBLinkedAlready) {
            return;
        }

        LaunchUtil.checkAuth(this);

        Log.d(TAG, "handleFacebookAccessToken:" + token);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        user.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(FirebaseHelper.facebookUid, token.getUserId());
                    FirebaseHelper.db.collection("UserDI").document(MyProfile.getUser().getDi()).update(FirebaseHelper.facebookUid, token.getUserId());
                    Toast.makeText(SetProfile_3BlockFriend.this, "???????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                }
            }
        });
    }

    private void setFacebookBlockDataToDB(AccessToken accessToken){
        WriteBatch batch = FirebaseHelper.db.batch();
        if (!MyProfile.getUser().getFacebookUid().equals(accessToken.getUserId())){
            batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid),FirebaseHelper.facebookUid, accessToken.getUserId() );
        }
        batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Block").document(FirebaseHelper.mUid),
                FirebaseHelper.myFacebookUid, accessToken.getUserId(),
                FirebaseHelper.facebookList, newFacebookList,
                FirebaseHelper.isFacebookBlocked, true);

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                isFacebookBlocked = true;
                oldFacebookList = newFacebookList;
                updateUI();
                Toast.makeText(SetProfile_3BlockFriend.this, "???????????? ????????? ?????????????????????. ", Toast.LENGTH_SHORT).show();
                mLoaderLayout.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isFacebookBlocked = false;
                Toast.makeText(SetProfile_3BlockFriend.this, "????????? ?????????????????????. ", Toast.LENGTH_SHORT).show();
                updateUI();
                mLoaderLayout.setVisibility(View.GONE);
            }
        });
    }

    private void getFacebookFriends(AccessToken accessToken, boolean isBlocking) {
        //?????? ?????????
        GraphRequest request2 = GraphRequest.newGraphPathRequest(accessToken, "/me/friends", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response != null) {
                    try {
                        ArrayList<String> facebookFriendUidList = new ArrayList<>();
                        JSONArray facebookFriendsList = response.getJSONObject().getJSONArray("data"); //{ {"id":"116896143035435","name":"Maria Aldadeadihiia Lauman"} ?????? n??? ??????
                        for (int i = 0; i < facebookFriendsList.length(); i++) {
                            JSONObject jsonObject = (JSONObject) facebookFriendsList.get(i); // i?????? ????????? {"id":"116896143035435","name":"Maria Aldadeadihiia Lauman"} ???????????? ?????????
                            facebookFriendUidList.add(jsonObject.get("id").toString()); //??? ??? uid ??????.
                        }

                        if(facebookFriendUidList == null){
                            newFacebookList = new ArrayList<>(Arrays.asList());
                        }else{
                            newFacebookList = facebookFriendUidList;
                        }

                        int friendCount = (int) response.getJSONObject().getJSONObject("summary").get("total_count");
                        facebookFriendCount = friendCount + "???";


                        isFacebookDataDownloaded = true;
                        if (isBlocking) {
                            setFacebookBlockDataToDB(accessToken);
                        } else{
                            mLoaderLayout.setVisibility(View.GONE);
                            updateUI();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "test777" + e.toString());
                        Toast.makeText(SetProfile_3BlockFriend.this, "????????? ?????????????????????. ", Toast.LENGTH_SHORT).show();
                        isFacebookBlocked = false;
                        updateUI();
                        mLoaderLayout.setVisibility(View.GONE);
                    }
                }else{
                    Toast.makeText(SetProfile_3BlockFriend.this, "????????? ?????????????????????. ", Toast.LENGTH_SHORT).show();
                    isFacebookBlocked = false;
                    updateUI();
                    mLoaderLayout.setVisibility(View.GONE);
                }
            }
        });

        request2.executeAsync();

    }


    //3. ?????????
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                // If request is cancelled, the result arrays are image_empty_profile.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLoaderLayout.setVisibility(View.VISIBLE);

                    contactUtil = new ContactUtil(getApplicationContext());
                    newContactList = contactUtil.getContactList();
                    //??????????????? ????????? ????????? arrayList ????????? ??????
                    Map<String, Object> contactData = new HashMap<>();
                    contactData.put(FirebaseHelper.isContactBlocked, true);
                    contactData.put(FirebaseHelper.contactList, newContactList);

                    //DB??? ?????????
                    FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Block").document(FirebaseHelper.mUid).update(contactData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    isContactBlocked = true;
                                    updateUI();
                                    Toast.makeText(getApplicationContext(), "????????? ??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                    mLoaderLayout.setVisibility(View.GONE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    isContactBlocked = false;
                                    updateUI();
                                    Toast.makeText(getApplicationContext(), "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                    mLoaderLayout.setVisibility(View.GONE);
                                }
                            });
                } else {
                    isContactBlocked = false;
                    updateUI();
                }
                break;
            default:
                isContactBlocked = false;
                updateUI();
                break;

        }
    }

    @Override
    public void fbBlockOk() {
        blockFB();
    }

    public void fbBlockCancelled() {
        isFacebookBlocked = false;
        updateUI();
    }

}


//        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
//            @Override
//            public void onCompleted(JSONObject object, GraphResponse response) {
//                if (object != null) {
//                    Log.d(TAG, "test111" + object.toString());
//                    Log.d(TAG, "test113" + accessToken.getUserId());
//                    try {
//                        String facebookUid = (String) object.get("id");
//                        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(FirebaseHelper.facebookUid, facebookUid)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        isCompleteMine = true;
//                                        if (isCompleteFriends) {
//                                            mFacebookTextView.setText("(?????? ?????????: " + facebookTokenList.size() + "???, ?????? ?????????: " + facebookTokenList.size() + "???, ?????????)");
//                                            Toast.makeText(SetProfile_3BlockFriend.this, "???????????? ????????? ?????????????????????. ", Toast.LENGTH_SHORT).show();
//                                            isLoading = false;
//                                            mLoaderLayout.setVisibility(View.GONE);
//                                        }
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(SetProfile_3BlockFriend.this, "????????? ?????????????????????. ", Toast.LENGTH_SHORT).show();
//                                mFacebookSwitch.setChecked(false);
//                                isLoading = false;
//                                mLoaderLayout.setVisibility(View.GONE);
//                            }
//                        });
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Log.d(TAG, "test333" + e.toString());
//                        Toast.makeText(SetProfile_3BlockFriend.this, "????????? ?????????????????????. ", Toast.LENGTH_SHORT).show();
//                        mFacebookSwitch.setChecked(false);
//                        isLoading = false;
//                        mLoaderLayout.setVisibility(View.GONE);
//                    }
//
//                }else{
//                    mFacebookSwitch.setChecked(false);
//                    isLoading = false;
//                    mLoaderLayout.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id,name");
//        request.setParameters(parameters);
//        request.executeAsync();