package com.mstoyanov.myapplication

import android.app.Application
import com.mstoyanov.myapplication.dao.MusicLessonsDatabase

class MusicLessonsApplication : Application() {
    lateinit var db: MusicLessonsDatabase

    override fun onCreate() {
        super.onCreate()
        db = MusicLessonsDatabase.getDatabase(applicationContext)
    }
}
