package com.slate.weekly.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
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

/**
 * Packages that must stay reachable during a strict focus timer. Blocking any
 * of these could trap the user: settings is the only way to turn the
 * accessibility service off, the launcher gets them back home, and the dialer
 * is needed for calls. The set is also used again inside the accessibility
 * service, so a stale or hand-edited blocked list can never lock the phone.
 */
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
    addFrom(Intent(Settings.ACTION_SETTINGS))
    addFrom(Intent(Intent.ACTION_DIAL, Uri.parse("tel:")))

    protected += CriticalPackages
    return protected
}

private val CriticalPackages = setOf(
    "android",
    "com.android.systemui",
    "com.android.settings",
    "com.android.settings.intelligence",
    "com.google.android.settings",
    "com.samsung.android.settings",
    "com.huawei.android.settings",
    "com.oplus.settings",
    "com.coloros.settings",
    "com.android.phone",
    "com.android.server.telecom",
    "com.android.dialer",
    "com.google.android.dialer",
    "com.samsung.android.dialer",
    "com.android.incallui",
    "com.android.emergency",
    "com.android.packageinstaller",
    "com.google.android.packageinstaller",
    "com.samsung.android.packageinstaller",
    "com.android.permissioncontroller",
    "com.google.android.permissioncontroller",
    "com.android.launcher",
    "com.android.launcher3",
    "com.google.android.apps.nexuslauncher",
    "com.sec.android.app.launcher",
    "com.miui.home",
    "com.oplus.launcher",
    "com.coloros.launcher",
    "com.huawei.android.launcher",
    "com.oneplus.launcher",
)
