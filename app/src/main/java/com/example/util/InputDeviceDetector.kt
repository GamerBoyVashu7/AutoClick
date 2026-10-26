package com.example.util

import android.content.Context
import android.hardware.input.InputManager
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.view.InputDevice
import com.example.model.MouseInfo
import com.example.model.SingleDeviceInfo

object InputDeviceDetector {

    fun scanDevices(context: Context): MouseInfo {
        val inputManager = context.getSystemService(Context.INPUT_SERVICE) as? InputManager
        val usbManager = context.getSystemService(Context.USB_SERVICE) as? UsbManager

        val detectedList = mutableListOf<SingleDeviceInfo>()
        var mouseFound = false
        var primaryName = "No Physical Mouse Detected"
        var primaryVendor = 0
        var primaryProduct = 0
        var hasRelative = false
        var hasScroll = false
        var sourceDesc = "None"

        // 1. Check InputDevice IDs
        val deviceIds = InputDevice.getDeviceIds()
        for (id in deviceIds) {
            val device = InputDevice.getDevice(id) ?: continue
            val sources = device.sources
            val isMouse = (sources and InputDevice.SOURCE_MOUSE) != 0
            val isTouchpad = (sources and InputDevice.SOURCE_TOUCHPAD) != 0
            val isExternal = device.isExternal

            val sourceNames = mutableListOf<String>()
            if (isMouse) sourceNames.add("MOUSE")
            if (isTouchpad) sourceNames.add("TOUCHPAD")
            if ((sources and InputDevice.SOURCE_KEYBOARD) != 0) sourceNames.add("KEYBOARD")
            if ((sources and InputDevice.SOURCE_JOYSTICK) != 0) sourceNames.add("JOYSTICK")
            if ((sources and InputDevice.SOURCE_TOUCHSCREEN) != 0) sourceNames.add("TOUCHSCREEN")

            val singleInfo = SingleDeviceInfo(
                id = device.id,
                name = device.name,
                isExternal = isExternal,
                isVirtual = device.isVirtual,
                sources = sourceNames.joinToString(", ").ifEmpty { "OTHER" },
                vendorId = device.vendorId,
                productId = device.productId
            )
            detectedList.add(singleInfo)

            if ((isMouse || (isExternal && !device.isVirtual)) && !mouseFound) {
                mouseFound = true
                primaryName = device.name
                primaryVendor = device.vendorId
                primaryProduct = device.productId
                hasRelative = device.getMotionRange(InputDevice.MOTION_RANGE_X) != null
                hasScroll = (sources and InputDevice.SOURCE_ROTARY_ENCODER) != 0 || isMouse
                sourceDesc = sourceNames.joinToString(" | ")
            }
        }

        // 2. Also cross-reference UsbManager for attached OTG peripherals
        var usbOtgConnected = false
        if (usbManager != null) {
            val deviceList: Map<String, UsbDevice> = usbManager.deviceList
            for ((_, usbDev) in deviceList) {
                var isHidMouse = false
                for (i in 0 until usbDev.interfaceCount) {
                    val intf = usbDev.getInterface(i)
                    if (intf.interfaceClass == UsbConstants.USB_CLASS_HID) {
                        // HID protocol 2 is mouse
                        if (intf.interfaceProtocol == 2 || intf.interfaceSubclass == 1) {
                            isHidMouse = true
                        }
                    }
                }
                if (isHidMouse || usbDev.deviceClass == UsbConstants.USB_CLASS_HID) {
                    usbOtgConnected = true
                    if (!mouseFound) {
                        mouseFound = true
                        primaryName = usbDev.productName ?: usbDev.deviceName
                        primaryVendor = usbDev.vendorId
                        primaryProduct = usbDev.productId
                        sourceDesc = "USB HID Mouse"
                    }
                }
            }
        }

        return MouseInfo(
            deviceName = if (mouseFound) primaryName else "No Physical Mouse Connected (OTG)",
            vendorId = primaryVendor,
            productId = primaryProduct,
            isUsbConnected = mouseFound || usbOtgConnected,
            hasRelativeAxes = hasRelative,
            hasScrollWheel = hasScroll,
            sourceMaskDescription = sourceDesc,
            connectedDevicesCount = detectedList.size,
            detectedMice = detectedList
        )
    }
}
