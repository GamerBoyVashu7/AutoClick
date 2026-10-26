package com.example.model

data class InputEventSnapshot(
    val id: Long = System.currentTimeMillis(),
    val timeFormatted: String,
    val actionName: String,
    val buttonStateText: String,
    val isPrimaryPressed: Boolean,
    val isSecondaryPressed: Boolean,
    val isTertiaryPressed: Boolean,
    val x: Float,
    val y: Float,
    val toolType: String,
    val source: String,
    val isHeld: Boolean = false
)
