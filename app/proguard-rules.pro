# Keep Google API client model classes and their members
-keep class com.google.api.client.googleapis.json.** { *; }

# Keep JSON parser-related classes
-keep class com.google.api.client.json.** { *; }

# Keep any fields annotated with @Key (used for JSON mapping)
-keepclassmembers class * {
    @com.google.api.client.util.Key <fields>;
}

# Keep default constructors for classes in Google API client packages
-keepclassmembers class com.google.api.client.googleapis.json.** {
    public <init>();
}

# KEEP Google Drive API model classes to allow reflection instantiation
-keep class com.google.api.services.drive.model.** { *; }
-keepclassmembers class com.google.api.services.drive.model.** {
    public <init>();
}

# Removed invalid '-dontobfuscate' directive

# Keep Google API client Data class and related static factory methods
-keep class com.google.api.client.util.Data { *; }
-keepclassmembers class com.google.api.client.util.Data {
    public static *** nullOf(java.lang.Class);
}

# Optional: Keep public constructors globally (helps with reflection)
-keepclassmembers class * {
    public <init>();
}

# Suppress warnings for Google API client and services packages
-dontwarn com.google.api.client.**
-dontwarn com.google.api.services.**

# Print usage output for unused rules
-printusage build/outputs/mapping/release/unused.txt
