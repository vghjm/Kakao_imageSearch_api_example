package com.example.kkotest.kakao_api.search_image.detail



data class SearchImageRequestData(
        var query: String? = null,
        var sort: SortType = DEFAULT_SORT,
        var page: Int = DEFAULT_PAGE,
        var size: Int = DEFAULT_SIZE
){
    companion object{
        val DEFAULT_SORT = SortType.SORT_ACCURACY
        val DEFAULT_PAGE = 1
        val DEFAULT_SIZE = 80
    }
}

enum class SortType {
    SORT_ACCURACY, SORT_RECENCY
}