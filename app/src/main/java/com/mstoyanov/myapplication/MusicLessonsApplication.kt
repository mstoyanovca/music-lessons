package com.mstoyanov.myapplication

import android.app.Application
import com.mstoyanov.myapplication.dao.MusicLessonsDatabase

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
