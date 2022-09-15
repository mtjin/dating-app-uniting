# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-assumenosideeffects class android.util.Log {
#    public static boolean isLoggable(java.lang.String, int);
#    public static int v(...);
#    public static int i(...);
#    public static int w(...);
#    public static int d(...);
#    public static int e(...);
#}

-keepattributes SourceFile,LineNumberTable, InnerClasses, Exceptions, Signature, EnclosingMethod
-keepattributes *Annotation*
-dontoptimize
-dontwarn org.**
-dontwarn retrofit2.**

-keep class com.remotemonster.sdk.* { *;}
-keep class com.remotemonster.sdk.data.** { *;}
-keep class com.remotemonster.sdk.util.Logger{ *;}
-keep class com.remotemonster.sdk.data.Channel{ *;}
-keep class com.remotemonster.sdk.data.InitMessage{ *;}
-keep enum com.remotemonster.sdk.data.ChannelStatus{ *;}
-keep enum com.remotemonster.sdk.data.ChannelType{ *;}
-keep class org.** { *;}
-keep interface org.** { *;}

-keepclasseswithmembers class * {@retrofit2.http.* <methods>;}
-keep class retrofit2.** { *; }


-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {public *;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info { public *;}
-keep class com.android.installreferrer.api.InstallReferrerClient {public *;}
-keep class com.android.installreferrer.api.InstallReferrerClient$newBuilder { public *;}
-keep class com.android.installreferrer.api.InstallReferrerClient$Builder { public *;}
-keep class com.android.installreferrer.api.ReferrerDetails {public *;}
-keep class com.android.installreferrer.api.InstallReferrerStateListener {public *;}
-keep class io.airbridge.deviceinfo.** { *; }
-keepclassmembers class io.airbridge.** { public *; }
-keep public class io.airbridge.**


#AppsFlyer
-dontwarn com.android.installreferrer
#오류시 추가 -keep class com.appsflyer.** { *; }
-keep public class com.google.firebase.messaging.FirebaseMessagingService {
  public *;
}