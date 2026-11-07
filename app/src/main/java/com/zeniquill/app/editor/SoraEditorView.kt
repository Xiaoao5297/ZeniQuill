package com.zeniquill.app.editor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.zeniquill.app.highlight.TextMateRegistry
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.Magnifier
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

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
    onEditorReady: (CodeEditor) -> Unit = {},
) {
    val context = LocalContext.current
    // A new tab (different uri) must create a fresh editor instance.
    val editorKey = remember(tab.uri) { tab.uri.toString() }

    key(editorKey) {
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
                    // No cursor move animation (user preference: jump instantly).
                    setCursorAnimationEnabled(false)
                    // Put a bit of breathing room between line numbers and the screen edge.
                    val dp = resources.displayMetrics.density
                    setDividerMargin(4f * dp, 4f * dp)
                    // Slim, semi-transparent scrollbar thumb with a rounded look.
                    setVerticalScrollbarThumbDrawable(ThumbDrawable())
                    // Faint full-height track behind the thumb while scrolling.
                    setVerticalScrollbarTrackDrawable(TrackDrawable())
                    onEditorReady(this)
                }
            },
            update = { editor ->
                editor.setTextSize(fontSize.toFloat())
                editor.setTabWidth(tabWidth)
                editor.setWordwrap(wordWrap)
                editor.setLineNumberEnabled(showLineNumbers)
                editor.setColorScheme(buildColorScheme(context, darkTheme))
                onEditorReady(editor)
            },
        )
    }
}

/**
 * Base TextMate scheme patched with ZeniQuill's visual preferences:
 * - matched bracket pair highlighted with an accent color instead of black
 * - soft light-gray indent guides instead of purple
 * - subtle scrollbar track behind the thumb
 */
private fun buildColorScheme(context: android.content.Context, darkTheme: Boolean): TextMateColorScheme {
    val scheme = TextMateColorScheme.create(TextMateRegistry.createThemeBlocking(context, darkTheme))
    val accent = if (darkTheme) Color.parseColor("#82AAFF") else Color.parseColor("#296DFF")
    val indentGuide = if (darkTheme) 0x3DFFFFFF.toInt() else 0x24000000.toInt()

    scheme.setColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_FOREGROUND, accent)
    scheme.setColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_UNDERLINE, Color.TRANSPARENT)
    scheme.setColor(EditorColorScheme.BLOCK_LINE, indentGuide)
    scheme.setColor(EditorColorScheme.BLOCK_LINE_CURRENT, if (darkTheme) 0x66FFFFFF.toInt() else 0x4D000000.toInt())
    scheme.setColor(EditorColorScheme.SCROLL_BAR_TRACK, if (darkTheme) 0x14FFFFFF.toInt() else 0x0F000000.toInt())
    return scheme
}

/**
 * Rounded, slim scrollbar thumb. sora-editor draws the thumb with its own
 * width; our drawable only decides how the color looks (semi-transparent
 * rounded bar with fade behavior inherited from the editor's fade timer).
 */
private class ThumbDrawable : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = Color.WHITE
        paint.alpha = 110
    }

    override fun draw(canvas: Canvas) {
        val b = bounds
        val w = (b.width() * 0.55f).toInt().coerceAtLeast(1)
        val x = b.centerX()
        canvas.drawRoundRect(
            (x - w / 2f),
            b.top.toFloat(),
            (x + w / 2f),
            b.bottom.toFloat(),
            w / 2f,
            w / 2f,
            paint,
        )
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getAlpha(): Int = paint.alpha

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}

/**
 * Full-height scrollbar track shown while holding the thumb. Very faint so
 * it reads as a background rather than a hard divider.
 */
private class TrackDrawable : Drawable() {
    private val paint = Paint()

    init {
        paint.color = Color.WHITE
        paint.alpha = 24
    }

    override fun draw(canvas: Canvas) {
        val b = bounds
        val w = (b.width() * 0.55f).coerceAtLeast(1f)
        val x = b.centerX()
        canvas.drawRoundRect(
            (x - w / 2f),
            b.top.toFloat(),
            (x + w / 2f),
            b.bottom.toFloat(),
            w / 2f,
            w / 2f,
            paint,
        )
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getAlpha(): Int = paint.alpha

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
