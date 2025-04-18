package com.moineaufactory.babylonnativetest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moineaufactory.babylonnativetest.ui.theme.BabylonNativeTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BabylonNativeTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            Greeting(
                                name = "BabylonNative",
                                modifier = Modifier.padding(innerPadding)
                            )
                            Models.entries.forEach { model ->
                                Button(
                                    modifier = Modifier.fillMaxWidth(0.7f),
                                    shape = RoundedCornerShape(8.dp),
                                    onClick = { loadModel(model.path) }
                                ) {
                                    Text(model.name.uppercase())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadModel(modelPath: String) {
        val intent = Intent(this, PlaygroundActivity::class.java)
        intent.putExtra("path", modelPath)
        startActivity(intent)
    }
}

enum class Models(val path: String) {
    BOX("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Box/glTF/Box.gltf"),
    AVOCADO("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf"),
    FLIGHT_HELMET("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/FlightHelmet/glTF/FlightHelmet.gltf")
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        style = MaterialTheme.typography.headlineSmall,
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BabylonNativeTestTheme {
        Greeting("Android")
    }
}