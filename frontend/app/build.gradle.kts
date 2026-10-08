plugins {
   alias(libs.plugins.android.application)
   alias(libs.plugins.kotlin.compose)
   alias(libs.plugins.secrets.gradle.plugin)
}

android {
   namespace = "com.example.hogwatch.frontend"
   compileSdk {
      version = release(libs.versions.android.compileSdk.get().toInt())
   }

   defaultConfig {
      applicationId = "com.example.hogwatch"
      minSdk = libs.versions.android.minSdk.get().toInt()
      targetSdk = libs.versions.android.targetSdk.get().toInt()
      versionCode = 1
      versionName = "1.0"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }

   buildTypes {
      release {
         optimization {
            enable = false
         }
      }
   }

   buildFeatures {
      compose = true
   }
}

kotlin {
   jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
   implementation("com.google.maps.android:maps-compose-utils:8.4.0")
   implementation(libs.androidx.compose.foundation)
   implementation(libs.maps.compose)
   implementation(platform(libs.androidx.compose.bom))
   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.compose.material3)
   implementation(libs.androidx.compose.ui)
   implementation(libs.androidx.compose.ui.graphics)
   implementation(libs.androidx.compose.ui.tooling.preview)
   implementation(libs.androidx.core.ktx)
   implementation(libs.androidx.lifecycle.runtime.ktx)
   implementation(libs.navigation.compose)
   implementation(libs.androidx.datastore.preferences)
   testImplementation(libs.junit)
   androidTestImplementation(libs.androidx.compose.ui.test.junit4)
   androidTestImplementation(libs.androidx.espresso.core)
   androidTestImplementation(libs.androidx.junit)
   debugImplementation(libs.androidx.compose.ui.test.manifest)
   debugImplementation(libs.androidx.compose.ui.tooling)

   //Network related dependencies
   implementation(project(":shared"))
   implementation(ktorLibs.serialization.kotlinx.json)
   implementation(libs.ktor.client.core)
   implementation(libs.ktor.client.cio)
   implementation(libs.ktor.client.content.negotiation)

   implementation(libs.androidx.camera.core)
   implementation(libs.androidx.camera.camera2)
   implementation(libs.androidx.camera.lifecycle)
   implementation(libs.androidx.camera.view)

   implementation(libs.androidx.compose.material.icons)
   implementation(libs.play.services.maps)
   implementation(libs.play.services.location)

}

secrets {
   propertiesFileName = "secrets.properties"
}