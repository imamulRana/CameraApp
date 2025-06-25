package com.imamul.cameraapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.dp
import com.imamul.cameraapp.ui.components.OverlayTopSection

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScreenOverlay(modifier: Modifier = Modifier, onOverlayClose: () -> Unit) {
    var currentStep by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Transparent)
    ) {
        OverlayTopSection(
            modifier = Modifier,
            currentStep = currentStep
        )

        Text(
            modifier = Modifier

                .padding(16.dp).align(Alignment.Center),
            text = when (currentStep) {
                0 -> " Click here to to turn on the flash"
                1 -> "Toggle this to turn on or off the (High quality) photo mode. This will result in higher image size and require more storage."
                2 -> "Crop Image: Tap Here to crop images \n 1. ID Photo Headshot-style ID image 3:2 \n 2. Member Photo Full-face member photo 4:3 \n 3. ID + Member Combo Combined layout (dual frame) 16:9"
                else -> {
                    ""
                }
            },

        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Step ${currentStep.plus(1)}/3")
            OutlinedButton(
                onClick = { currentStep = (currentStep + 1) },
                modifier = Modifier.fillMaxWidth(),
                shapes = ButtonDefaults.shapes(),
                enabled = currentStep < 2
            ) {
                Text("Continue")
            }
            OutlinedButton(
                onClick = onOverlayClose,
                modifier = Modifier.fillMaxWidth(),
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        }
    }
}
