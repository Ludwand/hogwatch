package com.example.hogwatch.frontend.ui.camerasubmission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
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
import java.io.ByteArrayOutputStream
import java.io.File
import android.widget.Toast

@Composable
fun CameraScreen(
    viewModel: CameraViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateMap: () -> Unit
) {
   val uiState by viewModel.uiState.collectAsStateWithLifecycle()
   val context = LocalContext.current

   LaunchedEffect(uiState.isSubmittedSuccess) {
      if (uiState.isSubmittedSuccess) {
         onNavigateBack()
         onNavigateMap()
      }
   }

   LaunchedEffect(uiState.errorMessage) {
      uiState.errorMessage?.let { message ->
         Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
         viewModel.clearErrorMessage()
      }
   }

   LaunchedEffect(uiState.isSubmittedSuccess) {
      if (uiState.isSubmittedSuccess) {
         Toast.makeText(context, "Sighting submitted successfully!", Toast.LENGTH_SHORT).show()
         viewModel.clearSuccessMessage()
      }
   }

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
            val filePath = photoFile.absolutePath
            val originalBitmap = BitmapFactory.decodeFile(filePath)

            if (originalBitmap != null) {
               // read rotation
               val exif = ExifInterface(filePath)
               val orientation = exif.getAttributeInt(
                  ExifInterface.TAG_ORIENTATION,
                  ExifInterface.ORIENTATION_NORMAL
               )

               val rotationDegrees = when (orientation) {
                  ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                  ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                  ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                  else -> 0f
               }

               // rotate if needed
               val rotatedBitmap = if (rotationDegrees != 0f) {
                  val matrix = Matrix().apply { postRotate(rotationDegrees) }
                  val rotated = Bitmap.createBitmap(
                     originalBitmap, 0, 0,
                     originalBitmap.width, originalBitmap.height,
                     matrix, true
                  )
                  originalBitmap.recycle()
                  rotated
               } else {
                  originalBitmap
               }

               val outputStream = ByteArrayOutputStream()
               rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
               val compressedBytes = outputStream.toByteArray()
               rotatedBitmap.recycle() // Free memory

               // Base 64
               val base64Image = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)

               // cleanup
               photoFile.delete()

               onPhotoCaptured(base64Image)
            }
         }

         override fun onError(exception: ImageCaptureException) {
            exception.printStackTrace()
         }
      }
   )
}