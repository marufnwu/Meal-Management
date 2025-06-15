package com.logicline.mydining.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.local.relations.toDomainModel
import com.logicline.mydining.data.models.MessUser
import com.logicline.mydining.data.models.UserData
import com.logicline.mydining.data.models.UserSummary
import com.logicline.mydining.data.models.toRelation
import com.logicline.mydining.data.repository.Repository
import com.logicline.mydining.extensions.safeApiCall
import com.logicline.mydining.utils.AppPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _userMinimalSummaryState = MutableStateFlow<DataState<UserSummary?>>(DataState.Idle())
    val userMinimalSummaryState: StateFlow<DataState<UserSummary?>> = _userMinimalSummaryState.asStateFlow()


    val observedUserData = repository.observedUserData()
        .stateIn(
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
        userData?.let {
            AppPrefs.accessToken = userData.token
            AppPrefs.user = userData.user
            AppPrefs.messUser = userData.messUser
            AppPrefs.userId = userData.user?.id
        }
        repository.saveUserDataLocally(userData?.toRelation())
    }

    fun loadUserMinimalMonthSummary() {
        viewModelScope.launch {
            _userMinimalSummaryState.value = DataState.Loading()
            _userMinimalSummaryState.value = repository.userMinimalMonthSummary()
        }
    }



}
