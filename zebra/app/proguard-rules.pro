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

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

#okgo
-dontwarn com.lzy.okgo.**
-keep class com.lzy.okgo.**{*;}

#okrx
-dontwarn com.lzy.okrx.**
-keep class com.lzy.okrx.**{*;}

#okrx2
-dontwarn com.lzy.okrx2.**
-keep class com.lzy.okrx2.**{*;}

#okserver
-dontwarn com.lzy.okserver.**
-keep class com.lzy.okserver.**{*;}

#
-dontwarn org.apache.http.**
-keep class org.apache.http.**{*;}

-dontwarn com.alipay.**
-keep class com.alipay.**{*;}

-dontwarn com.tencent.mm.opensdk.**
-keep class com.tencent.mm.opensdk.**{*;}

-dontwarn com.google.gson.**
-keep class com.google.gson.**{*;}

-dontwarn com.orhanobut.logger.**
-keep class com.orhanobut.logger.**{*;}

-dontwarn com.readystatesoftware.systembartint.**
-keep class com.readystatesoftware.systembartint.**{*;}

# 保留Parcelable序列化的类不能被混淆
-keep class * implements android.os.Parcelable{
    public static final android.os.Parcelable$Creator *;
}

-keep class cn.jinxiit.zebra.beans.**{*;}

-dontwarn com.github.mikephil.**
-keep class com.github.mikephil.**{ *; }

-dontwarn com.google.zxing.**
-keep class com.google.zxing.**{ *; }

 # ProGuard configurations for Bugtags
  -keepattributes LineNumberTable,SourceFile

  -keep class com.bugtags.library.** {*;}
  -dontwarn com.bugtags.library.**
  -keep class io.bugtags.** {*;}
  -dontwarn io.bugtags.**
  -dontwarn org.apache.http.**
  -dontwarn android.net.http.AndroidHttpClient

  # End Bugtags