package com.unilab.uniting.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Calendar;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

/**
 * @since 2018. 12. 21
 * Data 암호화 로직
 * 반드시 중요한 데이터만 암호화 시킬 것(성능 저하 발생)
 */
public class RSACryptor {
    private static final String TAG = "RSACryptor";

    private KeyStore.Entry keyEntry;

    //비대칭 암호화(공개키) 알고리즘 호출 상수
    private static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";

    /**
     * Singleton Pattern
     * Call -> RSACryptor.getInstance().method()
     */
    private RSACryptor(){}

    private static class RSACryptorHolder{
        static final RSACryptor INSTANCE = new RSACryptor();
    }

    public static RSACryptor getInstance(){
        return RSACryptorHolder.INSTANCE;
    }

    //Android KeyStore 시스템에서는 암호화 키를
    //컨테이너(시스템만이 접근 가능한 곳)에 저장해야 하므로
    //이 키를 기기에서 추출해내기가 더 어려움
    public void init(Context context){
        try {
            //AndroidKeyStore 정확하게 기입
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);

            //KeyStore에 해당 패키지 네임이 등록되어있는가?
            if(!ks.containsAlias(context.getPackageName())){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    initAndroidM(context.getPackageName());
                } else {
                    initAndroidK(context);
                }
            }
            keyEntry = ks.getEntry(context.getPackageName(), null);
        }catch (KeyStoreException | IOException | NoSuchAlgorithmException |
                CertificateException | UnrecoverableEntryException e ){
            Log.e(TAG, "Initialize fail", e);
        }
    }

    //API Level 23 이상(마쉬멜로우) 개인키 생성
    private void initAndroidM(String alias) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //AndroidKeyStore 정확하게 기입
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

                kpg.initialize(new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setAlgorithmParameterSpec(new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4))
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .setDigests(KeyProperties.DIGEST_SHA512, KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA256)
                        .setUserAuthenticationRequired(false)
                        .build());

                kpg.generateKeyPair();

                Log.d(TAG, "RSA Initialize");
            }
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "이 디바이스는 관련 알고리즘을 지원하지 않음.", e);
        }
    }

    //API Level 19 이상(킷캣) 개인키 생성
    private void initAndroidK(Context context){
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //유효성 기간
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 25);

                //AndroidKeyStore 정확하게 기입
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");

                kpg.initialize(new KeyPairGeneratorSpec.Builder(context)
                        .setKeySize(2048)
                        .setAlias(context.getPackageName())
                        .setSubject(new X500Principal("CN=myKey"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build());

                kpg.generateKeyPair();

                Log.d(TAG, "RSA Initialize");
            }
        }catch (GeneralSecurityException e) {
            Log.e(TAG, "이 디바이스는 관련 알고리즘을 지원하지 않음.", e);
        }
    }

    //문자열 위주로 작업하기 때문에 반드시 String형이나 toString을 쓸 것!!
    //암호화(set)
    public String encrypt(String plain){
        try {
            byte[] bytes = plain.getBytes("UTF-8");
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            //Public Key로 암호화
            cipher.init(Cipher.ENCRYPT_MODE, ((KeyStore.PrivateKeyEntry) keyEntry).getCertificate().getPublicKey());
            byte[] encryptedBytes = cipher.doFinal(bytes);

            Log.d(TAG, "Encrypted Text : " + new String(Base64.encode(encryptedBytes, Base64.DEFAULT)));

            return new String(Base64.encode(encryptedBytes, Base64.DEFAULT));

        }catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
            Log.e(TAG, "Encrypt fail",  e);
            return plain;
        }
    }

    //복호화(get)
    //데이터가 유출되더라도 복호화는 이 로직에서만 가능함
    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            //Private Key로 복호화
            cipher.init(Cipher.DECRYPT_MODE, ((KeyStore.PrivateKeyEntry) keyEntry).getPrivateKey());
            byte[] base64Bytes = encryptedText.getBytes("UTF-8");
            byte[] decryptedBytes = Base64.decode(base64Bytes, Base64.DEFAULT);

            Log.d(TAG, "Decrypted Text : " + new String(cipher.doFinal(decryptedBytes)));

            return new String(cipher.doFinal(decryptedBytes));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException e) {
            Log.e(TAG, "Decrypt fail",  e);
            return encryptedText;
        }
    }
}
