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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.example.thismathinvaders.ViewModel.SettingsViewModel
import com.example.thismathinvaders.game.data.GameSettings
import androidx.compose.ui.platform.LocalConfiguration


// TODO - Refactor into element
@Composable
fun SettingScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Game Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Math Topics", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

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
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Number Range: ${settings.minNumberRange} to ${settings.maxNumberRange}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

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
        }

        val currentLocale = LocalConfiguration.current.locales[0]

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Invader Speed: ${String.format(currentLocale, "%.1fx", settings.speedMultiplier)}",
                        style = MaterialTheme.typography.titleMedium
                    )
                Spacer(modifier = Modifier.height(8.dp))

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
    }
}

@Composable
private fun SettingSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}