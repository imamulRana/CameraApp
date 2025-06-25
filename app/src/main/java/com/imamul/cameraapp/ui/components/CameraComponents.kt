package com.imamul.cameraapp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.imamul.cameraapp.R

enum class CameraBottomButtons(@DrawableRes val buttonIcon: Int) {
    SwitchCamera(buttonIcon = R.drawable.flip_camera_android_24px),
    CaptureImage(buttonIcon = R.drawable.blur_circular_24px),
    ViewGallery(buttonIcon = R.drawable.landscape_2_24px),
}

enum class CameraTopButtons(@DrawableRes val buttonIcon: Int) {
    ToggleFlash(buttonIcon = R.drawable.flash_on_24px),
    ToggleHDR(buttonIcon = R.drawable.high_quality_24px),
    ToggleCrop(buttonIcon = R.drawable.ic_crop)
}

enum class CameraCropButtons(@DrawableRes val buttonIcon: Int) {
    Crop32(buttonIcon = R.drawable.ic_crop_3_2),
    Crop43(buttonIcon = R.drawable.ic_crop_4_3),
    Crop169(buttonIcon = R.drawable.ic_crop_16_9)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CameraBottomSection(
    modifier: Modifier = Modifier,
    onGalleryView: () -> Unit,
    onImageCapture: () -> Unit,
    onSwitchCamera: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CameraBottomButtons.entries.fastForEachIndexed { index, button ->
            val onClick = when (button) {
                CameraBottomButtons.ViewGallery -> onGalleryView
                CameraBottomButtons.CaptureImage -> onImageCapture
                CameraBottomButtons.SwitchCamera -> onSwitchCamera
            }
            FilledIconButton(
                onClick = onClick,
                modifier = Modifier
                    .size(
                        if (button != CameraBottomButtons.CaptureImage)

                            IconButtonDefaults.smallContainerSize()
                        else IconButtonDefaults.mediumContainerSize()
                    ),
                shapes = IconButtonDefaults.shapes(),
                content = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = button.buttonIcon),
                        contentDescription = null
                    )
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CameraCropSection(
    modifier: Modifier = Modifier,
    crop32: () -> Unit,
    crop43: () -> Unit,
    crop169: () -> Unit,
    onIconSelection: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CameraCropButtons.entries.fastForEachIndexed { index, button ->
            val onClick = when (button) {
                CameraCropButtons.Crop32 -> crop32
                CameraCropButtons.Crop43 -> crop43
                CameraCropButtons.Crop169 -> crop169
            }

            TextButton(
                onClick = { onClick(); onIconSelection(index) },
                modifier = Modifier,
                shapes = ButtonDefaults.shapes(),
                content = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = button.buttonIcon),
                            contentDescription = null
                        )
                        Text(
                            text = when (index) {
                                0 -> "3:2"
                                1 -> "4:3"
                                else -> {
                                    "16:9"
                                }
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
            )

        }
    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CameraTopSection(
    modifier: Modifier = Modifier,
    toggleFlash: () -> Unit,
    toggleHDR: () -> Unit,
    toggleCrop: () -> Unit,
    iconButtonIndex: Int,
    isHighQuality: Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CameraTopButtons.entries.fastForEachIndexed { index, button ->
            val onClick = when (button) {
                CameraTopButtons.ToggleFlash -> toggleFlash
                CameraTopButtons.ToggleHDR -> toggleHDR
                CameraTopButtons.ToggleCrop -> toggleCrop
            }
            IconButton(
                onClick = onClick,
                modifier = Modifier,
                content = {
                    if (button == CameraTopButtons.ToggleHDR)
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                id =
                                    if (isHighQuality)
                                        R.drawable.high_quality_24px_fill
                                    else R.drawable.high_quality_24px
                            ),
                            contentDescription = null
                        )
                    else
                        Icon(
                            imageVector =
                                ImageVector.vectorResource(
                                    id =

                                        if (button != CameraTopButtons.ToggleCrop)
                                            button.buttonIcon
                                        else
                                            CameraCropButtons.entries[iconButtonIndex].buttonIcon
                                ),
                            contentDescription = null
                        )
                },
            )
        }
    }

}