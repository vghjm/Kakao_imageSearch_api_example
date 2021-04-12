package com.example.kkotest.kakao.api.search.image.data

data class SearchImageResponse(
        val meta: Meta,
        val documents: MutableList<ImageInfo>
)

data class Meta(
        var is_end: Boolean,
        val pageable_count: Int,
        val total_count: Int
)

data class ImageInfo(
    val collection: String,
    val datetime: String,
    val display_sitename: String,
    val doc_url: String,
    val height: Int,
    val image_url: String,
    val thumbnail_url: String,
    val width: Int
)