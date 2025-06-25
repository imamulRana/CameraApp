package com.imamul.cameraapp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.util.fastForEachIndexed
import com.imamul.cameraapp.R

enum class OverlayComponents(
    val activeColor: Color,
    @DrawableRes val icon: Int,
    val description: String,
    var currentStep: Int,
) {
    Flash(
        activeColor = White,
        icon = R.drawable.flash_on_24px,
        description = "Toggle this to turn on the Flash",
        currentStep = 0
    ),
    HighQuality(
        activeColor = White,
        icon = R.drawable.high_quality_24px,
        description = "Toggle this to turn on High Quality Photo to capture HQ images (More storage may require)",
        currentStep = 1
    ),
    AspectRatio(
        activeColor = White,
        icon = R.drawable.ic_crop_3_2,
        description = "Toggle this to change the aspect ratio",
        currentStep = 2
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OverlayTopSection(
    modifier: Modifier = Modifier,
    currentStep: Int,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OverlayComponents.entries.fastForEachIndexed { index, button ->
            /*val onClick = when (button) {
                OverlayComponents.Flash -> changeStep(0)
                OverlayComponents.HighQuality -> changeStep(1)
                OverlayComponents.AspectRatio -> changeStep(2)
            }*/

            IconButton(
                onClick = { },
                modifier = Modifier,
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = if (index == currentStep) button.activeColor else White.copy(.3f)
                ),
                content = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = button.icon),
                        contentDescription = null
                    )

                },
            )
        }

    }
}
