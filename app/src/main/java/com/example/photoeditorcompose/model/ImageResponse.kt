package com.example.photoeditorcompose.model


import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("data")
    var images: List<ImageModel> = listOf(),
    @SerializedName("msg")
    var message: String = "",
    @SerializedName("status")
    var status: Boolean = false
)