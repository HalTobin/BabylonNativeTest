package com.moineaufactory.babylonnativetest.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isGone
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.library.babylonnative.BabylonView
import com.moineaufactory.babylonnativetest.data.Scene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IndexActivity : ComponentActivity(), BabylonView.ViewDelegate {
    private lateinit var mView: BabylonView

    private var isSceneInitialized = false
    private var _folder: String? = null
    private var modelPath: String = "app:///Scenes/elephant.glb"

    private var _viewInitialized: Boolean = false

    private val _currentScene = MutableStateFlow(Scene.BOX)
    private val currentScene = _currentScene.asStateFlow()

    // Activity life
    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        Log.i("PlaygroundActivity", "init PlaygroundActivity.kt")

        mView = BabylonView(application, this)
        setContent {

            val myScene by currentScene.collectAsStateWithLifecycle()

            LaunchedEffect(true) {
                _viewInitialized = true
                loadScene(myScene)
            }

            Scaffold { insets ->
                Box {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context -> mView }
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = insets.calculateBottomPadding() + 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Crossfade(myScene) { scopedScene ->
                            Text(
                                text = scopedScene.name.uppercase(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Scene.entries.forEach { scene ->
                                IconButton(
                                    onClick = { loadScene(scene) }
                                ) {
                                    Crossfade(
                                        targetState = scene == myScene
                                    ) { on ->
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (on) Color.White
                                                    else Color.LightGray
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        mView.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mView.onResume()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        //mView.onRequestPermissionsResult(requestCode, permissions, results)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && mView.isGone == true) {
            mView.visibility = View.VISIBLE
        }
    }

    override fun onViewReady() {
        Log.i("PlaygroundActivity", "View Ready")
        CoroutineScope(Dispatchers.IO).launch {
            while (!_viewInitialized) {
                delay(50)
            } // Wait for callbacks to be set
        }
    }

    fun loadScene(scene: Scene) {
        _currentScene.update { scene }
        val path = scene.path
        Log.i("PlaygroundActivity", "load: $path")

        // Define JS variable
        val injectedJs = "globalThis.modelPath = '$path';"
        val sourceUrl = "runtime-injected.js"

        mView.eval(injectedJs, sourceUrl)
        if (!isSceneInitialized) {
            mView.loadScript("app:///Scripts/experience_wip.js")
            mView.eval("initObject();", "load-glb.js")
            isSceneInitialized = true
        }
        mView.eval("loadModel();", "load-glb.js")
    }

    override fun onStop() {
        super.onStop()
        Log.i("PlaygroundActivity", "finalize")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("PlaygroundActivity", "finalize")
        mView.loadScript("app:///Scripts/clear_engine.js")
        // Clean up the scene
        //mView.eval("scene.dispose();", "cleanup-scene.js")
        //mView.eval("engine.dispose();", "exit-xr.js")
    }
}
