package com.zeniquill.app.editor

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeniquill.app.files.OpenedFile
import com.zeniquill.app.files.RecentFilesStore
import com.zeniquill.app.files.SafFileStore
import com.zeniquill.app.highlight.ZeniLanguage
import com.zeniquill.app.settings.EditorSettings
import com.zeniquill.app.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** One open document tab (v0.1: single tab at a time). */
data class EditorTab(
    val uri: Uri,
    val displayName: String,
    val initialContent: String,
    val language: ZeniLanguage,
    var currentContent: String,
    var dirty: Boolean = false,
)

class EditorViewModel : ViewModel() {

    private val _tab = MutableStateFlow<EditorTab?>(null)
    val tab: StateFlow<EditorTab?> = _tab.asStateFlow()

    val settings: StateFlow<EditorSettings> = SettingsRepository.flow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, EditorSettings())

    val recents: StateFlow<List<Uri>> = MutableStateFlow<List<Uri>>(emptyList())
        .also { flow -> viewModelScope.launch { flow.emit(RecentFilesStore.recents()) } }
        .asStateFlow()

    var toast by mutableStateOf<String?>(null)
        private set

    fun consumeToast() {
        toast = null
    }

    fun refreshRecents() {
        viewModelScope.launch {
            (recents as MutableStateFlow<List<Uri>>).emit(RecentFilesStore.recents())
        }
    }

    fun openUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            runCatching {
                val opened: OpenedFile = SafFileStore.open(context, uri)
                RecentFilesStore.touch(uri)
                opened
            }.onSuccess { opened ->
                _tab.value = EditorTab(
                    uri = opened.uri,
                    displayName = opened.displayName,
                    initialContent = opened.content,
                    currentContent = opened.content,
                    language = ZeniLanguage.fromFileName(opened.displayName),
                )
                refreshRecents()
            }.onFailure { e ->
                toast = "打开失败: ${e.message}"
            }
        }
    }

    fun updateContent(content: String) {
        val t = _tab.value ?: return
        t.currentContent = content
        t.dirty = content != t.initialContent
    }

    fun save(context: Context) {
        val t = _tab.value ?: return
        viewModelScope.launch {
            runCatching {
                SafFileStore.save(context, t.uri, t.currentContent)
                t.initialContent = t.currentContent
                t.dirty = false
            }.onSuccess {
                toast = "已保存 ${t.displayName}"
            }.onFailure { e ->
                toast = "保存失败: ${e.message}"
            }
        }
    }

    fun closeTab() {
        _tab.value = null
    }
}
