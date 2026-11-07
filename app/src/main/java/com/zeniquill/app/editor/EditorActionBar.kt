package com.zeniquill.app.editor

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.SelectionMovement

/**
 * A slim action strip shown above the soft keyboard (below the editor).
 * v0.1 scope: undo/redo, clipboard, cursor movement, common bracket pairs.
 */
@Composable
fun EditorActionBar(
    editor: CodeEditor?,
    modifier: Modifier = Modifier,
) {
    if (editor == null) return

    val clipboard = LocalClipboardManager.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .height(44.dp)
                .padding(horizontal = 2.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            ActionIconButton(Icons.Filled.Undo, "撤销") { if (editor.canUndo()) editor.undo() }
            ActionIconButton(Icons.Filled.Redo, "重做") { if (editor.canRedo()) editor.redo() }

            ActionIconButton(Icons.Filled.ContentCut, "剪切") {
                selectedText(editor)?.let {
                    clipboard.setText(AnnotatedString(it))
                    editor.deleteText()
                }
            }
            ActionIconButton(Icons.Filled.ContentCopy, "复制") {
                selectedText(editor)?.let { clipboard.setText(AnnotatedString(it)) }
            }
            ActionIconButton(Icons.Filled.ContentPaste, "粘贴") {
                clipboard.getText()?.let { editor.insertText(it.toString(), it.length) }
            }

            ActionIconButton(Icons.AutoMirrored.Filled.ArrowBack, "光标左移") {
                editor.moveSelection(SelectionMovement.LEFT)
            }
            ActionIconButton(Icons.AutoMirrored.Filled.ArrowForward, "光标右移") {
                editor.moveSelection(SelectionMovement.RIGHT)
            }

            BracketButton("{ }") { editor.insertText("{}", 1) }
            BracketButton("( )") { editor.insertText("()", 1) }
            BracketButton("[ ]") { editor.insertText("[]", 1) }
            BracketButton("\" \"") { editor.insertText("\"\"", 1) }
            BracketButton("' '") { editor.insertText("''", 1) }
            BracketButton("< >") { editor.insertText("<>", 1) }
            BracketButton("Tab") {
                editor.insertText("\t", 1)
            }
        }
    }
}

@Composable
private fun ActionIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(icon, contentDescription = contentDescription)
    }
}

@Composable
private fun BracketButton(
    label: String,
    onClick: () -> Unit,
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 2.dp),
    ) {
        androidx.compose.material3.Text(label)
    }
}

/** Return the currently selected text, or null when there is no selection. */
private fun selectedText(editor: CodeEditor): String? {
    val cursor = editor.text.cursor
    if (!cursor.isSelected) return null
    val content = editor.text
    val start = content.getIndexer().getCharPosition(cursor.leftLine, cursor.leftColumn).index
    val end = content.getIndexer().getCharPosition(cursor.rightLine, cursor.rightColumn).index
    return content.substring(start, end)
}
