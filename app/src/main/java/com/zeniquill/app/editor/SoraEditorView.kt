package com.zeniquill.app.editor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.zeniquill.app.highlight.TextMateRegistry
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.Magnifier

/**
 * Compose wrapper hosting a single sora-editor [CodeEditor] view.
 * The editor is a classic Android View; settings are re-applied on update.
 */
@Composable
fun SoraEditorView(
    tab: EditorTab,
    darkTheme: Boolean,
    fontSize: Int,
    tabWidth: Int,
    wordWrap: Boolean,
    showLineNumbers: Boolean,
    onContentChanged: (String) -> Unit,
) {
    val context = LocalContext.current
    // A new tab (different uri) must create a fresh editor instance.
    val editorKey = remember(tab.uri) { tab.uri.toString() }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            CodeEditor(ctx).apply {
                typefaceText = android.graphics.Typeface.MONOSPACE
                setText(tab.initialContent)
                setEditorLanguage(TextMateRegistry.createLanguageBlocking(tab.language, ctx))
                subscribeEvent(io.github.rosemoe.sora.event.ContentChangeEvent::class.java) { event, _ ->
                    onContentChanged(event.editor.text.toString())
                }
                props.stickyScroll = false
                getComponent(Magnifier::class.java).isEnabled = true
            }
        },
        update = { editor ->
            editor.setTextSize(fontSize.toFloat())
            editor.setTabWidth(tabWidth)
            editor.setWordwrap(wordWrap)
            editor.setLineNumberEnabled(showLineNumbers)
            editor.setColorScheme(
                TextMateColorScheme.create(TextMateRegistry.createThemeBlocking(context, darkTheme))
            )
        },
        // `editorKey` forces recreation when the document changes (v0.1: single tab swap).
        key = editorKey,
    )
}
