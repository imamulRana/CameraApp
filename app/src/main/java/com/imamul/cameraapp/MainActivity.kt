package com.imamul.cameraapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.imamul.cameraapp.ui.CameraPreviewScreen
import com.imamul.cameraapp.ui.CameraViewModel
import com.imamul.cameraapp.ui.ScreenCamera
import com.imamul.cameraapp.ui.ScreenOverlay
import com.imamul.cameraapp.ui.theme.CameraAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isOverlayActive by remember { mutableStateOf(true) }
            val viewModel: CameraViewModel by viewModels()
            CameraAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    /*CameraPreviewScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                        ,
                        viewModel = viewModel
                    )*/
                    if (isOverlayActive)
                        ScreenOverlay(
                            modifier = Modifier.padding(innerPadding),
                            onOverlayClose = { isOverlayActive = false })
                    else

                        ScreenCamera(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}