package com.zeniquill.app.files

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** A document opened through SAF. */
data class OpenedFile(
    val uri: Uri,
    val displayName: String,
    val content: String,
)

object SafFileStore {

    /**
     * Read a text document via SAF. Persistable permission is requested so the
     * file can be saved later without re-picking.
     */
    suspend fun open(context: Context, uri: Uri): OpenedFile = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }

        val displayName = queryDisplayName(context, uri)
        val content = context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.bufferedReader(Charsets.UTF_8).readText()
        } ?: ""

        OpenedFile(uri, displayName, content)
    }

    /** Write text back to the document. */
    suspend fun save(context: Context, uri: Uri, content: String): Unit = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(uri, "wt")?.use { stream ->
            stream.bufferedWriter(Charsets.UTF_8).use { writer ->
                writer.write(content)
            }
        } ?: throw IllegalStateException("Cannot open output stream for $uri")
    }

    fun queryDisplayName(context: Context, uri: Uri): String {
        return runCatching {
            context.contentResolver.query(
                uri,
                arrayOf(android.provider.OpenableColumns.DISPLAY_NAME),
                null, null, null
            )?.use { cursor ->
                val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (idx >= 0 && cursor.moveToFirst()) cursor.getString(idx) else null
            }
        }.getOrNull() ?: uri.lastPathSegment ?: "untitled"
    }
}
