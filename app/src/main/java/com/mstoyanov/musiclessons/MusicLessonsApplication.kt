package com.mstoyanov.musiclessons

import android.app.Application
import com.mstoyanov.musiclessons.dao.MusicLessonsDatabase

class MusicLessonsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        db = MusicLessonsDatabase.getDatabase(applicationContext)
    }

    companion object {
        lateinit var db: MusicLessonsDatabase
            private set
    }
}
