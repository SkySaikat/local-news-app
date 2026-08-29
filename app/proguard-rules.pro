# Keep Firestore data models so reflective (de)serialization keeps working in release builds.
-keepclassmembers class com.chittagong.localnews.data.remote.dto.** {
    *;
}
-keep class com.chittagong.localnews.data.remote.dto.** { *; }

# kotlinx.serialization keeps the generated serializers for our navigation routes.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}
