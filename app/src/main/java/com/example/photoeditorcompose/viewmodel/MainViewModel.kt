package com.example.photoeditorcompose.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoeditorcompose.model.ImageModel
import com.example.photoeditorcompose.model.EditImageModelRequest
import com.example.photoeditorcompose.model.ImageResponse
import com.example.photoeditorcompose.network.RetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MainViewModel : ViewModel() {
    var imageModel: ImageModel? = null
    var fileUpdateFromEditor: File? = null

    //    var jsonResult: String = ""
    var file: File? = null
    private val apiService = RetrofitInstance.api
    val images: MutableState<List<ImageModel>> = mutableStateOf(ImageResponse().images)
    val isEdit: MutableState<Boolean> = mutableStateOf(false)
    private val TAG = "MainViewModel"
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

    fun editImage(id: Int,jsonResult: String) {
        if (fileUpdateFromEditor != null && jsonResult.isNotEmpty()) {
            viewModelScope.launch {
                fileUpdateFromEditor?.let {
                    try {
                        val newImageFile = RequestBody.create("image/*".toMediaTypeOrNull(), fileUpdateFromEditor!!)
                        val newImagePart =
                            MultipartBody.Part.createFormData("newImage", it.name, newImageFile)

                        val response = apiService.editImage(
                            getEditImageRequestBody(
                                EditImageModelRequest(text = jsonResult, id = id)),
                            newImagePart
                        )
                        Log.d(TAG, "getImages: here ${response.status}")
                        isEdit.value = true

                    } catch (e: Exception) {
                        // Handle errors here
                        Log.d(TAG, "getImages: ${e.message}")
                    }
                }

            }
        }
    }

    fun createPartFromString(descriptionString: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), descriptionString)
    }

    fun getEditImageRequestBody(model: EditImageModelRequest): Map<String, RequestBody> {
        val map = mutableMapOf<String, RequestBody>()

        // Assuming EditImageModelRequest has fields like "title", "description", etc.
        map["text"] = createPartFromString(model.text)
        model.id?.let {
            map["id"] = createPartFromString(model.id.toString())
        }

        // Add other fields from EditImageModelRequest similarly
        return map
    }


    fun saveImage(jsonResult: String, oldImage: File) {
        Log.d(TAG, "saveImage: ")
        if (fileUpdateFromEditor != null) {
            Log.d(TAG, "saveImage: not null")
            viewModelScope.launch {
                Log.d(TAG, "saveImage: launch")
                fileUpdateFromEditor?.let {
                    Log.d(TAG, "saveImage: files")
                    try {
                        if(oldImage.exists()) Log.d(TAG, "saveImage: Exist Old")
                        if(fileUpdateFromEditor!!.exists()) Log.d(TAG, "saveImage: Exist New")
                        val oldImageFile = RequestBody.create("image/*".toMediaTypeOrNull(), oldImage)
                        val newImageFile = RequestBody.create("image/*".toMediaTypeOrNull(), fileUpdateFromEditor!!)

                        val oldImagePart =
                            MultipartBody.Part.createFormData("oldImage", it.name, oldImageFile)
                        val newImagePart =
                            MultipartBody.Part.createFormData("newImage", it.name, newImageFile)
                        val response = apiService.saveImage(getEditImageRequestBody(
                            EditImageModelRequest(text = jsonResult)),
                            oldImagePart,newImagePart
                        )
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

}