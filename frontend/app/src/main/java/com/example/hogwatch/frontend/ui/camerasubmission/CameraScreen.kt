package com.example.hogwatch.frontend.ui.camerasubmission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File

@Composable
fun CameraScreen(
   viewModel: CameraViewModel = viewModel()
) {
   val uiState by viewModel.uiState.collectAsStateWithLifecycle()
   val context = LocalContext.current

   var hasCameraPermission by remember {
      mutableStateOf(
         ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
         ) == PackageManager.PERMISSION_GRANTED
      )
   }

   val imageCapture = remember { ImageCapture.Builder().build() }
   val permissionLauncher = rememberLauncherForActivityResult(
      contract = ActivityResultContracts.RequestPermission()
   ) { isGranted -> hasCameraPermission = isGranted }

   LaunchedEffect(Unit) {
      if (!hasCameraPermission) {
         permissionLauncher.launch(Manifest.permission.CAMERA)
      }
   }

   CameraContent(
      hasCameraPermission = hasCameraPermission,
      uiState = uiState,
      imageCapture = imageCapture,
      onCaptureClick = {
         takePhoto(context, imageCapture) { base64Image ->
            viewModel.onPhotoCaptured(base64Image)
         }
      },
      onRetakeClick = { viewModel.onRetakePhoto() },
      onSubmitClick = { viewModel.onSubmit() }
   )
}

@Composable
fun CameraContent(
   hasCameraPermission: Boolean,
   uiState: CameraUiState,
   imageCapture: ImageCapture,
   onCaptureClick: () -> Unit,
   onRetakeClick: () -> Unit,
   onSubmitClick: () -> Unit
) {
   val lifecycleOwner = LocalLifecycleOwner.current

   Box(modifier = Modifier.fillMaxSize()) {
      if (!hasCameraPermission) {
         Text(
            text = "Camera permission is required.",
            modifier = Modifier.align(Alignment.Center)
         )
         return@Box
      }

      //Preview
      if (uiState.capturedImageBase64 != null) {
         // Converts Base64 String back to Bitmap
         val imageBytes = Base64.decode(uiState.capturedImageBase64, Base64.DEFAULT)
         val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

         bitmap?.let {
            Image(
               bitmap = it.asImageBitmap(),
               contentDescription = "Captured Photo Preview",
               modifier = Modifier.fillMaxSize(),
               contentScale = ContentScale.Crop
            )
         }

         Row(
            modifier = Modifier
               .fillMaxWidth()
               .align(Alignment.BottomCenter)
               .background(Color.Black.copy(alpha = 0.4f))
               .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
         ) {
            Button(
               onClick = onRetakeClick,
               modifier = Modifier.weight(1f)
            ) {
               Text("Retake")
            }

            Button(
               onClick = onSubmitClick,
               enabled = !uiState.isSubmitting,
               modifier = Modifier
                  .weight(1f)
                  .padding(start = 16.dp)
            ) {
               Text(if (uiState.isSubmitting) "Sending..." else "Submit")
            }
         }
      } else {
         AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
               PreviewView(ctx).apply {
                  implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                  val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                  cameraProviderFuture.addListener({
                     val cameraProvider = cameraProviderFuture.get()
                     val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(surfaceProvider)
                     }

                     try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                           lifecycleOwner,
                           CameraSelector.DEFAULT_BACK_CAMERA,
                           preview,
                           imageCapture
                        )
                     } catch (e: Exception) {
                        e.printStackTrace()
                     }
                  }, ContextCompat.getMainExecutor(ctx))
               }
            }
         )

         IconButton(
            onClick = onCaptureClick,
            modifier = Modifier
               .align(Alignment.BottomCenter)
               .padding(bottom = 48.dp)
               .size(80.dp)
               .border(BorderStroke(4.dp, Color.White), CircleShape)
         ) {}
      }
   }
}

private fun takePhoto(
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
            photoFile.delete()
            val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
            onPhotoCaptured(base64Image)
         }

         override fun onError(exception: ImageCaptureException) {
            exception.printStackTrace()
         }
      }
   )
}