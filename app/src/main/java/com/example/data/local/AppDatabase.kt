package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ParentNotificationLog
import com.example.data.model.Student
import com.example.data.model.ViolationCategory
import com.example.data.model.ViolationMaster
import com.example.data.model.ViolationRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Student::class,
        ViolationCategory::class,
        ViolationMaster::class,
        ViolationRecord::class,
        ParentNotificationLog::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun violationDao(): ViolationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "skor_siswa_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        val violationDao = database.violationDao()
                        if (violationDao.getMasterCount() < 37) {
                            violationDao.insertAllCategories(DatabasePrepopulate.getDefaultCategories())
                            violationDao.insertAllMasters(DatabasePrepopulate.getDefaultViolationMasters())
                        }
                    }
                }
            }

            private suspend fun populateDatabase(database: AppDatabase) {
                val violationDao = database.violationDao()

                // Insert standard categories
                violationDao.insertAllCategories(DatabasePrepopulate.getDefaultCategories())

                // Insert standard master violation rules (37 official rules)
                violationDao.insertAllMasters(DatabasePrepopulate.getDefaultViolationMasters())

                // Clean initial state: 0 students, 0 violation logs (ready for real user data)
            }
        }
    }
}
