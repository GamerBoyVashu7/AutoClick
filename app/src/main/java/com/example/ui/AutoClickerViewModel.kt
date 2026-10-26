package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.MotionEvent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.InputEventSnapshot
import com.example.model.MouseInfo
import com.example.service.FloatingOverlayService
import com.example.util.InputDeviceDetector
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AutoClickerViewModel(application: Application) : AndroidViewModel(application) {

    private val _mouseInfo = MutableStateFlow(MouseInfo())
    val mouseInfo: StateFlow<MouseInfo> = _mouseInfo.asStateFlow()

    private val _clickIntervalMs = MutableStateFlow(100L)
    val clickIntervalMs: StateFlow<Long> = _clickIntervalMs.asStateFlow()

    private val _isServiceActive = MutableStateFlow(false)
    val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()

    private val _isOverlayRunning = MutableStateFlow(false)
    val isOverlayRunning: StateFlow<Boolean> = _isOverlayRunning.asStateFlow()

    private val _isHoldingButton1 = MutableStateFlow(false)
    val isHoldingButton1: StateFlow<Boolean> = _isHoldingButton1.asStateFlow()

    private val _inAppSimulatedClickCount = MutableStateFlow(0)
    val inAppSimulatedClickCount: StateFlow<Int> = _inAppSimulatedClickCount.asStateFlow()

    private val _recentEvents = MutableStateFlow<List<InputEventSnapshot>>(emptyList())
    val recentEvents: StateFlow<List<InputEventSnapshot>> = _recentEvents.asStateFlow()

    private val _emergencyStopTriggered = MutableStateFlow(false)
    val emergencyStopTriggered: StateFlow<Boolean> = _emergencyStopTriggered.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    private var testLoopJob: Job? = null

    init {
        refreshHardware()
    }

    fun refreshHardware() {
        viewModelScope.launch {
            val info = InputDeviceDetector.scanDevices(getApplication())
            _mouseInfo.value = info
            _isOverlayRunning.value = FloatingOverlayService.isRunning
        }
    }

    fun setClickInterval(ms: Long) {
        _clickIntervalMs.value = ms
        FloatingOverlayService.selectedIntervalMs = ms
    }

    fun toggleService() {
        if (_isServiceActive.value) {
            stopAutoClicker()
        } else {
            startAutoClicker()
        }
    }

    fun startAutoClicker() {
        _isServiceActive.value = true
        _emergencyStopTriggered.value = false
    }

    fun stopAutoClicker() {
        _isServiceActive.value = false
        _isHoldingButton1.value = false
        testLoopJob?.cancel()
        testLoopJob = null
    }

    fun triggerEmergencyStop() {
        _isServiceActive.value = false
        _isHoldingButton1.value = false
        _emergencyStopTriggered.value = true
        testLoopJob?.cancel()
        testLoopJob = null

        // Stop floating service if running
        try {
            FloatingOverlayService.stop(getApplication())
            _isOverlayRunning.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleOverlay(context: Context): Boolean {
        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return false
        }

        if (FloatingOverlayService.isRunning) {
            FloatingOverlayService.stop(context)
            _isOverlayRunning.value = false
        } else {
            FloatingOverlayService.start(context, _clickIntervalMs.value)
            _isOverlayRunning.value = true
        }
        return true
    }

    fun handlePadMotionEvent(event: MotionEvent) {
        val action = event.actionMasked
        val buttonState = event.buttonState
        val isPrimary = (buttonState and MotionEvent.BUTTON_PRIMARY) != 0 ||
                action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_BUTTON_PRESS

        val isSecondary = (buttonState and MotionEvent.BUTTON_SECONDARY) != 0
        val isTertiary = (buttonState and MotionEvent.BUTTON_TERTIARY) != 0

        val actionName = when (action) {
            MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
            MotionEvent.ACTION_UP -> "ACTION_UP"
            MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
            MotionEvent.ACTION_BUTTON_PRESS -> "BUTTON_PRESS"
            MotionEvent.ACTION_BUTTON_RELEASE -> "BUTTON_RELEASE"
            MotionEvent.ACTION_HOVER_MOVE -> "HOVER_MOVE"
            MotionEvent.ACTION_HOVER_ENTER -> "HOVER_ENTER"
            MotionEvent.ACTION_HOVER_EXIT -> "HOVER_EXIT"
            MotionEvent.ACTION_SCROLL -> "ACTION_SCROLL"
            else -> "ACTION_$action"
        }

        val toolType = when (event.getToolType(0)) {
            MotionEvent.TOOL_TYPE_MOUSE -> "MOUSE"
            MotionEvent.TOOL_TYPE_FINGER -> "FINGER"
            MotionEvent.TOOL_TYPE_STYLUS -> "STYLUS"
            MotionEvent.TOOL_TYPE_ERASER -> "ERASER"
            else -> "UNKNOWN (${event.getToolType(0)})"
        }

        val btnText = buildString {
            if (isPrimary) append("[L-Click] ")
            if (isSecondary) append("[R-Click] ")
            if (isTertiary) append("[M-Click] ")
            if (isEmpty()) append("None")
        }

        val isDownOrPress = action == MotionEvent.ACTION_DOWN ||
                (action == MotionEvent.ACTION_BUTTON_PRESS && (event.actionButton == MotionEvent.BUTTON_PRIMARY || isPrimary))
        val isUpOrRelease = action == MotionEvent.ACTION_UP ||
                (action == MotionEvent.ACTION_BUTTON_RELEASE && (event.actionButton == MotionEvent.BUTTON_PRIMARY || !isPrimary))

        if (isDownOrPress) {
            _isHoldingButton1.value = true
            // If service is enabled, trigger in-app testing pulse loop to demonstrate interval timing
            if (_isServiceActive.value && testLoopJob == null) {
                startInAppTestLoop()
            }
        } else if (isUpOrRelease) {
            _isHoldingButton1.value = false
            testLoopJob?.cancel()
            testLoopJob = null
        }

        val snapshot = InputEventSnapshot(
            id = System.nanoTime(),
            timeFormatted = timeFormat.format(Date(event.eventTime)),
            actionName = actionName,
            buttonStateText = btnText,
            isPrimaryPressed = isPrimary,
            isSecondaryPressed = isSecondary,
            isTertiaryPressed = isTertiary,
            x = event.x,
            y = event.y,
            toolType = toolType,
            source = "Source: ${event.source}",
            isHeld = _isHoldingButton1.value
        )

        _recentEvents.update { list ->
            listOf(snapshot) + list.take(49) // Keep last 50 events
        }
    }

    private fun startInAppTestLoop() {
        testLoopJob = viewModelScope.launch {
            while (isActive && _isHoldingButton1.value && _isServiceActive.value) {
                _inAppSimulatedClickCount.update { it + 1 }
                delay(_clickIntervalMs.value)
            }
        }
    }

    fun clearLogs() {
        _recentEvents.value = emptyList()
        _inAppSimulatedClickCount.value = 0
    }
}
