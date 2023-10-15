package com.example.bookofrecipes

import android.app.Application
import androidx.room.Room
import com.example.bookofrecipes.dataBase.AppDatabase

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app-database"
        ).build()
    }
}