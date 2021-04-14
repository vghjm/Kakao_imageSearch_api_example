package com.example.kkotest.kakao_api.search_image

data class SearchImageResponseData(
        var meta: Meta = DEFAULT_META,
        val documents: MutableList<ImageInfo> = mutableListOf()
){
    companion object{
        val DEFAULT_META = Meta(true, 0, 0)
    }
}

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