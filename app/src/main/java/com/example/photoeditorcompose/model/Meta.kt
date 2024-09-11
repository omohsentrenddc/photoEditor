package com.example.photoeditorcompose.model


import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("current_page")
    var currentPage: Int = 0,
    @SerializedName("from")
    var from: Int = 0,
    @SerializedName("last_page")
    var lastPage: Int = 0,
    @SerializedName("links")
    var links: List<Link> = listOf(),
    @SerializedName("path")
    var path: String = "",
    @SerializedName("per_page")
    var perPage: Int = 0,
    @SerializedName("to")
    var to: Int = 0,
    @SerializedName("total")
    var total: Int = 0
)