# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/iqbalprabu/Android/Sdk/tools/proguard/proguard-android.txt
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

-keep class com.synnapps.carouselview.** { *; }

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }
-keep class android.support.v7.widget.SearchView { *; }

-dontwarn sun.misc.Unsafe

-dontwarn javax.annotation.**
-dontwarn javax.lang.model.element.**
-dontwarn javax.lang.model.type.**
-dontwarn javax.lang.model.SourceVersion
-dontwarn javax.lang.model.util.Types
-dontwarn javax.tools.Diagnostic**
-dontwarn javax.tools.JavaFileObject**