# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Berkeley/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontoptimize
-dontpreverify
-verbose
-dontwarn
-dontskipnonpubliclibraryclassmembers
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable



-dontwarn android.**
-dontwarn com.google.gson.**
-dontwarn android.support.**
-dontwarn com.etsy.android.grid.**
-dontwarn com.j256.ormlite.**
-dontwarn com.ncc.sdk.**
-dontwarn org.apache.http.**
-dontwarn com.umeng.socialize.**
-dontwarn com.tencent.**
-dontwarn com.renn.**
-dontwarn com.sina.**
-dontwarn com.nostra13.**

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService
-keepnames class * implements java.io.Serializable
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
private static final java.io.ObjectStreamField[] serialPersistentFields;
private void writeObject(java.io.ObjectOutputStream);
private void readObject(java.io.ObjectInputStream);
java.lang.Object writeReplace();
java.lang.Object readResolve();
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep class **.R$* {
    *;
}



# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn javax.annotation.**


-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.news.yazhidao.entity.** { *; }
-keep class org.json.** {*;}
-keep class com.google.gson.** {*;}
-keep class com.nostra13.**{*;}
-keep class com.sina.**{*;}
-keep class com.renn.**{*;}
-keep class com.tencent.**{*;}
-keep class org.apache.http.**{*;}
-keep class com.etsy.android.grid.**{*;}
-keep class com.umeng.socialize.**{*;}
-keep class com.j256.ormlite.**  {*;}
-keep class android.webkit.**{*;}
-keep class android.**{*;}
-keep class android.support.**{*;}
##视频
-keep class tv.danmaku.ijk.media.** { *; }
