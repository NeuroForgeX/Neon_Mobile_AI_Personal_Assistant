# Release Candidate specific ProGuard rules
# More aggressive optimization for release candidates

# Keep LangChain4j classes and methods
-keep class dev.langchain4j.** { *; }
-keep class org.apache.** { *; }
-keep class ai.djl.** { *; }
-keep class org.slf4j.** { *; }

# Keep AI model related classes
-keep class com.forge.bright.ai.** { *; }
-keep class com.forge.bright.utils.** { *; }

# Keep model loading and chat functionality
-keepclassmembers class * {
    public *** chat(...);
    public *** loadChatModel(...);
}

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

# Keep RecyclerView adapters
-keep class * extends androidx.recyclerview.widget.RecyclerView.Adapter {
    public *** onCreateViewHolder(...);
    public *** onBindViewHolder(...);
}

# Preserve line numbers for debugging release candidates
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Don't obfuscate local variable names in release candidates
-keepnames class * { *; }

# Keep exceptions for better error reporting
-keepclassmembers class * extends java.lang.Exception {
    *** getMessage(...);
    *** getCause(...);
}

# Keep logging for release candidate debugging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
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

# Keep AsyncTask and similar classes for model downloads
-keep class * extends android.os.AsyncTask { *; }
-keep class * extends androidx.lifecycle.AsyncTask { *; }
