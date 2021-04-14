package com.example.kkotest.kakao_api.search_image.service

import com.example.kkotest.kakao_api.search_image.SearchImageResponseData
import retrofit2.Call
import retrofit2.http.*

interface SearchImageService {
    @GET("/v2/search/image")
    fun getSearchResult(
        @QueryMap(encoded = true) parameter: Map<String, String>,
        @Header("Authorization") key: String
    ): Call<SearchImageResponseData>
}