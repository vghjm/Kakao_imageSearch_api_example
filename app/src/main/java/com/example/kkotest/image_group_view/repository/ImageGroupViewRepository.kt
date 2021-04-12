package com.example.kkotest.image_group_view.repository

import com.example.kkotest.image_group_view.data.Image
import com.example.kkotest.image_group_view.data.ImageGroup

class ImageGroupViewRepository(var layoutHeight: Int, var layoutWidth: Int, var paddingSize: Int) {
    var imageList = emptyList<Image>()
    val classifiedImageHashMap = HashMap<String, MutableList<Image>>()
    private val MAX_IMAGE_COUNT_IN_GROUP = 5
    companion object {
        val SELECT_ALL = "all"
    }

    fun updateImageList(imageList: List<Image>){
        this.imageList = imageList

        classifiedImageHashMap.clear()
        for(image in imageList){
            if(classifiedImageHashMap.containsKey(image.collection)){
                classifiedImageHashMap[image.collection]!!.add(image)
            }else{
                classifiedImageHashMap[image.collection] = mutableListOf(image)
            }
        }
    }

    fun getCollectionList(): List<String> {
        return classifiedImageHashMap.keys.toList()
    }

    fun makeImageGroup(collectionOption: String?): List<ImageGroup>{
        val selectedImageList = if(collectionOption == SELECT_ALL) imageList else classifiedImageHashMap[collectionOption]!!
        val imageHeight = layoutHeight - paddingSize * 2
        val imageGroupList = mutableListOf<ImageGroup>()

        for(image in selectedImageList){
            val imageWidth = image.width * imageHeight / image.height
            var hasEmptySpace = false
            val newImage = image.copy(width = imageWidth, height = imageHeight)

            for(imageGroup in imageGroupList){
                val widthSum = imageGroup.widthSum + imageWidth + paddingSize * 2

                if(imageGroup.imageCount < MAX_IMAGE_COUNT_IN_GROUP && widthSum <= layoutWidth){
                    imageGroup.imageList.add(newImage)
                    imageGroup.widthSum = widthSum
                    imageGroup.imageCount++
                    hasEmptySpace = true
                    break
                }
            }

            if(!hasEmptySpace){
                imageGroupList.add(ImageGroup(mutableListOf(newImage), imageWidth + paddingSize * 2, 1))
            }
        }

        return imageGroupList
    }

    fun updateLayoutWidth(layoutWidth: Int){
        this.layoutWidth = layoutWidth
    }

}