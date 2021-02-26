package com.zine.ketotime.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shin.tmsuser.database.entity.ConfigTb
import com.zine.ketotime.database.dao.TaskDao

@Database(entities = arrayOf(ConfigTb::class), version = 1)
abstract class KetoDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var instance: KetoDatabase? = null

        private val DATABASE_NAME = "ketoDatabase"

        operator fun invoke(context: Context) =
            instance
                ?: synchronized(this) {
                    instance
                        ?: buildDatabase(
                            context
                        ).also {
                            instance = it
                        }
                }

        private fun buildDatabase(context: Context): KetoDatabase {
            return Room.databaseBuilder(
                context,
                KetoDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}