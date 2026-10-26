package com.example.model

data class MouseInfo(
    val deviceName: String = "No Mouse Detected",
    val vendorId: Int = 0,
    val productId: Int = 0,
    val isUsbConnected: Boolean = false,
    val hasRelativeAxes: Boolean = false,
    val hasScrollWheel: Boolean = false,
    val sourceMaskDescription: String = "None",
    val connectedDevicesCount: Int = 0,
    val detectedMice: List<SingleDeviceInfo> = emptyList()
)

data class SingleDeviceInfo(
    val id: Int,
    val name: String,
    val isExternal: Boolean,
    val isVirtual: Boolean,
    val sources: String,
    val vendorId: Int,
    val productId: Int
)
