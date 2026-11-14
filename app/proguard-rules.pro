# Slate ProGuard rules

# Hilt
-keep class com.slate.weekly.SlateApp { *; }
-keep @interface dagger.hilt.android.*
-keep class dagger.hilt.** { *; }

# Room
-keep class androidx.room.** { *; }
-keep class com.slate.weekly.data.HabitEntity { *; }
-keep class com.slate.weekly.data.HabitCheck { *; }
-keep class com.slate.weekly.data.HabitWithChecks { *; }

# DataStore
-keep class androidx.datastore.** { *; }

# WorkManager
-keep class androidx.work.** { *; }

# Navigation Compose
-keep class androidx.navigation.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-keep class androidx.activity.** { *; }

# Kotlin serialization
-keep class kotlinx.serialization.** { *; }

# Application class
-keep class com.slate.weekly.MainActivity { *; }

# Preserve R class
-keep class com.slate.weekly.R { *; }
-keep class com.slate.weekly.R$* { *; }

# Accessibility service
-keep class com.slate.weekly.focus.FocusAccessibilityService { *; }
