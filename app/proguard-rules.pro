# Keep Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Keep Moshi
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}

# Keep Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers class * { @androidx.room.* <fields>; }

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
