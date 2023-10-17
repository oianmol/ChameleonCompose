package dev.baseio.chameleon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun <Event, State> ChameleonUi(
    chameleon: Chameleon<Event, State>,
    content: @Composable Chameleon<Event, State>.(State) -> Unit,
) {
    val uiState by chameleon.uiState.collectAsState()
    with(chameleon) {
        content(uiState)
    }
}

interface Chameleon<Event, State> {
    val event: SharedFlow<Event>
    val uiState: StateFlow<State>
    fun setEvent(event: Event, viewModelScope: CoroutineScope)
    fun setState(reduce: State.() -> State)
}

class ChameleonImpl<Event, State>(initialState: State) : Chameleon<Event, State> {

    // Get Current State
    private val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    override val uiState = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    override val event = _event.asSharedFlow()

    /**
     * Set new Event
     */
    override fun setEvent(
        event: Event,
        viewModelScope: CoroutineScope,
    ) {
        val newEvent = event
        viewModelScope.launch { _event.emit(newEvent) }
    }

    /**
     * Set new Ui State
     */
    override fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _uiState.value = newState
    }
}
