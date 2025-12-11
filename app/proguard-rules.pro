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

############################################################
# KEEP KOTLIN METADATA (required for Compose, Room, Nav)
############################################################
-keepclassmembers class kotlin.Metadata { *; }

############################################################
# KEEP JETPACK COMPOSE RUNTIME
############################################################
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

############################################################
# KEEP NAVIGATION COMPOSE
############################################################
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

############################################################
# KEEP LIFECYCLE + VIEWMODEL
############################################################
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

############################################################
# KEEP ROOM (Runtime + KSP generated classes)
############################################################
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

############################################################
# RETROFIT (reflective interface calls)
############################################################
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

############################################################
# GSON (reflective JSON serialization)
############################################################
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepattributes Signature
-keepattributes *Annotation*

############################################################
# DATASTORE PREFERENCES
############################################################
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

############################################################
# COROUTINES
############################################################
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

############################################################
# COIL (uses reflection)
############################################################
-dontwarn coil.**
-keep class coil.** { *; }


############################################################
# PAYPAL SDK
############################################################
-keep class com.paypal.** { *; }
-dontwarn com.paypal.**


############################################################
# LOTTIE (reflection for animations)
############################################################
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# Keep Gson annotations
-keep class com.google.gson.annotations.SerializedName { *; }

# Keep all Gson classes
-keep class com.google.gson.** { *; }

-keep class com.example.autocompose.data.** { *; }
-keep class com.example.autocompose.domain.** { *; }
-keep class com.example.autocompose.utils.** { *; }

