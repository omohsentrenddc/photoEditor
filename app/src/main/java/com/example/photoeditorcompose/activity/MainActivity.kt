package com.example.photoeditorcompose.activity

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.example.photoeditorcompose.model.ImageModel
import com.example.photoeditorcompose.ui.theme.PhotoEditorComposeTheme
import com.example.photoeditorcompose.viewmodel.MainViewModel
import kotlinx.coroutines.launch
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
    val viewModel: MainViewModel by viewModels()
    var imageSelected: Uri? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    startActivity(Intent(this@MainActivity, PickImageActivity::class.java))
                }) {
                    Text(text = "Upload New Image")
                }
                HorizontalDivider()
                ImageList(viewModel = viewModel) { imageModel ->
                    val intent = Intent(this@MainActivity, PickImageActivity::class.java)
                    intent.putExtra("id", imageModel.id)
                    intent.putExtra("oldImage", imageModel.oldImage)
                    intent.putExtra("newImage", imageModel.newImage)
                    intent.putExtra("text", imageModel.text)
                    startActivity(intent)
                }
            }


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

    override fun onResume() {
        super.onResume()
        viewModel.getImages()
    }
}


private val TAG = "MainActivity"

@Composable
fun ImageList(viewModel: MainViewModel, modifiy: (ImageModel) -> Unit) {
    Log.d(TAG, "ImageList: Start here")
    val images = viewModel.images.value
    Log.d(TAG, "ImageList: working here")
    var showList by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = viewModel.images.value) {
        if (viewModel.images.value.isNotEmpty()) showList = true
    }
//    if(showList) {
    LazyColumn(modifier = Modifier.padding(20.dp)) {

        items(images) { image ->

            Row {
                Image(
                    painter = rememberAsyncImagePainter(model = image.newImage),
                    contentDescription = "newImage",
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = {
                    modifiy(image)
                }) {
                    Text(text = "Update")
                }
            }

//
//            }
        }
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