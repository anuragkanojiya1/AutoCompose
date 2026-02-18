############################################################
# HILT
############################################################
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-dontwarn dagger.hilt.**

############################################################
# ROOM
############################################################
-keep class * extends androidx.room.RoomDatabase

############################################################
# GSON (Only if using reflection)
############################################################
-keepattributes Signature
-keepattributes *Annotation*

# Keep fields annotated with @SerializedName
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep classes referenced by @JsonAdapter
-keep class * extends com.google.gson.JsonDeserializer
-keep class * extends com.google.gson.JsonSerializer

# Only keep your actual DTO models (adjust package)
-keep class com.example.autocompose.domain.model.** { *; }
-keep class com.example.autocompose.domain.responseModel.** { *; }
-keep class com.example.autocompose.domain.paymentResponseModels.** { *; }

############################################################
# PAYPAL SDK
############################################################
-keep class com.paypal.** { *; }
-dontwarn com.paypal.**

############################################################
# LOTTIE
############################################################
-dontwarn com.airbnb.lottie.**