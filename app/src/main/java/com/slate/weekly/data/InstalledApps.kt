package com.slate.weekly.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import java.util.Locale

data class LaunchableApp(
    val packageName: String,
    val label: String,
)

fun loadLaunchableApps(context: Context): List<LaunchableApp> {
    val packageManager = context.packageManager
    val protected = protectedPackages(context)
    val launcherIntent = Intent(Intent.ACTION_MAIN)
        .addCategory(Intent.CATEGORY_LAUNCHER)

    @Suppress("DEPRECATION")
    val resolved = packageManager.queryIntentActivities(launcherIntent, 0)
    return resolved
        .asSequence()
        .mapNotNull { info ->
            val packageName = info.activityInfo?.packageName ?: return@mapNotNull null
            if (packageName in protected) return@mapNotNull null
            val label = info.loadLabel(packageManager)
                ?.toString()
                ?.trim()
                .orEmpty()
                .ifEmpty { packageName }
            LaunchableApp(packageName = packageName, label = label)
        }
        .distinctBy { it.packageName }
        .sortedBy { it.label.lowercase(Locale.ENGLISH) }
        .toList()
}

internal fun protectedPackages(context: Context): Set<String> {
    val packageManager = context.packageManager
    val protected = linkedSetOf(context.packageName)

    fun addFrom(intent: Intent) {
        runCatching {
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .mapNotNullTo(protected) { it.activityInfo?.packageName }
        }
    }

    addFrom(
        Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_HOME),
    )
    addFrom(Intent(android.provider.Settings.ACTION_SETTINGS))
    addFrom(Intent(Intent.ACTION_DIAL, Uri.parse("tel:")))
    protected += setOf(
        "com.android.systemui",
        "com.android.permissioncontroller",
        "com.google.android.permissioncontroller",
        "com.android.packageinstaller",
        "com.google.android.packageinstaller",
    )
    return protected
}
