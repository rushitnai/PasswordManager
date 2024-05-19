package com.example.passwordmanager.ui.theme.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.passwordmanager.ui.theme.model.PasswordModel

@Database(entities = [PasswordModel::class], version = 1)
abstract class PasswordDB : RoomDatabase() {
    abstract fun passwordDao(): PasswordDao

    companion object {
        @Volatile
        private var INSTANCE: PasswordDB? = null

        fun getDatabase(context: Context): PasswordDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PasswordDB::class.java,
                    "myDB"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
