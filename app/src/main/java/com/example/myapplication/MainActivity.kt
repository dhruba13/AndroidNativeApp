package com.example.myapplication

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}

class ComposeFileProvider : FileProvider(R.xml.file_paths) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()

            val file = File.createTempFile("selected_image_", ".jpg", directory)

            val authority = context.packageName + ".fileProvider"
            return getUriForFile(context, authority, file)
        }
    }
}


@Composable
fun MyApp() {
    val context = LocalContext.current
    var hasImage by remember {
        mutableStateOf(false)
    }
    var hasPermission by remember {
        mutableStateOf(true)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            hasImage = uri != null
            imageUri = uri
        }
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            {
                hasPermission = isGranted
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
        }
    )
    Box() {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (hasImage && imageUri != null) {
                Row(
                    modifier = Modifier
                        .height(300.dp)
                        .width(300.dp)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = imageUri,
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = null
                    )
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { imagePicker.launch("image/*") }) {
                    Text(text = "Select Image")
                }
                Button(onClick = {
                    if (hasPermission){
                        val uri = ComposeFileProvider.getImageUri(context)
                        imageUri = uri
                        hasImage = false
                        cameraLauncher.launch(uri)
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Text(text = "Take Picture")
                }

            }
        }
    }
}