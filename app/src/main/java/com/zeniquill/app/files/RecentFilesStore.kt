package com.zeniquill.app.files

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val Context.recentsDataStore by preferencesDataStore(name = "zeniquill_recents")

/**
 * Recently opened files, persisted as encoded SAF URIs.
 * Capped at [MAX_RECENTS]; most recent first.
 */
object RecentFilesStore {

    private const val MAX_RECENTS = 20

    private object Keys {
        val URIS = stringPreferencesKey("recent_uris")
        val COUNT = intPreferencesKey("recent_count")
    }

    private val mutex = Mutex()
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    suspend fun recents(): List<Uri> {
        val ctx = appContext ?: return emptyList()
        val encoded = ctx.recentsDataStore.data.first()[Keys.URIS] ?: return emptyList()
        return encoded.split("\n").filter { it.isNotBlank() }.mapNotNull { raw ->
            runCatching { Uri.parse(raw) }.getOrNull()
        }
    }

    /** Move [uri] to the front of the recents list (adding it if absent). */
    suspend fun touch(uri: Uri) = mutex.withLock {
        val ctx = appContext ?: return@withLock
        val current = recents().map { it.toString() }.toMutableList()
        val key = uri.toString()
        current.remove(key)
        current.add(0, key)
        while (current.size > MAX_RECENTS) current.removeAt(current.size - 1)
        ctx.recentsDataStore.edit { it[Keys.URIS] = current.joinToString("\n") }
    }

    suspend fun remove(uri: Uri) = mutex.withLock {
        val ctx = appContext ?: return@withLock
        val current = recents().map { it.toString() }.toMutableList()
        current.remove(uri.toString())
        ctx.recentsDataStore.edit { it[Keys.URIS] = current.joinToString("\n") }
    }

    suspend fun clear() {
        val ctx = appContext ?: return
        ctx.recentsDataStore.edit { it[Keys.URIS] = "" }
    }
}
