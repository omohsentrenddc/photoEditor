package com.example.photoeditorcompose.model


import com.google.gson.annotations.SerializedName

data class Link(
    @SerializedName("active")
    var active: Boolean = false,
    @SerializedName("label")
    var label: String = "",
    @SerializedName("url")
    var url: String = ""
)