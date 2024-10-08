package com.example.photoeditorcompose

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.photoeditorcompose.ui.theme.PhotoEditorComposeTheme
import com.example.photoeditorcompose.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.android.pesdk.PhotoEditorSettingsList
import ly.img.android.pesdk.backend.model.EditorSDKResult
import ly.img.android.pesdk.backend.model.constant.OutputMode
import ly.img.android.pesdk.backend.model.state.LoadSettings
import ly.img.android.pesdk.backend.model.state.PhotoEditorSaveSettings
import ly.img.android.pesdk.ui.activity.PhotoEditorActivityResultContract
import ly.img.android.serializer._3.IMGLYFileWriter
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : ComponentActivity() {
    val viewModel : MainViewModel by viewModels()
    var imageSelected: Uri? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val editorLauncher = rememberLauncherForActivityResult(
                contract = PhotoEditorActivityResultContract(),
                onResult = { result ->
                    result.settingsList.use { settingsList ->
//                        val file = File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//                            .toString()+"/testing.json" )
//                        if (file.exists()) {
//                            file.delete()
//                        }
//                        file.createNewFile()
                        val jsonString = IMGLYFileWriter(settingsList).writeJsonAsString()
                        Log.d(TAG, "onCreateDone: $result")
                        imageSelected?.let { selectImage ->
                            val file = uriToFile(context,selectImage)
                            file?.let {
                                viewModel.saveImage(jsonString,file)
                            }

                        }

                        // Serialize the settingsList as JSON into the file
//                        IMGLYFileWriter(settingsList).writeJson(file)
                        // val result = activity.contentResolver.openInputStream(file.toUri())
                        // print("result:${result}")
                        lifecycleScope.launch {
                            showMessage("Serialisation saved successfully")
                        }

                    }
                    when (result.resultStatus) {
                        EditorSDKResult.Status.CANCELED -> showMessage("Editor cancelled")
                        EditorSDKResult.Status.EXPORT_DONE -> showMessage("Result saved at ${result.resultUri}")
//                        EDITOR_REQUEST_CODE -> showMessage("Result saved at ${result.resultUri}")
                        else -> {

                        }
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                GalleryPicker{ uri ->
                    imageSelected = uri
                    val settingsList = PhotoEditorSettingsList(true)
                        .configure<LoadSettings> {
                            // Set the source as the Uri of the image to be loaded
                            it.source = uri
                        }
                        .configure<PhotoEditorSaveSettings> {
                            it.outputMode = OutputMode.EXPORT_ALWAYS
                        }
                        .configure<PhotoEditorSaveSettings> {
                            val file = File(cacheDir, "imgly_photo.jpg")
                            viewModel.fileUpdateFromEditor = file
                            it.setOutputToUri(Uri.fromFile(file))
                        }


                    editorLauncher.launch(settingsList)
                    // Release the SettingsList once done
                    settingsList.release()
                }

            }

            ImageList(viewModel = MainViewModel())

//            PhotoEditorComposeTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
        }
    }


    private fun showMessage(msg: String){
        Toast.makeText(this,"msg: $msg",Toast.LENGTH_LONG).show()
    }

    fun uriToFile(context: Context, uri: Uri): File? {
        val contentResolver: ContentResolver = context.contentResolver
        val file = File(context.cacheDir, "selectedImage_${System.currentTimeMillis()}.jpg")

        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (input.read(buffer).also { length = it } > 0) {
                        output.write(buffer, 0, length)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return file
    }



    @Composable
    fun GalleryPicker( openEditor: (uri: Uri) -> Unit) {
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                if (uri != null) {
                    selectedImageUri = uri
                    imageSelected = selectedImageUri
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                galleryLauncher.launch("image/*")
            }) {
                Text("Open Gallery")
            }

            Spacer(modifier = Modifier.height(20.dp))

            selectedImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier.size(300.dp),
                    contentScale = ContentScale.Crop
                )
                Button(onClick = {
                    openEditor(uri)
                }) {
                    Text("Open Editor")
                }
            } ?: Text("No Image Selected", color = Color.Gray)
        }
    }

}



private  val TAG = "MainActivity"

@Composable
fun ImageList(viewModel: MainViewModel) {
    Log.d(TAG, "ImageList: Start here")
    val images = viewModel.images.value
    Log.d(TAG, "ImageList: working here")
    LazyColumn(modifier = Modifier.padding(20.dp)) {

        items(images) { image ->
            Text(text = image.text)
            Spacer(Modifier.height(20.dp))

        }
    }
    DisposableEffect(Unit) {
        viewModel.getImages()
        onDispose {}
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PhotoEditorComposeTheme {
        Greeting("Android")
    }
}