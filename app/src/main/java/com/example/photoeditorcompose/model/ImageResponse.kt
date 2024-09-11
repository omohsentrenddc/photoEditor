package com.example.photoeditorcompose.model


import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("data")
    var images: List<ImageModel> = listOf(),
    @SerializedName("links")
    var links: Links = Links(),
    @SerializedName("meta")
    var meta: Meta = Meta(),
    @SerializedName("status")
    var status: Int = 0
)