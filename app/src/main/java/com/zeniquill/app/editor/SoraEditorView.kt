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
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.Magnifier
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

/**
 * TextMate scheme that re-applies ZeniQuill's visual preferences after every
 * theme load. TextMateColorScheme.setTheme() clears the color map and
 * re-applies the theme JSON (applyDefault), which would wipe any colors set
 * afterwards — including when the editor re-attaches the scheme. Overriding
 * applyDefault() guarantees our overrides survive every (re)load:
 * - matched bracket pair highlighted with an accent color instead of black
 * - soft light-gray indent guides instead of purple
 * - subtle scrollbar track behind the thumb
 */
private class ZeniColorScheme(
    themeModel: ThemeModel,
) : TextMateColorScheme(ThemeRegistry.getInstance(), themeModel) {

    override fun applyDefault() {
        // Theme JSON colors first...
        super.applyDefault()
        // ...then our preferences, so they always win over the theme.
        // Note: applyDefault() also runs from the base constructor, before
        // any Kotlin field of this class is initialized — so darkness is
        // derived from isDark() (backed by the base's currentTheme, which is
        // assigned before applyDefault() is invoked) instead of a field.
        val dark = isDark()
        val accent = if (dark) Color.parseColor("#82AAFF") else Color.parseColor("#296DFF")
        val indentGuide = if (dark) 0x3DFFFFFF.toInt() else 0x24000000.toInt()
        setColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_FOREGROUND, accent)
        setColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_UNDERLINE, Color.TRANSPARENT)
        setColor(EditorColorScheme.BLOCK_LINE, indentGuide)
        setColor(
            EditorColorScheme.BLOCK_LINE_CURRENT,
            if (dark) 0x66FFFFFF.toInt() else 0x4D000000.toInt(),
        )
        setColor(
            EditorColorScheme.SCROLL_BAR_TRACK,
            if (dark) 0x14FFFFFF.toInt() else 0x0F000000.toInt(),
        )
    }
}

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
                editor.setColorScheme(
                    ZeniColorScheme(TextMateRegistry.createThemeBlocking(context, darkTheme))
                )
                onEditorReady(editor)
            },
        )
    }
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
