package com.example.kotlin_project.config

import android.app.Application
import com.example.kotlin_project.repository.TodoRepository

class ApplicationClass: Application() {

    override fun onCreate() {
        super.onCreate()

        TodoRepository.initialize(this)
    }
}