package dev.baseio.chameleon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun <Event : ChameleonEvent, State : ChameleonState> ChameleonUi(
    chameleon: Chameleon<Event, State>,
    content: @Composable Chameleon<Event, State>.(State) -> Unit,
) {
    val uiState by chameleon.uiState.collectAsState()
    with(chameleon) {
        content(uiState)
    }
}

interface ChameleonState

interface ChameleonEvent

open class Chameleon<Event : ChameleonEvent, State : ChameleonState>(
    createInitialState: () -> State,
) : ViewModel() {

    private val initialState: State by lazy { createInitialState() }

    private val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    fun sendEvent(event: Event) {
        val newEvent = event
        viewModelScope.launch { _event.emit(newEvent) }
    }

    fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _uiState.value = newState
    }
}
