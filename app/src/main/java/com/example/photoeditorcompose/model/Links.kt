package com.example.photoeditorcompose.model


import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("first")
    var first: String = "",
    @SerializedName("last")
    var last: String = "",
    @SerializedName("next")
    var next: Any = Any(),
    @SerializedName("prev")
    var prev: Any = Any()
)