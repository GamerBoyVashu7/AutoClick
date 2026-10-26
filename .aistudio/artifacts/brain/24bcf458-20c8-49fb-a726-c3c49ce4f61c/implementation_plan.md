# GamerBoyVashu Auto Clicker for Minecraft PE Android

An input-system utility and diagnostics suite engineered for Minecraft PE / Bedrock players using physical USB OTG mice on Android. It provides transparent analysis of Android input security boundaries, real-time USB hardware detection, an interactive input event testing workbench, and configurable interval controls with an emergency kill-switch floating pill.

## User Review & Critical Decisions

> [!IMPORTANT]
> **Android OS Security & Technical Feasibility Finding:**
> On standard, non-rooted Android devices without system platform signatures or Shizuku/ADB:
> 1. **Global Background Mouse Button Interception:** Android does *not* deliver `MotionEvent` (Button 1 down/up) of a physical mouse to background apps or standard `AccessibilityService` when another app (Minecraft Bedrock) is in the foreground. `AccessibilityService.onKeyEvent` only intercepts keyboard keys, not pointer motion or mouse button masks (`BUTTON_PRIMARY`).
> 2. **Native Mouse Event Injection (`TOOL_TYPE_MOUSE`):** Injecting native mouse events requires `android.permission.INJECT_EVENTS`, which is protected at `signature` level (granted only to system OS firmware apps). Standard `AccessibilityService.dispatchGesture()` can only inject simulated touch paths (`TOOL_TYPE_UNKNOWN` or touchscreen touches), *not* true native mouse-button events with cursor-following coordinates.
> 3. **System Cursor Position Query:** Android's public SDK prevents third-party apps from querying the OS hardware mouse cursor coordinates when Minecraft captures pointer lock or has window focus.
> 
> Per your confirmed preference in Phase 1, **GamerBoyVashu Auto Clicker** will implement a **Full Diagnostic Suite with an honest feasibility report, USB OTG hardware analyzer, and interactive Input Tester**, rather than deceiving you with fake claims or hidden touch taps.

- **Confirmed Decision 1 (Handling Android OS Input Restrictions):** Full Diagnostic Suite with honest feasibility report and input tester. The app provides exact technical diagnostics, USB OTG connection monitoring, and live input event auditing inside the app window.
- **Confirmed Decision 2 (Overlay & Controls):** Minimal collapsible floating pill with quick toggle, click interval selector (50ms, 100ms, 200ms, 500ms), and prominent emergency kill-switch.

---

## 1. Overview & Core Concept

**GamerBoyVashu Auto Clicker** is designed specifically for Minecraft Bedrock mobile gamers with USB OTG mouse peripherals. Instead of masquerading touchscreen gestures as native mouse automation, it delivers:

1. **System Feasibility & Security Audit:** Clear, honest, technical breakdowns of Android's input pipeline (`/dev/input`, `EventHub`, `InputReader`, `InputDispatcher`, `AccessibilityService`, `INJECT_EVENTS`).
2. **USB OTG Mouse Hardware Detector:** Real-time enumeration of connected HID mouse and keyboard devices via Android `UsbManager` and `InputDevice` APIs.
3. **Interactive Input Event Diagnostic Pad:** An in-app testing canvas where users can click, hold Button 1, right-click, scroll, and test mouse events to inspect real `MotionEvent` stream details (tool type, button state, axis coordinates, pressure, and down time).
4. **Auto-Click Configuration Engine:** Click interval selector presets (50 ms, 100 ms, 200 ms, 500 ms) and custom sliders, hold-to-repeat state modeling, and failsafe monitors.
5. **Floating Overlay Pill:** Lightweight, draggable floating pill overlay providing instant access to service status and the emergency stop button over any game window without obscuring gameplay or drawing intrusive crosshairs.

---

## 2. User Experience & Visual Design

### Key User Flows

```
┌──────────────────┐     ┌───────────────────────┐     ┌──────────────────────┐
│  Launch Screen   │ ──> │ Dashboard & Status    │ ──> │ Floating Overlay     │
│  GamerBoyVashu   │     │ - USB Mouse: Connected│     │ - Collapsible Pill   │
│  Branded UI      │     │ - Input Audit: Active │     │ - Emergency Kill     │
└──────────────────┘     └───────────┬───────────┘     └──────────────────────┘
                                     │
                         ┌───────────┴───────────┐
                         ▼                       ▼
               ┌──────────────────┐    ┌──────────────────┐
               │ Feasibility &    │    │ Live Input Test  │
               │ Hardware Audit   │    │ Pad & Event Log  │
               └──────────────────┘    └──────────────────┘
```

1. **Dashboard (Main Hub):**
   - Branded header: **GamerBoyVashu Auto Clicker** with Minecraft-inspired high-contrast dark gaming aesthetics (deep slate `#0D1117`, emerald/creeper accent `#00E676`, diamond cyan `#00E5FF`, warning amber `#FFAB00`, and error crimson `#FF5252`).
   - USB Mouse Detection Card: Live status badge indicating whether a physical mouse is plugged in via OTG, showing vendor ID, product ID, device name, and supported input sources (`SOURCE_MOUSE`, `SOURCE_TOUCHPAD`).
   - Click Interval Tuner: Segmented buttons for `50ms` (20 CPS), `100ms` (10 CPS), `200ms` (5 CPS), `500ms` (2 CPS) with visual interval indicator.
   - Master Toggle & Emergency Stop: Large, high-priority emergency kill-switch that instantly kills any running loops and resets all state.

2. **Feasibility & Android Architecture Report:**
   - Interactive accordion explaining why Android blocks background native mouse injection, contrasting:
     - *Physical Mouse (`/dev/input/event*`)* vs *Touchscreen gestures (`dispatchGesture()`)*
     - Why non-rooted apps cannot obtain `INJECT_EVENTS`
     - How pointer lock works in Minecraft Bedrock vs Android window focus
   - Honest breakdown of requirements for native click injection (Root / Shizuku / ADB shell vs standard userland).

3. **Live Input Event Diagnostic Pad:**
   - Full interactive test pad where the user can click their physical OTG mouse.
   - Detects `BUTTON_PRIMARY` (Left click), `BUTTON_SECONDARY` (Right click), and `BUTTON_TERTIARY` (Middle click).
   - Measures hold duration, click release latency, and actual coordinates.
   - Proves whether Button 1 hold/release events are accessible in the active window.

4. **Floating Pill Overlay:**
   - Unobtrusive floating widget with pill-shaped handle.
   - Tap to expand: shows current interval and giant **EMERGENCY STOP** button.
   - No custom crosshair, no aiming dot, no screen obstruction.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Transparent Technical Truth over Deceptive Simulation**
  - *Chosen Approach:* Accurately document and demonstrate the Android input system constraints; provide real hardware detection and diagnostic event processing.
  - *Why:* Android OS architecture fundamentally prohibits background apps from receiving mouse motion and injecting native mouse events into Minecraft without root or platform keys. Fake claims violate user trust.
  - *Alternatives Considered:* Silently tapping random screen coordinates via `AccessibilityService.dispatchGesture()`—rejected because the user explicitly specified: *"AccessibilityService.dispatchGesture() is not a substitute for native mouse-button injection. Do not use touchscreen tapping and claim it is native mouse automation."*

- **Decision 2: Floating Pill Overlay instead of Targeting Crosshairs**
  - *Chosen Approach:* Draggable, minimal pill widget placed along screen edges.
  - *Why:* Honors the user's strict requirement: *"Do not draw, add, or simulate any cursor or crosshair. Use the existing Android/Minecraft pointer."*

- **Decision 3: Real USB OTG & InputDevice Hardware Telemetry**
  - *Chosen Approach:* Query `InputManager` and `UsbManager` for attached devices with `InputDevice.SOURCE_MOUSE`, reacting to broadcast intents when USB devices are attached/detached.
  - *Why:* Gives real-time feedback when the user connects or disconnects their OTG mouse.

---

## 4. Technical Architecture & Data Strategy

### System Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                        GamerBoyVashu Auto Clicker                      │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  ┌─────────────────────────┐          ┌─────────────────────────────┐  │
│  │   UI & Navigation       │          │   Floating Overlay Service  │  │
│  │  - DashboardScreen      │ ◄──────► │  - Minimal Floating Pill    │  │
│  │  - InputTestPadScreen   │          │  - WindowManager Overlay    │  │
│  │  - FeasibilityReport    │          │  - Emergency Kill-Switch    │  │
│  └───────────┬─────────────┘          └──────────────┬──────────────┘  │
│              │                                       │                 │
│              ▼                                       ▼                 │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                 AutoClickerViewModel & State                     │  │
│  │  - isServiceActive: StateFlow<Boolean>                           │  │
│  │  - clickIntervalMs: StateFlow<Long> (50, 100, 200, 500)          │  │
│  │  - mouseDeviceState: StateFlow<MouseDeviceStatus>                │  │
│  │  - liveEventLog: StateFlow<List<InputEventSnapshot>>             │  │
│  └──────────────────┬───────────────────────────────────────────────┘  │
│                     │                                                  │
│                     ▼                                                  │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                    Hardware & Telemetry Layer                    │  │
│  │  - UsbMouseDetector (UsbManager & InputManager)                  │  │
│  │  - InputDiagnosticsRepository (real MotionEvent analysis)        │  │
│  │  - AndroidSecurityFeasibilityChecker                             │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

### State Management & Components

- **`AutoClickerState`**:
  - `isServiceEnabled: Boolean`
  - `selectedIntervalMs: Long` (default `100L`, options `[50L, 100L, 200L, 500L]`)
  - `isMouseConnected: Boolean`
  - `mouseDetails: MouseInfo?` (Device Name, Vendor ID, Product ID, Source types)
  - `isOverlayActive: Boolean`
  - `emergencyStopTriggered: Boolean`
  - `activeEvents: List<InputLogEntry>`

- **Services & Permissions**:
  - `SYSTEM_ALERT_WINDOW` for the floating pill overlay.
  - Permission check & launch flows with user guidance.
  - Intent receiver for `ACTION_USB_DEVICE_ATTACHED` and `ACTION_USB_DEVICE_DETACHED`.
