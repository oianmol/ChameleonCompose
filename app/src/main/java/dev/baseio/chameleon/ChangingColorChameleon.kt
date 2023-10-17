package dev.baseio.chameleon

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class ChangingColorChameleon :
    ViewModel(),
    Chameleon<ChangeColorEvent, ExampleChameleonState> by
    ChameleonImpl(initialState = ExampleChameleonState()) {

    private val random = Random

    init {
        event.debounce(300.milliseconds).onEach {
            changeColor()
        }.catch {
            Log.e(this@ChangingColorChameleon.javaClass.name, it.stackTraceToString())
        }.launchIn(viewModelScope)
    }

    private fun changeColor() {
        setState {
            copy(
                color = Color.hsl(
                    random.nextDouble(0.0, 1.0).toFloat(),
                    random.nextDouble(0.0, 1.0).toFloat(),
                    random.nextDouble(0.0, 1.0).toFloat(),
                ),
            )
        }
    }
}

data class ExampleChameleonState(val color: Color? = null)

sealed interface ChangeColorEvent {
    data object ClickPerformed : ChangeColorEvent
}
