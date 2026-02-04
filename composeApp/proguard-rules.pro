# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Firebase Crashlytics
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Keep Koin
-keepnames class * extends org.koin.core.module.Module
-keepclassmembers class * extends org.koin.core.module.Module {
    public <init>(...);
}

# Keep SQLDelight
-keep class com.kobby.hymnal.composeApp.database.** { *; }
-keepclassmembers class com.kobby.hymnal.composeApp.database.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
-keep class kotlin.Metadata { *; }

# Keep data classes
-keepclassmembers class com.kobby.hymnal.** {
    public <init>(...);
}

# Kotlinx Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Multiplatform Settings
-keep class com.russhwolf.settings.** { *; }

# If using Voyager navigation
-keep class cafe.adriel.voyager.** { *; }
