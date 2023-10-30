# ChameleonCompose
A better Composable for Jetpack Compose 

ChameleonUi is a composable which allows you to define a viewModel for it, so that you have to worry less about the UiState and can freely send events to ViewModels

## A Chameleon is represented as this

```
@Composable
fun <Event, State> ChameleonUi(
    chameleon: Chameleon<Event, State>, // takes in a chameleon of type Event and State
    content: @Composable Chameleon<Event, State>.(State) -> Unit,
) {
    val uiState by chameleon.uiState.collectAsState()
    with(chameleon) {
        content(uiState)
    }
}

```

## A Chameleon is provided to viewModel which allows you to work with MVI design for Unidirectional flow.

```
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
```

## Usage

### Just create a chameleon UI and wrap your UI within it, 

```

class ChangingColorChameleon : ViewModel() : Chameleon by ChameleonImpl()

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


## Demo Video

### this shows how we use debounce with flows on click events

https://github.com/oianmol/ChameleonCompose/assets/4393101/e735575f-e0c5-441d-b982-e45a66d2bd5f


### License

```
MIT License

Copyright (c) 2023 Anmol Verma

Permission is hereby granted, free of charge, to any person obtaining a 
copy of this software and associated documentation files (the "Software"), 
to deal in the Software without restriction, including without limitation 
the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the 
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included 
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```
