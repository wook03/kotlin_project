package com.example.kotlin_project.database

import androidx.room.*

@Database(entities = arrayOf(Todo::class), version = 1)
abstract class TodoDatabase: RoomDatabase() {
    abstract fun todoDao(): TodoDao
}