package com.example.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun DashboardScreen(
    viewModel: AutoClickerViewModel,
    onNavigateToPad: () -> Unit,
    onNavigateToFeasibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mouseInfo by viewModel.mouseInfo.collectAsState()
    val isServiceActive by viewModel.isServiceActive.collectAsState()
    val clickIntervalMs by viewModel.clickIntervalMs.collectAsState()
    val isOverlayRunning by viewModel.isOverlayRunning.collectAsState()
    val emergencyStopTriggered by viewModel.emergencyStopTriggered.collectAsState()

    val statusColor by animateColorAsState(
        targetValue = when {
            emergencyStopTriggered -> RedstoneError
            isServiceActive -> EmeraldPrimary
            else -> AmberWarning
        },
        animationSpec = tween(300),
        label = "statusColor"
    )

    Scaffold(
        containerColor = ObsidianDark,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Branded Header
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SlateSurfaceVariant)
                            .border(1.5.dp, EmeraldPrimary, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mouse,
                            contentDescription = "Mouse Icon",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GamerBoyVashu",
                            color = EmeraldPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Minecraft Bedrock OTG Mouse Assistant",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(
                        onClick = { viewModel.refreshHardware() },
                        modifier = Modifier.testTag("refresh_hardware_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Peripherals",
                            tint = DiamondCyan
                        )
                    }
                }
            }

            // Status & Master Control Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BedrockSurface),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                        .testTag("status_control_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(statusColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when {
                                        emergencyStopTriggered -> "SYSTEM EMERGENCY STOPPED"
                                        isServiceActive -> "SERVICE ACTIVE (HOLD BUTTON 1)"
                                        else -> "SERVICE STANDBY / READY"
                                    },
                                    color = statusColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isServiceActive)
                                "Armed with ${clickIntervalMs}ms loop interval (${1000 / clickIntervalMs} CPS). In-app Button 1 hold triggers rapid repeat."
                            else
                                "Ready. Toggle on to activate hold-to-repeat trigger and test pad monitoring.",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.toggleService() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isServiceActive) SlateSurfaceVariant else EmeraldPrimary,
                                    contentColor = if (isServiceActive) TextPrimary else Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("master_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isServiceActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isServiceActive) "DISARM" else "ARM CLICKER",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Button(
                                onClick = { viewModel.triggerEmergencyStop() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RedstoneError,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("emergency_stop_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "KILL STOP",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // USB OTG Mouse Hardware Detection Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BedrockSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateOutline, RoundedCornerShape(16.dp))
                        .testTag("hardware_detection_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "USB OTG HARDWARE TELEMETRY",
                                color = DiamondCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (mouseInfo.isUsbConnected) EmeraldPrimary.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (mouseInfo.isUsbConnected) "MOUSE DETECTED" else "NO MOUSE PLUGGED",
                                    color = if (mouseInfo.isUsbConnected) EmeraldPrimary else AmberWarning,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = mouseInfo.deviceName,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Vendor ID: 0x${mouseInfo.vendorId.toString(16).padStart(4, '0').uppercase()}",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Product ID: 0x${mouseInfo.productId.toString(16).padStart(4, '0').uppercase()}",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Input Sources: ${mouseInfo.sourceMaskDescription}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )

                        if (!mouseInfo.isUsbConnected) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tip: Connect your USB mouse into an OTG adapter, plug it into your phone, then tap the Refresh button above.",
                                color = AmberWarning,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Click Interval Selector
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BedrockSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateOutline, RoundedCornerShape(16.dp))
                        .testTag("interval_selector_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "CLICK INTERVAL TUNER",
                                color = DiamondCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${clickIntervalMs} ms (~${1000 / clickIntervalMs} CPS)",
                                color = EmeraldPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Preset buttons
                        val intervals = listOf(50L to "50ms (20 CPS)", 100L to "100ms (10 CPS)", 200L to "200ms (5 CPS)", 500L to "500ms (2 CPS)")
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            intervals.forEach { (ms, label) ->
                                val isSelected = clickIntervalMs == ms
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmeraldPrimary else SlateSurfaceVariant)
                                        .border(
                                            1.dp,
                                            if (isSelected) EmeraldPrimary else SlateOutline,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.setClickInterval(ms) }
                                        .testTag("interval_${ms}ms_button")
                                ) {
                                    Text(
                                        text = "${ms}ms",
                                        color = if (isSelected) Color.Black else TextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Floating Pill Overlay Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BedrockSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateOutline, RoundedCornerShape(16.dp))
                        .testTag("floating_overlay_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "MINIMAL FLOATING PILL",
                                    color = DiamondCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Shows draggable pill with emergency stop over Minecraft without any crosshairs or cursor markers.",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = isOverlayRunning,
                                onCheckedChange = { viewModel.toggleOverlay(context) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = EmeraldPrimary,
                                    checkedTrackColor = EmeraldPrimary.copy(alpha = 0.3f),
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = SlateSurfaceVariant
                                ),
                                modifier = Modifier.testTag("overlay_switch")
                            )
                        }
                    }
                }
            }

            // Android Security Audit Alert
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AmberWarning.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .testTag("security_audit_alert_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = AmberWarning,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TECHNICAL FEASIBILITY AUDIT",
                                color = AmberWarning,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Official Android API constraint: Standard Android prevents background apps from intercepting global mouse clicks or injecting native TOOL_TYPE_MOUSE clicks into Minecraft without root or system keys.",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalButton(
                                onClick = onNavigateToPad,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("nav_test_pad_button")
                            ) {
                                Text("Test Mouse Pad", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = onNavigateToFeasibility,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("nav_feasibility_button")
                            ) {
                                Text("Read Full OS Audit", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
