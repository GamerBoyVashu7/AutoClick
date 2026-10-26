package com.example.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AuditStatus
import com.example.model.FeasibilityData
import com.example.model.FeasibilityItem
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
fun FeasibilityScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Android Input Security Audit",
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BedrockSurface)
            )
        },
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
            // Executive Verdict Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BedrockSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, RedstoneError, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = RedstoneError,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "EXECUTIVE TECHNICAL VERDICT",
                                color = RedstoneError,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "A standard, non-rooted Android app CANNOT intercept background USB mouse Button 1 events or inject native mouse clicks into Minecraft Bedrock.",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Android's Linux input subsystem routes pointer events exclusively to the focused window. Furthermore, native mouse injection requires the INJECT_EVENTS permission, which is restricted to system firmware keys. AccessibilityService.dispatchGesture() only creates simulated touchscreen taps, not native mouse packets.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Real-Device Verification Checklist Table
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BedrockSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateOutline, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "REAL-DEVICE FEASIBILITY MATRIX",
                            color = DiamondCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        VerificationRow("USB OTG Mouse Detection", true, "Fully supported via UsbManager & InputDevice")
                        VerificationRow("Button 1 Detection in App Pad", true, "Works when GamerBoyVashu has window focus")
                        VerificationRow("Button 1 Detection in Minecraft", false, "BLOCKED by InputDispatcher window isolation")
                        VerificationRow("Native Mouse Event Injection", false, "BLOCKED: INJECT_EVENTS is signature-only")
                        VerificationRow("Cursor-following in Minecraft", false, "BLOCKED: OS hides pointer in Pointer Capture")
                        VerificationRow("Accessibility Touch Simulation", false, "Incompatible with Minecraft mouse look")
                        VerificationRow("Floating Pill & Emergency Stop", true, "Fully supported via SYSTEM_ALERT_WINDOW")
                    }
                }
            }

            // Detailed Technical Topics Section
            item {
                Text(
                    text = "DEEP TECHNICAL INVESTIGATION",
                    color = EmeraldPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            items(FeasibilityData.items) { item ->
                FeasibilityAccordionCard(item)
            }
        }
    }
}

@Composable
fun VerificationRow(item: String, isSupported: Boolean, note: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSupported) EmeraldPrimary.copy(alpha = 0.2f) else RedstoneError.copy(alpha = 0.2f))
        ) {
            Icon(
                imageVector = if (isSupported) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (isSupported) EmeraldPrimary else RedstoneError,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = note,
                color = if (isSupported) TextSecondary else RedstoneError.copy(alpha = 0.9f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun FeasibilityAccordionCard(item: FeasibilityItem) {
    var expanded by remember { mutableStateOf(false) }

    val statusColor = when (item.status) {
        AuditStatus.SUPPORTED_IN_APP -> EmeraldPrimary
        AuditStatus.RESTRICTED_BY_OS -> AmberWarning
        AuditStatus.REQUIRES_PRIVILEGED_ROOT_OR_ADB -> DiamondCyan
        AuditStatus.IMPOSSIBLE_IN_USERLAND -> RedstoneError
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = BedrockSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateOutline, RoundedCornerShape(14.dp))
            .clickable { expanded = !expanded }
            .testTag("feasibility_card_${item.title.hashCode()}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.summary,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Technical Architecture:",
                        color = DiamondCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.technicalDetails,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Android OS Limitation:",
                        color = AmberWarning,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.androidConstraint,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Engineer Recommendation:",
                        color = EmeraldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.recommendation,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
