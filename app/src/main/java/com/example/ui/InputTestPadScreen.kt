package com.example.ui

import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.InputEventSnapshot
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BedrockSurface
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.RedstoneError
import com.example.ui.theme.SlateOutline
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputTestPadScreen(
    viewModel: AutoClickerViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHoldingButton1 by viewModel.isHoldingButton1.collectAsState()
    val isServiceActive by viewModel.isServiceActive.collectAsState()
    val simulatedClicks by viewModel.inAppSimulatedClickCount.collectAsState()
    val clickIntervalMs by viewModel.clickIntervalMs.collectAsState()
    val recentEvents by viewModel.recentEvents.collectAsState()

    val holdBadgeColor by animateColorAsState(
        targetValue = if (isHoldingButton1) EmeraldPrimary else SlateOutline,
        label = "holdBadgeColor"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Input Diagnostic Pad",
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Dashboard",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.clearLogs() },
                        modifier = Modifier.testTag("clear_logs_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Event Logs",
                            tint = DiamondCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BedrockSurface)
            )
        },
        containerColor = ObsidianDark,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Test Pad Canvas Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BedrockSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(2.dp, holdBadgeColor, RoundedCornerShape(16.dp))
                    .testTag("interactive_test_pad")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Native Android View hook for capturing MotionEvent
                    AndroidView(
                        factory = { ctx ->
                            FrameLayout(ctx).apply {
                                isFocusable = true
                                isFocusableInTouchMode = true
                                setOnTouchListener { _, event ->
                                    viewModel.handlePadMotionEvent(event)
                                    true
                                }
                                setOnGenericMotionListener { _, event ->
                                    viewModel.handlePadMotionEvent(event)
                                    true
                                }
                                setOnHoverListener { _, event ->
                                    viewModel.handlePadMotionEvent(event)
                                    true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay HUD inside test pad
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mouse,
                            contentDescription = null,
                            tint = if (isHoldingButton1) EmeraldPrimary else TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isHoldingButton1) "BUTTON 1 (LEFT CLICK) HELD" else "CLICK OR HOLD MOUSE HERE",
                            color = if (isHoldingButton1) EmeraldPrimary else TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isServiceActive)
                                "Armed loop: ${simulatedClicks} simulated pulses at ${clickIntervalMs}ms"
                            else
                                "Service Disarmed • Left, Right, & Scroll inputs accepted",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status notice
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = AmberWarning,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Android isolates events to the focused window. When Minecraft is opened, Android stops routing pointer events here.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Event Logs Section Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "LIVE MOTIONEVENT STREAM (${recentEvents.size})",
                    color = DiamondCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Auto-scrolling",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Log Items List
            if (recentEvents.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BedrockSurface)
                ) {
                    Text(
                        text = "No mouse motion events captured yet.\nConnect USB mouse and click inside the box above.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(recentEvents, key = { it.id }) { item ->
                        EventLogRow(item)
                    }
                }
            }
        }
    }
}

@Composable
fun EventLogRow(item: InputEventSnapshot) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BedrockSurface),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateOutline, RoundedCornerShape(8.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (item.isPrimaryPressed) EmeraldPrimary else DiamondCyan)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.actionName,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = item.timeFormatted,
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Btn: ${item.buttonStateText} | Tool: ${item.toolType}",
                        color = if (item.isPrimaryPressed) EmeraldPrimary else TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "(${item.x.toInt()}, ${item.y.toInt()})",
                        color = DiamondCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
