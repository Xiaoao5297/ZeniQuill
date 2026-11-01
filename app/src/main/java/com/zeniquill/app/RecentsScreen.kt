package com.zeniquill.app

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.zeniquill.app.editor.EditorViewModel
import com.zeniquill.app.files.SafFileStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentsScreen(
    viewModel: EditorViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val recents by viewModel.recents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("最近文件") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
            )
        }
    ) { padding ->
        if (recents.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
            ) {
                Text(
                    "暂无最近文件",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(recents, key = { it.toString() }) { uri ->
                    ListItem(
                        headlineContent = { Text(displayNameFor(context, uri, viewModel)) },
                        leadingContent = {
                            Icon(Icons.Filled.Description, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.openUri(context, uri)
                                onBack()
                            },
                    )
                }
            }
        }
    }
}

@Composable
private fun displayNameFor(context: Context, uri: android.net.Uri, viewModel: EditorViewModel): String {
    // Query synchronously via remember; SAF display names are cheap to look up per item.
    return androidx.compose.runtime.remember(uri) {
        SafFileStore.queryDisplayName(context, uri)
    }
}
