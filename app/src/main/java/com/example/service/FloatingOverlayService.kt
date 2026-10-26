package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class FloatingOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var isExpanded = false

    // Draggable coordinates
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    companion object {
        const val ACTION_START = "com.example.service.START_OVERLAY"
        const val ACTION_STOP = "com.example.service.STOP_OVERLAY"
        const val ACTION_EMERGENCY_STOP = "com.example.service.EMERGENCY_STOP"
        const val CHANNEL_ID = "gbv_overlay_channel"
        const val NOTIFICATION_ID = 1001

        var isRunning = false
            private set

        var selectedIntervalMs: Long = 100L
        var isAutoClicking = false

        fun start(context: Context, intervalMs: Long = 100L) {
            selectedIntervalMs = intervalMs
            val intent = Intent(context, FloatingOverlayService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, FloatingOverlayService::class.java).apply {
                action = ACTION_STOP
            }
            context.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        createNotificationChannel()
        val notification = buildNotification("Overlay active", "GamerBoyVashu Quick Pill is ready")
        startForeground(NOTIFICATION_ID, notification)
        setupFloatingView()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopSelf()
            }
            ACTION_EMERGENCY_STOP -> {
                isAutoClicking = false
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun setupFloatingView() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val wmLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 80
            y = 200
        }

        val rootContainer = FrameLayout(this)
        overlayView = rootContainer

        // Render Collapsed Pill by default
        renderView(rootContainer, wmLayoutParams)

        try {
            windowManager?.addView(rootContainer, wmLayoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }
    }

    private fun renderView(container: FrameLayout, wmLayoutParams: WindowManager.LayoutParams) {
        container.removeAllViews()

        if (!isExpanded) {
            // Collapsed Pill View
            val pill = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(dpToPx(14), dpToPx(8), dpToPx(14), dpToPx(8))
                gravity = Gravity.CENTER_VERTICAL

                val bg = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = dpToPx(24).toFloat()
                    setColor(Color.parseColor("#E60D1117"))
                    setStroke(dpToPx(2), Color.parseColor("#00E676"))
                }
                background = bg
            }

            val dot = View(this).apply {
                val size = dpToPx(10)
                val lp = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = dpToPx(8)
                }
                this.layoutParams = lp
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(if (isAutoClicking) Color.parseColor("#00E676") else Color.parseColor("#FFD700"))
                }
            }

            val text = TextView(this).apply {
                this.text = "GBV • ${selectedIntervalMs}ms"
                setTextColor(Color.WHITE)
                textSize = 13f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            pill.addView(dot)
            pill.addView(text)

            // Touch listener for drag & click
            pill.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = wmLayoutParams.x
                        initialY = wmLayoutParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        wmLayoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        wmLayoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(container, wmLayoutParams)
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        val diffX = Math.abs(event.rawX - initialTouchX)
                        val diffY = Math.abs(event.rawY - initialTouchY)
                        if (diffX < 15 && diffY < 15) {
                            // Click detected -> Expand
                            isExpanded = true
                            renderView(container, wmLayoutParams)
                        }
                        true
                    }
                    else -> false
                }
            }

            container.addView(pill)
        } else {
            // Expanded HUD Panel
            val panel = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
                val bg = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = dpToPx(16).toFloat()
                    setColor(Color.parseColor("#F2161B22"))
                    setStroke(dpToPx(2), Color.parseColor("#00E5FF"))
                }
                background = bg
            }

            // Header Row
            val header = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val title = TextView(this).apply {
                this.text = "GamerBoyVashu HUD"
                setTextColor(Color.parseColor("#00E676"))
                textSize = 14f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                this.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val collapseBtn = Button(this).apply {
                this.text = "✕"
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.TRANSPARENT)
                setOnClickListener {
                    isExpanded = false
                    renderView(container, wmLayoutParams)
                }
            }

            header.addView(title)
            header.addView(collapseBtn)
            panel.addView(header)

            // Interval selector row
            val intervalRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, dpToPx(8), 0, dpToPx(8))
            }

            val intervals = listOf(50L, 100L, 200L, 500L)
            for (ms in intervals) {
                val btn = Button(this).apply {
                    this.text = "${ms}ms"
                    textSize = 11f
                    setTextColor(if (selectedIntervalMs == ms) Color.BLACK else Color.WHITE)
                    val btnBg = GradientDrawable().apply {
                        cornerRadius = dpToPx(8).toFloat()
                        setColor(if (selectedIntervalMs == ms) Color.parseColor("#00E676") else Color.parseColor("#30363D"))
                    }
                    background = btnBg
                    this.layoutParams = LinearLayout.LayoutParams(0, dpToPx(38), 1f).apply {
                        marginEnd = dpToPx(4)
                    }
                    setOnClickListener {
                        selectedIntervalMs = ms
                        renderView(container, wmLayoutParams)
                    }
                }
                intervalRow.addView(btn)
            }
            panel.addView(intervalRow)

            // Emergency Stop Button
            val emergencyBtn = Button(this).apply {
                this.text = "EMERGENCY KILL STOP"
                setTextColor(Color.WHITE)
                textSize = 12f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                val redBg = GradientDrawable().apply {
                    cornerRadius = dpToPx(10).toFloat()
                    setColor(Color.parseColor("#FF5252"))
                }
                background = redBg
                this.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(44)
                ).apply {
                    topMargin = dpToPx(8)
                }
                setOnClickListener {
                    isAutoClicking = false
                    stopSelf()
                }
            }
            panel.addView(emergencyBtn)

            // Open App Button
            val openAppBtn = Button(this).apply {
                this.text = "Open Diagnostics App"
                setTextColor(Color.parseColor("#00E5FF"))
                setBackgroundColor(Color.TRANSPARENT)
                textSize = 11f
                setOnClickListener {
                    val appIntent = Intent(this@FloatingOverlayService, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(appIntent)
                    isExpanded = false
                    renderView(container, wmLayoutParams)
                }
            }
            panel.addView(openAppBtn)

            container.addView(panel)
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "GamerBoyVashu Floating Overlay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls floating overlay and emergency stop"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, text: String): Notification {
        val stopIntent = Intent(this, FloatingOverlayService::class.java).apply {
            action = ACTION_EMERGENCY_STOP
        }
        val stopPending = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val appIntent = Intent(this, MainActivity::class.java)
        val appPending = PendingIntent.getActivity(
            this,
            0,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("GamerBoyVashu Auto Clicker")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(appPending)
            .addAction(android.R.drawable.ic_delete, "Emergency Stop", stopPending)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
