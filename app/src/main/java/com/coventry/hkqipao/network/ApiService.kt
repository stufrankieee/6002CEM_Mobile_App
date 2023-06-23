package com.coventry.hkqipao.network

import com.coventry.hkqipao.model.Photo
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v2/list")
    fun getPhotoList(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<List<Photo>>
}

object ApiServiceInstance {
    private const val BASE_URL = "https://picsum.photos"

    val apiService: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}

//private const val BASE_URL = "https://picsum.photos/v2/list"
//
//val retrofit = Retrofit.Builder()
//    .baseUrl(BASE_URL)
//    .addConverterFactory(GsonConverterFactory.create())
//    .build()
//
//val apiService = retrofit.create(ApiService::class.java)