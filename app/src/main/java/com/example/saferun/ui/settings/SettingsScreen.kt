package com.example.saferun.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.saferun.R
import com.example.saferun.ui.components.SafeRunCard
import com.example.saferun.ui.components.ScreenHeader
import com.example.saferun.ui.components.StatusChip
import com.example.saferun.ui.components.StatusTone

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    nightMode: Boolean = false,
    onNightModeChange: (Boolean) -> Unit = {},
) {
    var fallDetection by rememberSaveable { mutableStateOf(true) }
    var lowLight by rememberSaveable { mutableStateOf(true) }
    var compass by rememberSaveable { mutableStateOf(true) }

    val context = LocalContext.current
    val locationGranted = remember(context) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }
    val cameraGranted = remember(context) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
    }
    val versionName = remember(context) {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "0.2.0"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(10.dp))
        ScreenHeader(
            title = stringResource(R.string.settings_title),
            subtitle = stringResource(R.string.settings_subtitle),
        )
        Spacer(Modifier.height(18.dp))

        SectionHeader(
            title = stringResource(R.string.settings_security),
            badge = stringResource(R.string.scanner_badge),
        )
        Spacer(Modifier.height(8.dp))
        SafeRunCard(contentPadding = PaddingValues(18.dp)) {
            ToggleRow(
                title = stringResource(R.string.settings_detection_fall),
                description = stringResource(R.string.settings_detection_fall_desc),
                checked = fallDetection,
                onCheckedChange = { fallDetection = it },
            )
            Spacer(Modifier.height(10.dp))
            ToggleRow(
                title = stringResource(R.string.settings_low_light),
                description = stringResource(R.string.settings_low_light_desc),
                checked = lowLight,
                onCheckedChange = { lowLight = it },
            )
            Spacer(Modifier.height(10.dp))
            ToggleRow(
                title = stringResource(R.string.settings_compass),
                description = stringResource(R.string.settings_compass_desc),
                checked = compass,
                onCheckedChange = { compass = it },
            )
        }
        Spacer(Modifier.height(18.dp))

        SectionHeader(title = stringResource(R.string.settings_appearance))
        Spacer(Modifier.height(8.dp))
        SafeRunCard(contentPadding = PaddingValues(18.dp)) {
            ToggleRow(
                title = stringResource(R.string.settings_night_preview),
                description = stringResource(R.string.settings_night_preview_desc),
                checked = nightMode,
                onCheckedChange = onNightModeChange,
            )
        }
        Spacer(Modifier.height(18.dp))

        SectionHeader(title = stringResource(R.string.settings_permissions))
        Spacer(Modifier.height(8.dp))
        SafeRunCard(contentPadding = PaddingValues(18.dp)) {
            PermissionRow(
                label = stringResource(R.string.settings_permission_location),
                granted = locationGranted,
            )
            Spacer(Modifier.height(12.dp))
            PermissionRow(
                label = stringResource(R.string.settings_permission_camera),
                granted = cameraGranted,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.settings_permission_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(18.dp))

        SectionHeader(title = stringResource(R.string.settings_demo))
        Spacer(Modifier.height(8.dp))
        SafeRunCard(contentPadding = PaddingValues(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.settings_demo),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                StatusChip(
                    text = stringResource(R.string.dashboard_live),
                    tone = StatusTone.Positive,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.settings_demo_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(18.dp))

        SectionHeader(title = stringResource(R.string.settings_about))
        Spacer(Modifier.height(8.dp))
        SafeRunCard {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.settings_about_body),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.settings_version, versionName),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionHeader(title: String, badge: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (badge != null) {
            StatusChip(text = badge, tone = StatusTone.Neutral)
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun PermissionRow(
    label: String,
    granted: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        StatusChip(
            text = if (granted) "Concedido" else "Pendiente",
            tone = if (granted) StatusTone.Positive else StatusTone.Warning,
        )
    }
}
