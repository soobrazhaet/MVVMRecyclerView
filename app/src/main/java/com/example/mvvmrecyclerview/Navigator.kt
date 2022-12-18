package com.example.mvvmrecyclerview

import com.example.mvvmrecyclerview.model.User

interface Navigator {

    //показ экрана со списком деталей
    fun showDetails(user: User)

    //выход на один экран назад
    fun goBack()

    //показ сообщения
    fun toast(messageRes: Int)
}