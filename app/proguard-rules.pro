# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# Keep application classes
-keep class com.forge.bright.** { *; }

# Keep model related classes
-keep class com.forge.bright.ai.** { *; }
-keep class com.forge.bright.utils.** { *; }
-keep class com.forge.bright.ui.** { *; }

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep ViewBinding classes
-keep class * extends androidx.viewbinding.ViewBinding {
    public static *** inflate(...);
    public static *** bind(...);
}

# Keep navigation components
-keep class androidx.navigation.** { *; }

# Keep RecyclerView adapters and view holders
-keep class * extends androidx.recyclerview.widget.RecyclerView.Adapter { *; }
-keep class * extends androidx.recyclerview.widget.RecyclerView.ViewHolder { *; }

# Keep coroutine related classes
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Keep exceptions for better error reporting
-keepclassmembers class * extends java.lang.Exception {
    *** getMessage(...);
    *** getCause(...);
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
    *** get*();
}

# Keep AsyncTask for model downloads
-keep class * extends android.os.AsyncTask { *; }

# Keep file operations
-keep class java.io.** { *; }
-keep class java.nio.** { *; }

# Keep network operations
-keep class java.net.** { *; }
-keep class javax.net.** { *; }

# Keep JSON parsing
-keep class org.json.** { *; }
-keep class com.google.gson.** { *; }

# Keep annotations
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Don't warn about missing classes that are not used
-dontwarn org.bouncycastle.**
-dontwarn org.brotli.dec.**
-dontwarn org.objectweb.asm.**
-dontwarn org.tukaani.xz.**
-dontwarn org.apache.commons.compress.harmony.**

# Keep PDFBox related classes but don't warn about missing dependencies
-keep class org.apache.pdfbox.** { *; }
-dontwarn org.bouncycastle.**

# Keep Apache Commons Compress
-keep class org.apache.commons.compress.** { *; }
-dontwarn org.tukaani.xz.**

# Assume no side effects for these classes
-assumenosideeffects class java.lang.StringBuilder {
    public java.lang.StringBuilder();
    public java.lang.StringBuilder append(java.lang.String);
    public java.lang.StringBuilder append(int);
    public java.lang.String toString();
}

# Keep TensorFlow Lite classes for Play Services compatibility
-keep class org.tensorflow.lite.** { *; }
-keep class org.tensorflow.lite.gpu.** { *; }
-keep class org.tensorflow.lite.nnapi.** { *; }
-keep class org.tensorflow.lite.delegate.** { *; }

# Keep Play Services TensorFlow Lite classes
-keep class com.google.android.gms.tflite.** { *; }
-keep class com.google.android.gms.dynamic.** { *; }

# Keep LiteRT classes
-keep class com.google.ai.edge.litert.** { *; }
-keep class com.google.ai.edge.litert.gpu.** { *; }
-keep class com.google.ai.edge.litertlm.** { *; }

# Don't warn about TensorFlow Lite related classes
-dontwarn org.tensorflow.lite.**
-dontwarn com.google.android.gms.tflite.**
