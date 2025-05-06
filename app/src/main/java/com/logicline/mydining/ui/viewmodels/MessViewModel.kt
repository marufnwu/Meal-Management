package com.logicline.mydining.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.local.relations.toDomainModel
import com.logicline.mydining.data.models.Mess
import com.logicline.mydining.data.models.MessUser
import com.logicline.mydining.data.models.toRoomModel
import com.logicline.mydining.data.repository.MessRepository
import com.logicline.mydining.extensions.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MessViewModel"
@HiltViewModel
class MessViewModel @Inject constructor (
    private val messRepository: MessRepository
) : ViewModel() {
    private val _messUserState = MutableStateFlow<DataState<MessUser?>>(DataState.Idle())
    val messUserUser: StateFlow<DataState<MessUser?>> = _messUserState.asStateFlow()

    init {
        _messUserState.value = DataState.Loading()
        viewModelScope.launch {
            messRepository.getMessUser().collect { messUser ->
                _messUserState.value = DataState.Success(data = messUser?.toDomainModel())
                Log.d(TAG, ": "+_messUserState.value)

                repeat(5) { i ->
                    delay(5000L) // 5 seconds delay between updates

                    val simulatedUser = _messUserState.value.data?.copy( mess = _messUserState.value.data!!.mess?.copy(name = "Name $i")) // simulate different data
                    messRepository.saveMessUserLocally(simulatedUser!!.toRoomModel())

                    Log.d("Test", "Updated MessUser with id = $i")
                }
            }
        }
    }

    fun createMess(name:String) {
        viewModelScope.launch {
            _messUserState.value = DataState.Loading()

            val result = safeApiCall {
                messRepository.createMess(name)
            }

            when (result) {
                is DataState.Success -> {
                    result.data?.let { messUser ->
                        // Save to local Room DB
                        messUser.toRoomModel().let {
                            messRepository.saveMessUserLocally(it)
                        }


                    }
                }
                is DataState.Error -> {
                    _messUserState.value = result
                    return@launch
                }
                else -> Unit
            }

            _messUserState.value = result
        }
    }

}