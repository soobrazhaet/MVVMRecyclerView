package com.example.mvvmrecyclerview.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvmrecyclerview.R
import com.example.mvvmrecyclerview.UserNotFoundException
import com.example.mvvmrecyclerview.model.User
import com.example.mvvmrecyclerview.model.UserDetails
import com.example.mvvmrecyclerview.model.UsersService
import com.example.mvvmrecyclerview.tasks.EmptyResult
import com.example.mvvmrecyclerview.tasks.PendingResult
import com.example.mvvmrecyclerview.tasks.Result
import com.example.mvvmrecyclerview.tasks.SuccessResult
import kotlinx.coroutines.runInterruptible

class UserDetailsViewModel(
    private val usersService: UsersService
): BaseViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    //livedata для показа тост-сообщения и выхода на предыдущий экран
    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private val _actionGoBack = MutableLiveData<Event<Unit>>()
    val actionGoBack: LiveData<Event<Unit>> = _actionGoBack

    private val currentState: State get() = _state.value!!

    init {
        _state.value = State(
            userDetailsResult = EmptyResult(),
            deletingInProgress = false,
        )
    }

    fun loadUser(userId: Long){
        if (currentState.userDetailsResult is SuccessResult) return
        _state.value = currentState.copy(userDetailsResult = PendingResult())

        usersService.getById(userId)
            .onSuccess {
                _state.value = currentState.copy(userDetailsResult = SuccessResult(it))
            }
            .onError {
                _actionShowToast.value = Event(R.string.cant_load_user_details)
                _actionGoBack.value = Event(Unit)
            }
            .autoCancel()
    }

    fun deleteUser(){
        val userDetailsResult = currentState.userDetailsResult

        if (userDetailsResult !is SuccessResult) return
        _state.value = currentState.copy(deletingInProgress = true)
        usersService.deleteUser(userDetailsResult.data.user)
            .onSuccess {
                _actionShowToast.value = Event(R.string.user_has_been_deleted)
                _actionGoBack.value = Event(Unit)
            }
            .onError {
                _state.value = currentState.copy(deletingInProgress = false)
                _actionShowToast.value = Event(R.string.cant_delete_user)
            }
            .autoCancel()
    }

    //класс отрисовки
    data class State(
        val userDetailsResult: Result<UserDetails>,
        private val deletingInProgress: Boolean
    ){

        val showContent: Boolean get() = userDetailsResult is SuccessResult
        val showProgress: Boolean get() = userDetailsResult is PendingResult || deletingInProgress
        val enabledDeleteButton: Boolean get() = !deletingInProgress
    }
}