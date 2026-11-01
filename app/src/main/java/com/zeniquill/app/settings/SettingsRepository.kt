package com.zeniquill.app.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "zeniquill_settings")

/** Editor preferences. Defaults chosen for a lightweight, sane editor. */
data class EditorSettings(
    val fontSize: Int = 14,
    val tabWidth: Int = 4,
    val useSpaces: Boolean = true,
    val wordWrap: Boolean = false,
    val showLineNumbers: Boolean = true,
    val darkTheme: Boolean = true,
)

object SettingsRepository {

    private object Keys {
        val FONT_SIZE = intPreferencesKey("font_size")
        val TAB_WIDTH = intPreferencesKey("tab_width")
        val USE_SPACES = booleanPreferencesKey("use_spaces")
        val WORD_WRAP = booleanPreferencesKey("word_wrap")
        val SHOW_LINE_NUMBERS = booleanPreferencesKey("show_line_numbers")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

    @Volatile
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun flow(): Flow<EditorSettings> = appContext!!.dataStore.data.map { prefs ->
        EditorSettings(
            fontSize = prefs[Keys.FONT_SIZE] ?: 14,
            tabWidth = prefs[Keys.TAB_WIDTH] ?: 4,
            useSpaces = prefs[Keys.USE_SPACES] ?: true,
            wordWrap = prefs[Keys.WORD_WRAP] ?: false,
            showLineNumbers = prefs[Keys.SHOW_LINE_NUMBERS] ?: true,
            darkTheme = prefs[Keys.DARK_THEME] ?: true,
        )
    }

    suspend fun current(): EditorSettings = flow().first()

    suspend fun setFontSize(v: Int) = appContext!!.dataStore.edit { it[Keys.FONT_SIZE] = v }
    suspend fun setTabWidth(v: Int) = appContext!!.dataStore.edit { it[Keys.TAB_WIDTH] = v }
    suspend fun setUseSpaces(v: Boolean) = appContext!!.dataStore.edit { it[Keys.USE_SPACES] = v }
    suspend fun setWordWrap(v: Boolean) = appContext!!.dataStore.edit { it[Keys.WORD_WRAP] = v }
    suspend fun setShowLineNumbers(v: Boolean) = appContext!!.dataStore.edit { it[Keys.SHOW_LINE_NUMBERS] = v }
    suspend fun setDarkTheme(v: Boolean) = appContext!!.dataStore.edit { it[Keys.DARK_THEME] = v }
}
