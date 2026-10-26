package com.example.model

data class FeasibilityItem(
    val title: String,
    val status: AuditStatus,
    val summary: String,
    val technicalDetails: String,
    val androidConstraint: String,
    val recommendation: String
)

enum class AuditStatus {
    SUPPORTED_IN_APP,
    RESTRICTED_BY_OS,
    REQUIRES_PRIVILEGED_ROOT_OR_ADB,
    IMPOSSIBLE_IN_USERLAND
}

object FeasibilityData {
    val items = listOf(
        FeasibilityItem(
            title = "Physical USB Mouse Button 1 Interception in Background",
            status = AuditStatus.RESTRICTED_BY_OS,
            summary = "Standard Android security isolates background apps from raw external pointer events when another app (Minecraft Bedrock) is in the foreground.",
            technicalDetails = "Physical USB OTG mouse events travel from the Linux kernel (/dev/input/eventX) into Android's native EventHub and InputReader. The InputDispatcher routes MotionEvent (including mouse clicks BUTTON_PRIMARY) EXCLUSIVELY to the currently focused window. When Minecraft is active, background services receive ZERO MotionEvents.",
            androidConstraint = "AccessibilityService.onKeyEvent() strictly intercepts KeyEvent (key presses like letters or media keys). Physical mouse buttons generate MotionEvent, NOT KeyEvent. Android provides no public API for background apps to hook global MotionEvent streams.",
            recommendation = "Do NOT believe apps claiming to read background mouse button presses without root, ADB, or Shizuku. Testing shows Button 1 is only detectable when the app's own window has focus."
        ),
        FeasibilityItem(
            title = "Native Mouse Event Injection (TOOL_TYPE_MOUSE)",
            status = AuditStatus.REQUIRES_PRIVILEGED_ROOT_OR_ADB,
            summary = "Injecting genuine mouse button packets into Minecraft requires the signature-level INJECT_EVENTS permission.",
            technicalDetails = "To inject true hardware-equivalent mouse clicks, an app must call InputManager.injectInputEvent(MotionEvent with TOOL_TYPE_MOUSE and BUTTON_PRIMARY). This call enforces the android.permission.INJECT_EVENTS permission, which has protectionLevel='signature'. Only apps signed with the device manufacturer's OS platform key can hold this permission.",
            androidConstraint = "Declaring <uses-permission android:name=\"android.permission.INJECT_EVENTS\"/> in the manifest will simply be ignored or rejected by the Android Package Manager on non-system apps.",
            recommendation = "To achieve native injection without root, users must use ADB command bridges (such as Shizuku running adb shell input tap/mouse) or dedicated hardware macro dongles (USB passthrough)."
        ),
        FeasibilityItem(
            title = "AccessibilityService.dispatchGesture() vs Native Mouse",
            status = AuditStatus.RESTRICTED_BY_OS,
            summary = "dispatchGesture() injects simulated touchscreen paths, NOT native USB mouse packets.",
            technicalDetails = "Android AccessibilityService provides dispatchGesture(GestureDescription, ...). This API was designed for motor-impaired users to simulate single-finger touches and swipe paths on the touchscreen. It generates simulated touch events with TOOL_TYPE_UNKNOWN / touchscreen coordinates. It CANNOT emit native USB HID packets or mouse button down/up signals.",
            androidConstraint = "Minecraft Bedrock reacts differently to touch events vs mouse lock. In mouse mode, Minecraft captures the hardware cursor (Pointer Capture) and expects relative raw delta movement (AXIS_RELATIVE_X / Y) and BUTTON_PRIMARY. Touchscreen taps via Accessibility disrupt camera tracking or get ignored.",
            recommendation = "GamerBoyVashu Auto Clicker will never deceptively claim dispatchGesture is a native mouse clicker."
        ),
        FeasibilityItem(
            title = "Real-time Hardware Pointer Position Tracking",
            status = AuditStatus.IMPOSSIBLE_IN_USERLAND,
            summary = "Android does not expose the current (X, Y) coordinate of the hardware mouse pointer across the system to background apps.",
            technicalDetails = "The system mouse cursor is rendered by the Android SurfaceFlinger / WindowManager compositing pipeline. For privacy and security (anti-keylogging, anti-clickjacking), Android strictly forbids third-party apps from reading cursor coordinates or screen pixel contents of other applications.",
            androidConstraint = "When Minecraft enters Pointer Lock (requestPointerCapture()), the OS cursor is hidden and relative movement deltas are streamed directly into Minecraft's native C++ engine. No background app can query where the cursor is pointing.",
            recommendation = "Any clicker claiming to 'follow the mouse cursor automatically' across other apps without root or screen-mirroring hooks is physically impossible on standard Android."
        ),
        FeasibilityItem(
            title = "USB OTG Device Detection & Status",
            status = AuditStatus.SUPPORTED_IN_APP,
            summary = "Android's UsbManager and InputManager can fully detect connected physical USB mice, vendor IDs, and device descriptors.",
            technicalDetails = "Using android.hardware.usb.UsbManager and android.view.InputDevice, the application can enumerate connected USB devices, identify HID peripherals (Class 3, Subclass 1, Protocol 2 = Mouse), and query InputDevice.SOURCE_MOUSE.",
            androidConstraint = "While the app can detect that a physical mouse is plugged in via OTG, having USB Host access does not grant permission to re-inject virtual inputs into the OS kernel without /dev/uinput (root required).",
            recommendation = "Use GamerBoyVashu Auto Clicker's live hardware status card to verify your OTG connection and troubleshoot peripheral recognition."
        ),
        FeasibilityItem(
            title = "Emergency Stop & Floating Pill Safety Guard",
            status = AuditStatus.SUPPORTED_IN_APP,
            summary = "A floating pill overlay with an instant emergency stop button can be safely rendered on top of Minecraft.",
            technicalDetails = "Using android.permission.SYSTEM_ALERT_WINDOW, the app can render a tiny, non-intrusive floating pill on top of games. It features an instant emergency kill-switch that stops any timers, loops, or background tasks immediately without obstructing your view.",
            androidConstraint = "The floating pill must respect WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL and FLAG_NOT_FOCUSABLE so that keyboard and mouse input continue to reach Minecraft uninterrupted.",
            recommendation = "Keep the floating pill enabled on screen edges so you have immediate one-tap emergency shutdown at all times."
        )
    )
}
