package com.zeniquill.app.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.github.rosemoe.sora.widget.CodeEditor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onOpenFile: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenRecents: () -> Unit,
) {
    val context = LocalContext.current
    val tab by viewModel.tab.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val recents by viewModel.recents.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.toast) {
        viewModel.toast?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.consumeToast()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tab?.displayName ?: "ZeniQuill") },
                actions = {
                    if (tab != null) {
                        IconButton(onClick = { viewModel.save(context) }) {
                            Icon(Icons.Filled.Save, contentDescription = "保存")
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val currentTab = tab
            if (currentTab == null) {
                WelcomePane(
                    hasRecents = recents.isNotEmpty(),
                    onOpenFile = onOpenFile,
                    onOpenRecents = onOpenRecents,
                    onOpenSettings = onOpenSettings,
                )
            } else {
                val editorState = remember(currentTab.uri) { androidx.compose.runtime.mutableStateOf<CodeEditor?>(null) }
                Column {
                    SoraEditorView(
                        tab = currentTab,
                        darkTheme = settings.darkTheme,
                        fontSize = settings.fontSize,
                        tabWidth = settings.tabWidth,
                        wordWrap = settings.wordWrap,
                        showLineNumbers = settings.showLineNumbers,
                        onContentChanged = viewModel::updateContent,
                        onEditorReady = { editorState.value = it },
                    )
                    EditorActionBar(editor = editorState.value)
                }
            }
        }
    }
}
