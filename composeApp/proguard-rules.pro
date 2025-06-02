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


-keepclassmembers class mt.modder.hub.axml.** {
    public <init>(...);
    public *;
}

-keepclassmembers class mt.modder.hub.axmlTools.** {
    public <init>(...);
    public *;
}

-keep class mt.modder.hub.axml.** { *; }
-keep class mt.modder.hub.axmlTools.** { *; }

-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

-keep class eternal.future.tefmodloader.utility.LoadPage_androidKt

-keep class com.and.games505.TerrariaPaid.R { *; }
-dontwarn com.and.games505.TerrariaPaid.R