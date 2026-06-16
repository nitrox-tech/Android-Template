# Preserve useful Crashlytics stack traces while hiding original source names.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotation and generic signature metadata used by Android/Firebase SDKs.
-keepattributes Signature,*Annotation*

# Keep native method names because JNI resolves them by name.
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Keep WebView JavaScript bridge methods if one is added later.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Firebase discovers component registrars through manifest metadata and reflection.
-keepclassmembers class * implements com.google.firebase.components.ComponentRegistrar {
    public <init>();
}

# Room creates generated database implementations through reflection.
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public <init>();
}
