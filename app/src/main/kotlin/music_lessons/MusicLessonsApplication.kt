package music_lessons

import android.app.Application
import androidx.room.Room
import music_lessons.repository.AppDatabase

class MusicLessonsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DB_NAME).fallbackToDestructiveMigration().build()
    }

    companion object {
        lateinit var db: AppDatabase
            private set
        private const val DB_NAME = "school"
    }
}
