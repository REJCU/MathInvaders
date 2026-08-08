package com.example.thismathinvaders.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.thismathinvaders.ViewModel.SettingsViewModel

@Composable
fun SettingScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val currentLocale = LocalConfiguration.current.locales[0]

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "GAMEPLAY SETTINGS",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            SettingsSectionCard(title = "Math Topics") {
                SettingSwitchRow(
                    label = "Addition (+)",
                    checked = settings.allowAddition,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(allowAddition = it)) }
                )
                SettingSwitchRow(
                    label = "Subtraction (-)",
                    checked = settings.allowSubtraction,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(allowSubtraction = it)) }
                )
            }

            SettingsSectionCard(
                title = "Number Range",
                valueLabel = "${settings.minNumberRange} to ${settings.maxNumberRange}"
            ) {
                RangeSlider(
                    value = settings.minNumberRange.toFloat()..settings.maxNumberRange.toFloat(),
                    onValueChange = { range ->
                        viewModel.updateSettings(
                            settings.copy(
                                minNumberRange = range.start.toInt(),
                                maxNumberRange = range.endInclusive.toInt()
                            )
                        )
                    },
                    valueRange = 0f..100f
                )
            }

            SettingsSectionCard(
                title = "Invader Speed",
                valueLabel = String.format(currentLocale, "%.1fx", settings.speedMultiplier)
            ) {
                Slider(
                    value = settings.speedMultiplier,
                    onValueChange = {
                        viewModel.updateSettings(settings.copy(speedMultiplier = it))
                    },
                    valueRange = 0.5f..2.0f,
                    steps = 2
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "AUDIO SETTINGS",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Music Volume Card
            SettingsSectionCard(
                title = "Music Volume",
                valueLabel = "${(settings.musicVolume * 100).toInt()}%"
            ) {
                Slider(
                    value = settings.musicVolume,
                    onValueChange = {
                        viewModel.updateSettings(settings.copy(musicVolume = it))
                    },
                    valueRange = 0f..1f
                )
            }

            SettingsSectionCard(
                title = "Sound Effects Volume",
                valueLabel = "${(settings.soundVolume * 100).toInt()}%"
            ) {
                Slider(
                    value = settings.soundVolume,
                    onValueChange = {
                        viewModel.updateSettings(settings.copy(soundVolume = it))
                    },
                    valueRange = 0f..1f
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    valueLabel: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (valueLabel != null) {
                    Text(
                        text = valueLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun SettingSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}