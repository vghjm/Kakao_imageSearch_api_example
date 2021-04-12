package com.example.kkotest.image_group_view.data

data class ImageGroup(
        val imageList: MutableList<Image>,
        var widthSum: Int,
        var imageCount: Int
)

data class Image(
    val collection: String,
    val datetime: String,
    val display_sitename: String,
    val doc_url: String,
    val width: Int,
    val height: Int,
    val image_url: String,
    val thumbnail_url: String
)