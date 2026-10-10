package com.example.ui.screens.equalizer

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EQPreset
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun EqualizerScreen(
    viewModel: EqualizerViewModel,
    modifier: Modifier = Modifier
) {
    val effectState by viewModel.effectState.collectAsStateWithLifecycle()
    val presets by viewModel.presets.collectAsStateWithLifecycle()

    var showSaveDialog by remember { mutableStateOf(false) }
    var newPresetName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header & Master Switch
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "10-Band Graphic Equalizer",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Hardware DSP & Acoustic Engine",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Switch(
                    checked = effectState.isEnabled,
                    onCheckedChange = { viewModel.setEnabled(it) },
                    modifier = Modifier.testTag("eq_master_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyanNeon,
                        checkedTrackColor = CyanNeon.copy(alpha = 0.3f),
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = SurfaceVariantDark
                    )
                )
            }
        }

        // Presets Selector Carousel
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Acoustic Presets",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row {
                    OutlinedButton(
                        onClick = { viewModel.resetToFlat() },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Flat", fontSize = 11.sp, color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Button(
                        onClick = { showSaveDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = ObsidianBlack)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save", fontSize = 11.sp, color = ObsidianBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(presets) { preset ->
                    val isSelected = effectState.activePresetName == preset.name
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) CyanNeon else SurfaceVariantDark,
                        modifier = Modifier
                            .clickable { viewModel.selectPreset(preset) }
                            .testTag("preset_${preset.id}")
                    ) {
                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isSelected) ObsidianBlack else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Preamp Slider
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceElevatedDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Preamp Gain", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                        Text(
                            text = String.format(Locale.US, "%+.1f dB", effectState.preampGainDb),
                            color = AmberGlow,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Slider(
                        value = effectState.preampGainDb,
                        onValueChange = { viewModel.setPreamp(it) },
                        valueRange = -12f..12f,
                        enabled = effectState.isEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = AmberGlow,
                            activeTrackColor = AmberGlow,
                            inactiveTrackColor = SurfaceVariantDark
                        )
                    )
                }
            }
        }

        // 10 Frequency Bands
        item {
            Text(
                text = "10 Frequency Bands",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceElevatedDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    EQPreset.FREQUENCY_LABELS.forEachIndexed { index, label ->
                        val gain = effectState.bandGains.getOrElse(index) { 0f }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextSecondary,
                                modifier = Modifier.width(54.dp)
                            )
                            Slider(
                                value = gain,
                                onValueChange = { viewModel.setBandGain(index, it) },
                                valueRange = -12f..12f,
                                enabled = effectState.isEnabled,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("eq_band_$index"),
                                colors = SliderDefaults.colors(
                                    thumbColor = CyanNeon,
                                    activeTrackColor = CyanNeon,
                                    inactiveTrackColor = SurfaceVariantDark
                                )
                            )
                            Text(
                                text = String.format(Locale.US, "%+.1f", gain),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (gain != 0f) CyanNeon else TextSecondary,
                                modifier = Modifier.width(42.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Bass Boost & Virtualizer
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bass Boost Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceElevatedDark,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Bass Boost", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            Text("${effectState.bassBoost / 10}%", color = AmberGlow, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = effectState.bassBoost.toFloat(),
                            onValueChange = { viewModel.setBassBoost(it.toInt()) },
                            valueRange = 0f..1000f,
                            enabled = effectState.isEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = AmberGlow,
                                activeTrackColor = AmberGlow,
                                inactiveTrackColor = SurfaceVariantDark
                            )
                        )
                    }
                }

                // Virtualizer Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceElevatedDark,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Virtualizer", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            Text("${effectState.virtualizer / 10}%", color = CyanNeon, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = effectState.virtualizer.toFloat(),
                            onValueChange = { viewModel.setVirtualizer(it.toInt()) },
                            valueRange = 0f..1000f,
                            enabled = effectState.isEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = CyanNeon,
                                activeTrackColor = CyanNeon,
                                inactiveTrackColor = SurfaceVariantDark
                            )
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Save Preset Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Custom Preset") },
            text = {
                OutlinedTextField(
                    value = newPresetName,
                    onValueChange = { newPresetName = it },
                    label = { Text("Preset Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPresetName.isNotBlank()) {
                            viewModel.saveCustomPreset(newPresetName.trim())
                            newPresetName = ""
                            showSaveDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon)
                ) {
                    Text("Save", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
