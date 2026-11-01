package com.zeniquill.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zeniquill.app.settings.SettingsRepository

private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()

@Composable
fun ZeniQuillTheme(content: @Composable () -> Unit) {
    val settings by SettingsRepository.flow().collectAsState(
        initial = com.zeniquill.app.settings.EditorSettings()
    )
    // v0.1: explicit dark toggle from settings.
    MaterialTheme(
        colorScheme = if (settings.darkTheme) DarkColors else LightColors,
        content = content,
    )
}
