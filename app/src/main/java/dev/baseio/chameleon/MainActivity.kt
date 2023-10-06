package dev.baseio.chameleon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import dev.baseio.chameleon.ui.theme.ChameleonTheme

class MainActivity : ComponentActivity() {

    private val changingColorChameleon by viewModels<ChangingColorChameleon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChameleonTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Greeting(chameleon = changingColorChameleon)
                }
            }
        }
    }
}

@Composable
fun Greeting(
    chameleon: ChangingColorChameleon,
) {
    ChameleonUi(
        chameleon = chameleon,
    ) { state ->
        Box(
            Modifier.fillMaxSize()
                .clickable {
                    sendEvent(ChangeColorEvent.ClickPerformed)
                }
                .background(state.color ?: Color.Green),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    ChameleonTheme {
        Greeting(chameleon = ChangingColorChameleon())
    }
}
