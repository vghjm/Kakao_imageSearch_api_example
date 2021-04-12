package com.example.kkotest.kakao.api.search.image.data

data class SearchImageRequest(
        var query: String?,
        var sort: String?,
        var page: Int?,
        var size: Int?
)