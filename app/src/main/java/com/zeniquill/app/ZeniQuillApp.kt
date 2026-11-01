package com.zeniquill.app

import android.app.Application
import com.zeniquill.app.files.RecentFilesStore
import com.zeniquill.app.highlight.TextMateRegistry
import com.zeniquill.app.settings.SettingsRepository

class ZeniQuillApp : Application() {

    override fun onCreate() {
        super.onCreate()
        SettingsRepository.init(this)
        RecentFilesStore.init(this)
        // TextMateRegistry warms up lazily in MainActivity / on first editor creation.
    }
}
