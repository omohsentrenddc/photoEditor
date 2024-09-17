package com.example.photoeditorcompose.model


import com.google.gson.annotations.SerializedName

data class ImageModel(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("new_image")
    var newImage: String = "",
    @SerializedName("old_image")
    var oldImage: String = "",
    @SerializedName("text")
    var text: String = ""
)