package com.imamul.cameraapp.ui

import android.Manifest
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.imamul.cameraapp.ui.components.CameraBottomSection
import com.imamul.cameraapp.ui.components.CameraCropSection
import com.imamul.cameraapp.ui.components.CameraTopSection
import com.imamul.cameraapp.utils.ImageSaver
import com.imamul.cameraapp.utils.checkPermission
import com.imamul.cameraapp.utils.rememberLauncherForPermission
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Composable
fun ScreenCamera(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    //ui state
    var isShutterEffect by remember { mutableStateOf(false) }
    var cropButtonIndex by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    var isHighQuality by remember { mutableStateOf(false) }
    var alignmentState by remember { mutableStateOf(false) }
    var previewWidth by remember { mutableIntStateOf(0) }
    var previewHeight by remember { mutableIntStateOf(0) }
    var cropSelection by remember { mutableStateOf(false) }
    val mediaPick = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { }


    //camera state
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var hasCameraPermission by remember {
        mutableStateOf(
            checkPermission(
                context,
                Manifest.permission.CAMERA
            )
        )
    }
    var hasStoragePermission by remember {
        mutableStateOf(
            checkPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val resolutionSelector = remember(cropButtonIndex) {
        ResolutionSelector.Builder().apply {
            when (cropButtonIndex) {
                0 -> setResolutionFilter { candidates, _ ->
                    candidates.filter { it.width * 2 == it.height * 3 }
                }

                1 -> setAspectRatioStrategy(
                    AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
                )

                else -> setAspectRatioStrategy(
                    AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY
                )
            }
        }.build()
    }

    val imageCapture = remember(isHighQuality, cropButtonIndex) {
        ImageCapture.Builder()
            .setCaptureMode(
                if (isHighQuality)
                    CAPTURE_MODE_MAXIMIZE_QUALITY
                else CAPTURE_MODE_MINIMIZE_LATENCY
            )
            .setResolutionSelector(resolutionSelector)
            .build()
    }
    val executor = remember { Executors.newSingleThreadExecutor() }

    //face detect
    val faceDetector = remember {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build()
        )
    }

    //analyzer mlkit
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    executor, MlKitAnalyzer(
                        listOf(faceDetector),
                        ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                        ContextCompat.getMainExecutor(context)
                    ) { result ->
                        val faces = result.getValue(faceDetector)
                        if (faces != null) {
                            alignmentState = faces.any { face ->
                                val bounds = face.boundingBox
                                val centerX = bounds.centerX()
                                val centerY = bounds.centerY()
                                centerX > previewWidth / 4 && centerX < previewWidth * 3 / 4 &&
                                        centerY > previewHeight / 4 && centerY < previewHeight * 3 / 4
                            }
                        }
                        Log.d("FaceDetection", "Faces detected: ${faces?.size ?: 0}")
                    })
            }
    }


    // custom perm launcher
    val permissionLauncher = rememberLauncherForPermission { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
        hasStoragePermission = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
    }

    // req perm
    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (!hasCameraPermission) permissionsToRequest.add(Manifest.permission.CAMERA)
        if (!hasStoragePermission && Build.VERSION.SDK_INT < 30) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    // if no perm
    if (!hasCameraPermission || (!hasStoragePermission && Build.VERSION.SDK_INT < 30)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Camera and storage permissions required")
        }
        return
    }


    // cam prev
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(
                width = if (alignmentState) 4.dp else 0.dp,
                color = if (alignmentState) Green else Transparent
            )
    ) {

        AndroidView(
            factory = { context ->
                val previewView = PreviewView(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(context))
                previewView
            },
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    if (isShutterEffect)
                        drawRect(
                            color = Black
                        )
                }
                .onGloballyPositioned { coordinates ->
                    previewWidth = coordinates.size.width
                    previewHeight = coordinates.size.height
                },
            update = { previewView ->
                // Rebind camera when cameraSelector changes
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        )
        if (cropSelection) {
            CameraCropSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.surfaceDim,
                                colorScheme.surfaceDim.copy(.8f)
                            )
                        )
                    )
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .align(Alignment.TopCenter),
                crop32 = { cropSelection = !cropSelection },
                crop43 = { cropSelection = !cropSelection },
                crop169 = { cropSelection = !cropSelection },
                onIconSelection = { cropButtonIndex = it }
            )
        } else
            CameraTopSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.surfaceDim,
                                colorScheme.surfaceDim.copy(.8f)
                            )
                        )
                    )
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .align(Alignment.TopCenter),
                toggleFlash = {},
                toggleHDR = {
                    isHighQuality = !isHighQuality
                    Toast.makeText(
                        context,
                        if (isHighQuality) "High Quality" else "Low Quality",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                },
                toggleCrop = {
                    cropSelection = !cropSelection
                },
                iconButtonIndex = cropButtonIndex,
                isHighQuality = isHighQuality
            )

        CameraBottomSection(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(color = colorScheme.surfaceDim)
                .windowInsetsPadding(WindowInsets.navigationBars),
            onGalleryView = { mediaPick.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
            onImageCapture = {
                ImageSaver(context, imageCapture).takePicture(
                    executor = executor
                )
                isShutterEffect = true
                coroutineScope.launch {
                    delay(250)
                    isShutterEffect = false
                }
            },
            onSwitchCamera = {
                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
            }
        )
    }
}


