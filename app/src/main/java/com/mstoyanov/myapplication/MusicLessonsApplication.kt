package com.mstoyanov.myapplication

import android.app.Application
import androidx.room.Room
import com.mstoyanov.myapplication.dao.MusicLessonsDatabase

class MusicLessonsApplication : Application() {
    val db: MusicLessonsDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            MusicLessonsDatabase::class.java,
            "school"
        ).build()
    }
}
