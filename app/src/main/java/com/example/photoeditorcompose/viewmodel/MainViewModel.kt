package com.example.photoeditorcompose.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoeditorcompose.model.ImageModel
import com.example.photoeditorcompose.model.ImageModelRequest
import com.example.photoeditorcompose.model.ImageResponse
import com.example.photoeditorcompose.network.RetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MainViewModel : ViewModel() {
    var jsonResult: String = ""
    var file : File? = null
    private val apiService = RetrofitInstance.api
    val images: MutableState<List<ImageModel>> = mutableStateOf(ImageResponse().images)
    private  val TAG = "MainViewModel"
    fun getImages() {
        Log.d(TAG, "getImages: ")
        viewModelScope.launch {
            try {
                val response = apiService.getImages()
                Log.d(TAG, "getImages: here ${response.status}")
                if (response.images.isNotEmpty())
                    images.value = response.images

            } catch (e: Exception) {
                // Handle errors here
                Log.d(TAG, "getImages: ${e.message}")
            }
        }
    }

    fun saveImage(){
        if(file != null && jsonResult.isNotEmpty()){
            viewModelScope.launch {
                try {
                    val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                    val imagePart = MultipartBody.Part.createFormData("image", file!!.name, requestFile)
                    val response = apiService.submitImage(ImageModelRequest(text = jsonResult),imagePart)
                    Log.d(TAG, "getImages: here ${response.status}")
                    if (response.images.isNotEmpty())
                        images.value = response.images

                } catch (e: Exception) {
                    // Handle errors here
                    Log.d(TAG, "getImages: ${e.message}")
                }
            }
        }
    }

}