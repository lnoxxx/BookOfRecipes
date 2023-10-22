package com.example.bookofrecipes

import android.app.Application
import androidx.room.Room
import com.example.bookofrecipes.dataBase.AppDatabase

class Application : Application() {

    val database by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "my_database").build()
    }

}