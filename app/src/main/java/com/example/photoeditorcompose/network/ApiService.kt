package com.example.photoeditorcompose.network

import com.example.photoeditorcompose.model.EditImageModelRequest
import com.example.photoeditorcompose.model.ImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface ApiService {
    //https://app.tahann.co/dashboard/api/v1/tests
    /*

     */
    @GET("test/all")
    suspend fun getImages(): ImageResponse

    @Multipart
    @POST("test/store")
    suspend fun saveImage(@PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
                          @Part oldImage: MultipartBody.Part, @Part newImage: MultipartBody.Part
    ): ImageResponse

    @Multipart
    @POST("tests")
    suspend fun editImage(@Body model : EditImageModelRequest,
                          @Part image: MultipartBody.Part
    ): ImageResponse

    @POST("tests")
    suspend fun editImage(@Body model : EditImageModelRequest
    ): ImageResponse
}