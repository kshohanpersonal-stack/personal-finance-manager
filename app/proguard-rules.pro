# Ledgerly — release shrink rules.
-keepattributes *Annotation*
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.** { *; }
-dontwarn androidx.room.paging.**
-keepclassmembers class * { @androidx.room.** *; }
-keep class com.shohankhan.ledgerly.data.local.** { *; }
-keepclassmembers class com.shohankhan.ledgerly.domain.model.** { *; }
-keep class androidx.work.** { *; }
