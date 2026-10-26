package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BedrockSurface
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SlateOutline
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

enum class AppDestination(val label: String, val testTag: String) {
    DASHBOARD("Dashboard", "nav_tab_dashboard"),
    INPUT_TEST_PAD("Test Pad", "nav_tab_test_pad"),
    FEASIBILITY("OS Audit", "nav_tab_feasibility")
}

@Composable
fun MainAppScreen(
    viewModel: AutoClickerViewModel,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(AppDestination.DASHBOARD) }

    // Predictive back / BackHandler navigation
    BackHandler(enabled = currentDestination != AppDestination.DASHBOARD) {
        currentDestination = AppDestination.DASHBOARD
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = BedrockSurface,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentDestination == AppDestination.DASHBOARD,
                    onClick = { currentDestination = AppDestination.DASHBOARD },
                    icon = {
                        Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard")
                    },
                    label = {
                        Text(
                            text = "Dashboard",
                            fontSize = 11.sp,
                            fontWeight = if (currentDestination == AppDestination.DASHBOARD) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = SlateSurfaceVariant,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag(AppDestination.DASHBOARD.testTag)
                )

                NavigationBarItem(
                    selected = currentDestination == AppDestination.INPUT_TEST_PAD,
                    onClick = { currentDestination = AppDestination.INPUT_TEST_PAD },
                    icon = {
                        Icon(imageVector = Icons.Default.Mouse, contentDescription = "Test Pad")
                    },
                    label = {
                        Text(
                            text = "Test Pad",
                            fontSize = 11.sp,
                            fontWeight = if (currentDestination == AppDestination.INPUT_TEST_PAD) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DiamondCyan,
                        selectedTextColor = DiamondCyan,
                        indicatorColor = SlateSurfaceVariant,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag(AppDestination.INPUT_TEST_PAD.testTag)
                )

                NavigationBarItem(
                    selected = currentDestination == AppDestination.FEASIBILITY,
                    onClick = { currentDestination = AppDestination.FEASIBILITY },
                    icon = {
                        Icon(imageVector = Icons.Default.Security, contentDescription = "OS Audit")
                    },
                    label = {
                        Text(
                            text = "OS Audit",
                            fontSize = 11.sp,
                            fontWeight = if (currentDestination == AppDestination.FEASIBILITY) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = SlateSurfaceVariant,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag(AppDestination.FEASIBILITY.testTag)
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Crossfade(
            targetState = currentDestination,
            label = "screen_transition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { destination ->
            when (destination) {
                AppDestination.DASHBOARD -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToPad = { currentDestination = AppDestination.INPUT_TEST_PAD },
                        onNavigateToFeasibility = { currentDestination = AppDestination.FEASIBILITY }
                    )
                }
                AppDestination.INPUT_TEST_PAD -> {
                    InputTestPadScreen(
                        viewModel = viewModel,
                        onBack = { currentDestination = AppDestination.DASHBOARD }
                    )
                }
                AppDestination.FEASIBILITY -> {
                    FeasibilityScreen(
                        onBack = { currentDestination = AppDestination.DASHBOARD }
                    )
                }
            }
        }
    }
}
