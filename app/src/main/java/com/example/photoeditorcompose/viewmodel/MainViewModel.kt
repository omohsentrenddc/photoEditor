package com.example.photoeditorcompose.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoeditorcompose.model.ImageModel
import com.example.photoeditorcompose.model.ImageResponse
import com.example.photoeditorcompose.network.RetrofitInstance
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
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
                Log.d(TAG, "getImages: here dobe")

            } catch (e: Exception) {
                // Handle errors here
                Log.d(TAG, "getImages: ${e.message}")
            }
        }
    }
}