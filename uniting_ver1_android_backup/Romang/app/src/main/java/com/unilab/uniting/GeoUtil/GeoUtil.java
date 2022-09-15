package com.unilab.uniting.GeoUtil;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;
import com.unilab.uniting.model.MyGeoPoint;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GeoUtil {

    public static boolean isLocationSynchronized = false;

    public static Map<String,String> koreaLocation = new HashMap<>();

    public static void init() {
        koreaLocation.put("서울특별시","서울");
        koreaLocation.put("인천광역시","인천");
        koreaLocation.put("경기도","경기");
        koreaLocation.put("대전광역시" ,"대전");
        koreaLocation.put("세종특별자치시" ,"세종");
        koreaLocation.put("부산광역시" ,"부산");
        koreaLocation.put("대구광역시" ,"대구");
        koreaLocation.put("광주광역시" ,"광주");
        koreaLocation.put("울산광역시" ,"울산");
        koreaLocation.put("강원도" ,"강원");
        koreaLocation.put("충청북도" ,"충북");
        koreaLocation.put("충청남도" ,"충남");
        koreaLocation.put("전라북도" ,"전북");
        koreaLocation.put("전라남도" ,"전남");
        koreaLocation.put("경상북도" ,"경북");
        koreaLocation.put("경상남도" ,"경남");
        koreaLocation.put("제주도" ,"제주");
    }

    static public String getAddress(Location location, Context context) {
        String nowAddress = "";
        String userLocation = MyProfile.getUser().getLocation();
        Geocoder geocoder = new Geocoder(context, Locale.KOREA);
        List<Address> address;
        try {
            //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
            //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
            address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (address != null && address.size() > 0) {
                // 주소 받아오기
                nowAddress = address.get(0).getAddressLine(0);

            }

        } catch (IOException e) {
            //Toast.makeText(MainActivity.this, "잘못된 포인트 설정입니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        if(!nowAddress.equals("")){
            String[] addressArray = nowAddress.split(" ");
            if (addressArray.length > 1){
                userLocation = addressArray[1];
            }
        }

        return userLocation;
    }

    public static void updateLocationPermission(Context context){
        if (!canGetLocation(context)||(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(FirebaseHelper.geoPermitted, false);
        } else {
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(FirebaseHelper.geoPermitted, true);
        }
    }

    public static boolean getLocationPermission(Context context){
         return !canGetLocation(context)||(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);

    }

    public static void synchronizeLocation(Location location, Context context){
        String nowAddress = "";
        String userLocation = MyProfile.getUser().getLocation();
        boolean isUpdateNeeded = true;

        Geocoder geocoder = new Geocoder(context, Locale.KOREA);
        List<Address> address;
        try {
            //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
            //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
            address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (address != null && address.size() > 0) {
                // 주소 받아오기
                nowAddress = address.get(0).getAddressLine(0);

            }

        } catch (IOException e) {
            //Toast.makeText(MainActivity.this, "잘못된 포인트 설정입니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        if(!nowAddress.equals("")){
            String[] addressArray = nowAddress.split(" ");
            if (addressArray.length > 1){
                userLocation = addressArray[1];
            }
        }

        GeoUtil.init();
        if(koreaLocation.get(userLocation) != null){
            userLocation = koreaLocation.get(userLocation);
        }

        if(MyProfile.getUser().getGeoPoint() != null){
            Location myProfileLocation =  new Location("uniting");
            myProfileLocation.setLatitude(MyProfile.getUser().getGeoPoint().getLatitude());
            myProfileLocation.setLongitude(MyProfile.getUser().getGeoPoint().getLongitude());

            if(!userLocation.equals(MyProfile.getUser().getLocation()) || getMeterDistanceFrom(location,myProfileLocation) > 300){
                isUpdateNeeded = true;
            } else {
                isUpdateNeeded = false;
                isLocationSynchronized = true;
            }
        }

        MyGeoPoint geoPoint = new MyGeoPoint(location.getLatitude(), location.getLongitude());
        GeoHash geoHash = GeoHash.fromLocation(location, 8);
        String hash = geoHash.toString();

        if(isUpdateNeeded){
            Map<String,Object> geoData = new HashMap<>();
            geoData.put(FirebaseHelper.geoPoint, geoPoint);
            geoData.put(FirebaseHelper.location, userLocation);
            geoData.put(FirebaseHelper.geoPermitted, true);
            geoData.put(FirebaseHelper.geoHash, hash);

            String finalUserLocation = userLocation;
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                    .set(geoData, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            MyProfile.getOurInstance().setLocation(finalUserLocation);
                            MyProfile.getOurInstance().setGeoPoint(geoPoint);
                            MyProfile.getOurInstance().setGeoHash(hash);
                            MyProfile.getOurInstance().setGeoPermitted(true);
                            isLocationSynchronized = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

    }

    public static float getMeterDistanceFrom(Location firstLocation, Location secondLocation){
        float distance = firstLocation.distanceTo(secondLocation);
        return distance;
    }

    public static float getMeterDistanceFrom(MyGeoPoint firstGeoPoint, MyGeoPoint secondGeoPoint){
        Location firstLocation = new Location("geo");
        firstLocation.setLatitude(firstGeoPoint.getLatitude());
        firstLocation.setLongitude(firstGeoPoint.getLongitude());

        Location secondLocation = new Location("geo");
        secondLocation.setLatitude(secondGeoPoint.getLatitude());
        secondLocation.setLongitude(secondGeoPoint.getLongitude());

        float distance = firstLocation.distanceTo(secondLocation);
        return distance;
    }

    public static int getKiloDistanceFrom(MyGeoPoint firstGeoPoint, MyGeoPoint secondGeoPoint){
        Location firstLocation = new Location("geo");
        firstLocation.setLatitude(firstGeoPoint.getLatitude());
        firstLocation.setLongitude(firstGeoPoint.getLongitude());

        Location secondLocation = new Location("geo");
        secondLocation.setLatitude(secondGeoPoint.getLatitude());
        secondLocation.setLongitude(secondGeoPoint.getLongitude());

        float distance = firstLocation.distanceTo(secondLocation);
        int distanceInKilo = (int) Math.ceil(distance/1000);
        return distanceInKilo;
    }

    public static boolean canGetLocation(Context context) {
        boolean result = true;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;


        // exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (gps_enabled == false || network_enabled == false) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }
}
