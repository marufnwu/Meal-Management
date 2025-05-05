package com.logicline.mydining.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.repository.MonthRepository
import com.logicline.mydining.data.models.Month
import com.logicline.mydining.extensions.map
import com.logicline.mydining.extensions.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MonthViewModel(
    private val monthRepository: MonthRepository
) : ViewModel() {
    private val _state = MutableStateFlow<DataState<List<Month>>>(DataState.Loading())
    val state: StateFlow<DataState<List<Month>>> = _state.asStateFlow()

    init {
        fetchMonths()
    }

    fun fetchMonths() {
        viewModelScope.launch {
            _state.value = DataState.Loading()
            _state.value = safeApiCall {
                monthRepository.getMonths()
            }.map { months->
                if(months.isNullOrEmpty()) months!!.toList() else emptyList()
            }

        }
    }

}