package com.zeniquill.app.highlight

import android.content.Context
import android.util.Log
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.DefaultGrammarDefinition
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.eclipse.tm4e.core.registry.IGrammarSource
import org.eclipse.tm4e.core.registry.IThemeSource

/**
 * Central bootstrap for TextMate grammars and editor themes.
 * Asset layout mirrors Xed-Editor (GPL-3.0): assets/textmate/<lang>/syntaxes/<name>.tmLanguage.json
 */
object TextMateRegistry {

    private const val TAG = "TextMateRegistry"
    private const val DARK_THEME = "darcula"
    private const val LIGHT_THEME = "quietlight"

    private val initMutex = Mutex()
    private var initialized = false

    suspend fun init(context: Context) = initMutex.withLock {
        if (initialized) return@withLock

        withContext(Dispatchers.IO) {
            FileProviderRegistry.getInstance().addFileProvider(
                AssetsFileResolver(context.assets)
            )

            val definitions = ZeniLanguage.entries.map { lang ->
                context.assets.open(lang.grammarPath).use { stream ->
                    DefaultGrammarDefinition.withLanguageConfiguration(
                        IGrammarSource.fromInputStream(
                            stream,
                            lang.grammarPath,
                            Charsets.UTF_8,
                        ),
                        "textmate/${lang.id}/language-configuration.json",
                        lang.id,
                        lang.scopeName,
                    )
                }
            }

            definitions.forEach { def ->
                runCatching { GrammarRegistry.getInstance().loadGrammar(def) }
                    .onFailure { e -> Log.w(TAG, "grammar load failed: ${def.name}", e) }
            }

            initialized = true
        }
    }

    fun createLanguage(lang: ZeniLanguage): TextMateLanguage =
        TextMateLanguage.create(lang.scopeName, true)

    fun createLanguageBlocking(lang: ZeniLanguage, context: Context): TextMateLanguage {
        kotlinx.coroutines.runBlocking { init(context) }
        return createLanguage(lang)
    }

    suspend fun createTheme(context: Context, dark: Boolean): ThemeModel =
        withContext(Dispatchers.IO) {
            val name = if (dark) DARK_THEME else LIGHT_THEME
            val path = themeAssetPath(dark)
            val themeSource = IThemeSource.fromInputStream(
                context.assets.open(path),
                path,
                null
            )
            ThemeModel(themeSource, name).apply { setDark(dark) }
        }

    fun createThemeBlocking(context: Context, dark: Boolean): ThemeModel =
        kotlinx.coroutines.runBlocking { createTheme(context, dark) }

    fun themeAssetPath(dark: Boolean): String =
        "textmate/" + (if (dark) DARK_THEME else LIGHT_THEME) + ".json"
}
