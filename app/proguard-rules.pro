# Keep Room entities and DAOs
-keep class com.coppertrack.private.data.local.entity.** { *; }
-keep class com.coppertrack.private.data.local.dao.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep enums
-keepclassmembers enum * { *; }
