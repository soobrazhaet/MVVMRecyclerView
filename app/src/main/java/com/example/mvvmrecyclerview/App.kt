package com.example.mvvmrecyclerview

import android.app.Application
import com.example.mvvmrecyclerview.model.UsersService

//реализация синглтона
class App: Application() {

    val usersService = UsersService()
}