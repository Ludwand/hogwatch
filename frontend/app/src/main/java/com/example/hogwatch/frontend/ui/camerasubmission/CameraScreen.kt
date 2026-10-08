package com.example.hogwatch.frontend.ui.camerasubmission
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hogwatch.frontend.data.repository.SightingRepository
import kotlinx.coroutines.launch

import java.io.File

@Composable
fun CameraScreen(
    viewModel: CameraViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // Initialize state by checking if permission is ALREADY granted
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val imageCapture = remember { ImageCapture.Builder().build() }

    // Request camera permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx)) //back to main thread

                    previewView
                }
            )

            //take photo button
            IconButton(
                onClick = {
                    // userId null check
                    val currentUserId = uiState.userId //TODO: Move this to viewmodel
                    if (currentUserId == null) {
                        println("Error: User ID is null")
                        return@IconButton
                    }

                    takePhoto(context, imageCapture) { base64Image ->
                        coroutineScope.launch {
                            val success = SightingRepository.submit(
                                id = currentUserId,
                                lat = 57.6282764,       // test data before location request is implemented
                                lon = 11.9030166,       // test data before location request is implemented
                                timeStamp = System.currentTimeMillis() / 1000L,
                                image = base64Image
                            )

                            if (success) {
                                println("Successfully submitted photo!")
                            } else {
                                println("Failed to submit photo.")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .size(80.dp)
                    .border(BorderStroke(4.dp, Color.White), CircleShape)
            ) {}
        } else {
            Text(
                text = "Camera permission is required.",
                modifier = Modifier.align(Alignment.Center)
                //this should also be refined to not just be text
            )
        }
    }
}


@Composable
fun CameraContent(){ //TODO: add stateless view-code

}

/*
* Captures a JPEG photo asynchronously using CameraX
* Converts the file into Base64
* Returns base64Image
* */


fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onPhotoCaptured: (String) -> Unit
) {
    val photoFile = File(context.cacheDir, "captured_photo_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val imageBytes = photoFile.readBytes()

                //cleanup
                photoFile.delete()

                val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

                //return
                onPhotoCaptured(base64Image)
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        }
    )
}