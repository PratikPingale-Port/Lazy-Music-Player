package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    var gaplessEnabled by remember { mutableStateOf(true) }
    var pauseOnUnplug by remember { mutableStateOf(true) }
    var audioFocusDucking by remember { mutableStateOf(true) }
    var highPrecisionResampler by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Audio Pipeline & Engine Configuration",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        // Section: Audio & DSP Engine
        item {
            Text(
                text = "Audio & DSP Pipeline",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = CyanNeon
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceElevatedDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SettingToggleRow(
                        title = "Gapless Playback",
                        subtitle = "Continuous transition without silent gaps",
                        checked = gaplessEnabled,
                        onCheckedChange = { gaplessEnabled = it }
                    )

                    SettingToggleRow(
                        title = "High-Precision 32-bit Float Pipeline",
                        subtitle = "ExoPlayer native PCM rendering buffer",
                        checked = highPrecisionResampler,
                        onCheckedChange = { highPrecisionResampler = it }
                    )

                    SettingToggleRow(
                        title = "Audio Focus & Ducking",
                        subtitle = "Lower volume gracefully during navigation announcements",
                        checked = audioFocusDucking,
                        onCheckedChange = { audioFocusDucking = it }
                    )

                    SettingToggleRow(
                        title = "Headphone Disconnect Guard",
                        subtitle = "Instantly pause playback when unplugging headphones",
                        checked = pauseOnUnplug,
                        onCheckedChange = { pauseOnUnplug = it }
                    )
                }
            }
        }

        // Section: Codec & Supported Decoders
        item {
            Text(
                text = "Supported Codecs & Formats",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = CyanNeon
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceElevatedDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• FLAC (Free Lossless Audio Codec) - Up to 24-bit / 192kHz", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("• WAV / LPCM (Waveform Audio) - Bit-perfect PCM", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("• MP3 (MPEG Audio Layer III) - Full CBR / VBR bitrate", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("• AAC / M4A (Advanced Audio Coding) - Low Complexity", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("• OGG / Opus - Low latency speech & music", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }

        // Section: About
        item {
            Text(
                text = "About",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = CyanNeon
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceElevatedDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resonance Music Player", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    Text("Version 1.0.0 (Audiophile Build)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Original Android Native offline-first architecture with AndroidX Media3, 10-band graphic equalizer, Room persistence, and local MediaStore scanning.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyanNeon,
                checkedTrackColor = CyanNeon.copy(alpha = 0.3f),
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = SurfaceVariantDark
            )
        )
    }
}
