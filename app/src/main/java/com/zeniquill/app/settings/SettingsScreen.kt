package com.zeniquill.app.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
) {
    val settings by SettingsRepository.flow().collectAsState(initial = EditorSettings())
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            SettingHeader("编辑器")
            SliderSetting(
                title = "字号",
                value = settings.fontSize.toFloat(),
                valueRange = 8f..32f,
                steps = 23,
                onValueChange = { scope.launch { SettingsRepository.setFontSize(it.toInt()) } },
            )
            SliderSetting(
                title = "Tab 宽度",
                value = settings.tabWidth.toFloat(),
                valueRange = 1f..8f,
                steps = 6,
                onValueChange = { scope.launch { SettingsRepository.setTabWidth(it.toInt()) } },
            )
            SwitchSetting(
                title = "使用空格缩进",
                subtitle = "关闭时输入 Tab 字符",
                checked = settings.useSpaces,
                onCheckedChange = { scope.launch { SettingsRepository.setUseSpaces(it) } },
            )
            SwitchSetting(
                title = "自动换行",
                subtitle = "长行折行显示",
                checked = settings.wordWrap,
                onCheckedChange = { scope.launch { SettingsRepository.setWordWrap(it) } },
            )
            SwitchSetting(
                title = "显示行号",
                checked = settings.showLineNumbers,
                onCheckedChange = { scope.launch { SettingsRepository.setShowLineNumbers(it) } },
            )
            SettingHeader("外观")
            SwitchSetting(
                title = "深色主题",
                checked = settings.darkTheme,
                onCheckedChange = { scope.launch { SettingsRepository.setDarkTheme(it) } },
            )
        }
    }
}

@Composable
private fun SettingHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun SliderSetting(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("$title: ${value.toInt()}", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
        )
    }
}

@Composable
private fun SwitchSetting(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
