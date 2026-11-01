package com.zeniquill.app.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WelcomePane(
    hasRecents: Boolean,
    onOpenFile: () -> Unit,
    onOpenRecents: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Filled.Description,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(16.dp))
        Text("ZeniQuill", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "轻量原生代码编辑器\nv0.1",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(32.dp))
        Button(onClick = onOpenFile) {
            Icon(Icons.Filled.FolderOpen, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("打开文件")
        }
        if (hasRecents) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onOpenRecents) {
                Text("最近文件")
            }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onOpenSettings) {
            Icon(Icons.Filled.Settings, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("设置")
        }
    }
}
