package com.example.kkotest.kakao.api.search.image.client

import com.example.kkotest.kakao.api.KakaoApi
import com.example.kkotest.kakao.api.search.image.service.SearchImageService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SearchImageClient {
    private val retrofitClient: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(KakaoApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    val searchImageService: SearchImageService by lazy {
        retrofitClient.build().create(SearchImageService::class.java)
    }
}