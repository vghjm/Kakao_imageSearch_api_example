package com.example.kkotest.kakao_api.search_image.detail

import com.example.kkotest.kakao_api.KakaoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SearchImageRetrofitClient {
    private val retrofitClient: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(KakaoApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    val searchImageService: SearchImageService by lazy {
        retrofitClient.build().create(SearchImageService::class.java)
    }
}