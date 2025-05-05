package com.logicline.mydining.domains

import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.models.Month
import com.logicline.mydining.data.repository.MonthRepository
import com.logicline.mydining.extensions.map
import com.logicline.mydining.extensions.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object MonthStore {
    private val _state = MutableStateFlow<DataState<List<Month>>>(DataState.Loading())
    val state: StateFlow<DataState<List<Month>>> = _state.asStateFlow()

    private var hasFetched = false

    suspend fun fetchMonthsIfNeeded(monthRepository: MonthRepository) {
        if (!hasFetched) {
            hasFetched = true
            _state.value = DataState.Loading()
            _state.value = safeApiCall {
                monthRepository.getMonths()
            }.map { months -> months ?: emptyList() }
        }
    }

    fun forceRefresh(monthRepository: MonthRepository) {
        hasFetched = false
        CoroutineScope(Dispatchers.IO).launch {
            fetchMonthsIfNeeded(monthRepository)
        }
    }
}
