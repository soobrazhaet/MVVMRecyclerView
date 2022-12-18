package com.example.mvvmrecyclerview.tasks

typealias Callback<T> = (T) -> Unit

interface Task<T> {
    //результат
    fun onSuccess(callback: Callback<T>): Task<T>
    //ошибка
    fun onError(callback: Callback<Throwable>): Task<T>
    //отмена
    fun cancel()
    //дождаться
    fun await(): T
}