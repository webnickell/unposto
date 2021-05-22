package com.example.unposto.util

import androidx.lifecycle.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class IntentViewModel<TIntent, TState>(initialState: TState) : ViewModel() {
    private val _intentFlow = ConflatedBroadcastChannel<TIntent>()
    val state: LiveData<TState> = liveData {
        emit(initialState)
        emitSource(
            _intentFlow.asFlow().flatMapConcat { mapIntentOnState(it, currentState) }.asLiveData()
        )
    }
    private val currentState get() = state.value!!

    abstract fun mapIntentOnState(intent: TIntent, currentState: TState): Flow<TState>

    fun addIntent(i: TIntent) {
        viewModelScope.launch {
            _intentFlow.send(i)
        }
    }
}