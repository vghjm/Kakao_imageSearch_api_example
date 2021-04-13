package com.example.kkotest.image_rv.selector

import android.util.Log
import com.example.kkotest.image_rv.data.ImageData

class ImageSelector {
    val allImageDataList = mutableListOf<ImageData>()
    val imageDataWithCollection = HashMap<String, MutableList<ImageData>>()
    var lastAppendedIndex = 0
    val lastAppendedIndexWithCollection = HashMap<String, Int>()
    companion object {
        val SELECT_ALL = "all"
    }

    fun appendImageList(imageDataList: List<ImageData>){
        this.allImageDataList.addAll(imageDataList)

        for(imageData in imageDataList){
            val collection = imageData.collection

            if(imageDataWithCollection.containsKey(collection)){
                imageDataWithCollection[collection]!!.add(imageData)
            }else{
                imageDataWithCollection[collection] = mutableListOf(imageData)
                lastAppendedIndexWithCollection[collection] = 0
            }
        }

        Log.d("결과", "이미지 추가됨 allImageDataList.size = ${allImageDataList.size}")
    }

    fun resetImageDataList(){
        this.allImageDataList.clear()
        this.imageDataWithCollection.clear()
        this.lastAppendedIndex = 0
        this.lastAppendedIndexWithCollection.clear()
    }

    fun getCollectionList(): List<String> {
        return imageDataWithCollection.keys.toList()
    }

    fun selectAll(selectedCollection: String): List<ImageData>{
        if(selectedCollection == SELECT_ALL){
            lastAppendedIndex = allImageDataList.size

            return allImageDataList
        }else{
            lastAppendedIndexWithCollection[selectedCollection] = imageDataWithCollection[selectedCollection]!!.size

            return imageDataWithCollection[selectedCollection]!!
        }
    }

    fun selectLastAppended(selectedCollection: String): List<ImageData>{
        val selectedList: MutableList<ImageData>
        val startIndex: Int

        if(selectedCollection == SELECT_ALL){
            startIndex = lastAppendedIndex
            selectedList = allImageDataList
            lastAppendedIndex = allImageDataList.size
        }else{
            startIndex = lastAppendedIndexWithCollection[selectedCollection]!!
            selectedList = imageDataWithCollection[selectedCollection]!!
            lastAppendedIndexWithCollection[selectedCollection] = imageDataWithCollection[selectedCollection]!!.size
        }

        return List(selectedList.size - startIndex){
            selectedList[startIndex + it]
        }
    }
}