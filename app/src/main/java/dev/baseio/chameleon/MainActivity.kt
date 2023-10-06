package dev.baseio.chameleon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    modifier: Modifier = Modifier,
    chameleon: ChangingColorChameleon,
) {
    ChameleonUi(
        chameleon = chameleon,
    ) { state ->
        Scaffold(modifier) {
            Box(
                Modifier.fillMaxSize()
                    .padding(it)
                    .background(state.color ?: Color.Green),
            ) {
                TextButton(onClick = {
                    sendEvent(ChangeColorEvent.ClickPerformed)
                }, modifier = Modifier.align(Alignment.Center)) {
                    Text("Change Color")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    ChameleonTheme {
        Greeting(chameleon = ChangingColorChameleon())
    }
}
