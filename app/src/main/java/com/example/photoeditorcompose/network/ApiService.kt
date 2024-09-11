package com.example.photoeditorcompose.network

import com.example.photoeditorcompose.model.ImageModelRequest
import com.example.photoeditorcompose.model.ImageResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    //https://app.tahann.co/dashboard/api/v1/tests
    @GET("tests")
    suspend fun getImages(): ImageResponse

    @Multipart
    @POST("tests")
    suspend fun submitImage(@Body model : ImageModelRequest,
                            @Part image: MultipartBody.Part
    ): ImageResponse
}