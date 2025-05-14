package com.logicline.mydining.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.local.relations.toDomainModel
import com.logicline.mydining.data.models.MessUser
import com.logicline.mydining.data.models.UserData
import com.logicline.mydining.data.models.toRelation
import com.logicline.mydining.data.repository.Repository
import com.logicline.mydining.extensions.safeApiCall
import com.logicline.mydining.utils.AppPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "UserViewModel"

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _messUserState = MutableStateFlow<DataState<MessUser?>>(DataState.Idle())
    val messUserState: StateFlow<DataState<MessUser?>> = _messUserState.asStateFlow()

    private val _loginState = MutableStateFlow<DataState<UserData?>>(DataState.Idle())
    val loginState: StateFlow<DataState<UserData?>> = _loginState.asStateFlow()


    val observedUserData: StateFlow<DataState<UserData?>> = flow {
        emit(DataState.Loading())
        try {
            repository.observedUserData().collect { userWithRelations ->
                emit(DataState.Success(userWithRelations?.toDomainModel()))
            }
        } catch (e: Exception) {
            emit(DataState.Exception(exception = e, message = e.localizedMessage))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(0),
        initialValue = DataState.Idle()
    )


    init {
        loadCurrentMessUser()
    }

    suspend fun observedUserData() = repository.observedUserData()

    fun loadCurrentMessUser() {
        viewModelScope.launch {
            repository.observeMessUser().collect { messUser ->
                val result = DataState.Success(data = messUser?.toDomainModel())
                _messUserState.value = result
                Log.d(TAG, "Current MessUser: ${Gson().toJson(result)}")
            }
        }
    }

    fun syncCurrentMessUser() {
        viewModelScope.launch {
            val result = safeApiCall { repository.getMessUser() }

            if (result is DataState.Success) {
                saveMessUserLocally(result.data)
            } else if (result is DataState.Error) {
                repository.saveMessUserLocally(null)
            }
        }
    }

    fun createMess(name: String) {
        viewModelScope.launch {
            _messUserState.value = DataState.Loading()

            val result = safeApiCall { repository.createMess(name) }

            when (result) {
                is DataState.Success -> saveMessUserLocally(result.data)
                is DataState.Error -> _messUserState.value = result
                else -> Unit
            }
        }
    }

    fun login(userName: String, password: String) {
        _loginState.value = DataState.Loading()
        viewModelScope.launch {
            _loginState.value = safeApiCall {
                repository.login(userName, password)
            }
        }
    }

    fun checkLogin() {
        _loginState.value = DataState.Loading()
        viewModelScope.launch {
            _loginState.value = safeApiCall {
                repository.checkLogin()
            }
        }
    }

    suspend fun saveMessUserLocally(messUser: MessUser?) {
        repository.saveMessUserLocally(messUser?.toRelation())
    }

    suspend fun saveUserDataLocally(userData: UserData?) {
        Log.d("insertMessUserWithRelations: view model saveMessUserLocally", Gson().toJson(userData))

        userData?.let {
            AppPrefs.accessToken = userData.token
            AppPrefs.user = userData.user
            AppPrefs.messUser = userData.messUser
            AppPrefs.userId = userData.user?.id
        }
        repository.saveUserDataLocally(userData?.toRelation())
    }
}
