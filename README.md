# ChameleonCompose
A better Composable for Jetpack Compose 

ChameleonUi is a composable which allows you to define a viewModel for it, so that you have to worry less about the UiState and can freely send events to ViewModels

## A Chameleon is represented as this

```
@Composable
fun <Event : ChameleonEvent, State : ChameleonState> ChameleonUi(
    chameleon: Chameleon<Event, State>, // takes in a chameleon of type Event and State
    content: @Composable Chameleon<Event, State>.(State) -> Unit,
) {
    val uiState by chameleon.uiState.collectAsState()
    with(chameleon) {
        content(uiState)
    }
}

interface ChameleonState

interface ChameleonEvent

```

## A Chameleon is a viewModel but allows you to work with MVI design for Unidirectional flow.

```
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
```

## Usage

### Just create a chameleon UI and wrap your UI within it, 
### you can create multiple chameleons and multiple ChameleonUI composables too

```
@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    chameleon: ChangingColorChameleon, // This is a typical viewmodel
) {
    ChameleonUi(
        chameleon = chameleon,
    ) { state -> // you get the state from a chameleon
        Scaffold(modifier) {
            Box(
                Modifier.fillMaxSize()
                    .padding(it)
                    .background(state.color ?: Color.Green),
            ) {
                TextButton(onClick = {
                    sendEvent(ChangeColorEvent.ClickPerformed) // you can magically send event to the chamelon instance associated to it via kotlin scopes.
                }, modifier = Modifier.align(Alignment.Center)) {
                    Text("Change Color")
                }
            }
        }
    }
}
```
