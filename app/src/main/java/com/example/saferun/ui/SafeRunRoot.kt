package com.example.saferun.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.saferun.R
import com.example.saferun.ui.dashboard.DashboardScreen
import com.example.saferun.ui.scanner.ScannerScreen
import com.example.saferun.ui.settings.SettingsScreen
import com.example.saferun.ui.theme.SafeRunTheme

enum class SafeRunDestination(
    val labelRes: Int,
    val icon: ImageVector,
) {
    Live(R.string.nav_live, Icons.Filled.PlayArrow),
    Scanner(R.string.nav_scanner, Icons.Filled.Search),
    Settings(R.string.nav_settings, Icons.Filled.Settings),
}

/**
 * Single activity entry point. Keeps the destination state in Compose, applies the
 * SafeRun theme (including the manual night preview) and hosts the bottom navigation.
 */
@Composable
fun SafeRunRoot() {
    var destination by rememberSaveable { mutableStateOf(SafeRunDestination.Live) }
    var nightMode by rememberSaveable { mutableStateOf(false) }

    SafeRunTheme(night = nightMode) {
        BackHandler(enabled = destination != SafeRunDestination.Live) {
            destination = SafeRunDestination.Live
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        ),
                    ),
                ),
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    SafeRunBottomBar(
                        current = destination,
                        onSelect = { destination = it },
                    )
                },
            ) { innerPadding ->
                AnimatedContent(
                    targetState = destination,
                    modifier = Modifier.padding(innerPadding),
                    transitionSpec = {
                        val enter = fadeIn(animationSpec = tween(220)) +
                            slideInVertically(animationSpec = tween(260)) { it / 18 }
                        val exit = fadeOut(animationSpec = tween(150)) +
                            slideOutVertically(animationSpec = tween(200)) { -it / 24 }
                        enter togetherWith exit
                    },
                    label = "safeRunScreen",
                ) { target ->
                    when (target) {
                        SafeRunDestination.Live -> DashboardScreen(
                            onOpenSettings = { destination = SafeRunDestination.Settings },
                        )

                        SafeRunDestination.Scanner -> ScannerScreen()

                        SafeRunDestination.Settings -> SettingsScreen(
                            nightMode = nightMode,
                            onNightModeChange = { nightMode = it },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SafeRunBottomBar(
    current: SafeRunDestination,
    onSelect: (SafeRunDestination) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
        tonalElevation = 2.dp,
        shadowElevation = 10.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SafeRunDestination.entries.forEach { destination ->
                val selected = destination == current
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            onClick = { onSelect(destination) },
                        )
                        .background(
                            if (selected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                Color.Transparent
                            },
                        )
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = null,
                        tint = if (selected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = stringResource(destination.labelRes),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
