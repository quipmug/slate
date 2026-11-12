package com.slate.weekly.focus

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.slate.weekly.SlateApp
import com.slate.weekly.domain.isFocusActive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FocusAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var settingsJob: Job? = null

    @Volatile
    private var focusUntil: Long = 0L

    @Volatile
    private var blockedPackages: Set<String> = emptySet()

    override fun onServiceConnected() {
        super.onServiceConnected()
        settingsJob?.cancel()
        val store = (application as SlateApp).store
        settingsJob = serviceScope.launch {
            store.settings.collect { settings ->
                focusUntil = settings.focusUntil
                blockedPackages = settings.blockedPackages
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val foreground = event.packageName?.toString() ?: return
        if (foreground == packageName) return
        if (!isFocusActive(focusUntil, System.currentTimeMillis())) return
        if (foreground in blockedPackages) {
            performGlobalAction(GLOBAL_ACTION_HOME)
        }
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        settingsJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }
}
