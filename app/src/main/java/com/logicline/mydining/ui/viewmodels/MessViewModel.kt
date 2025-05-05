package com.logicline.mydining.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.models.Mess
import com.logicline.mydining.data.models.MessUser
import com.logicline.mydining.data.repository.MessRepository
import com.logicline.mydining.extensions.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessViewModel @Inject constructor (
    private val messRepository: MessRepository
) : ViewModel() {
    private val _createMessState = MutableStateFlow<DataState<MessUser?>>(DataState.Idle())
    val createMessState: StateFlow<DataState<MessUser?>> = _createMessState.asStateFlow()

    fun createMess(name:String) {
        viewModelScope.launch {
            _createMessState.value = DataState.Loading()
            _createMessState.value = safeApiCall {
                messRepository.createMess(name)
            }

        }
    }

}