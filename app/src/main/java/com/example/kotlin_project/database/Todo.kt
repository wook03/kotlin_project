package com.example.kotlin_project.database

import androidx.room.*
import java.io.Serializable

@Entity(tableName = "todoTable")
class Todo(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "timestamp") val timestamp: String,
    @ColumnInfo(name = "isChecked") var isChecked: Boolean
): Serializable {
}