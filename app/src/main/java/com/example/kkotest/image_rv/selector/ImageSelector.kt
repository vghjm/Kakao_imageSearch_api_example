package com.example.kkotest.image_rv.selector

import android.util.Log
import com.example.kkotest.image_rv.data.ImageData

class ImageSelector {
    companion object {
        val SELECT_ALL = "all"
    }

    val imageDataWithCollection = hashMapOf(SELECT_ALL to mutableListOf<ImageData>())
    val lastAppendedIndexWithCollection = hashMapOf(SELECT_ALL to 0)

    fun appendImageList(imageDataList: List<ImageData>){
        imageDataWithCollection[SELECT_ALL]!!.addAll(imageDataList)
        lastAppendedIndexWithCollection[SELECT_ALL] = 0

        for(imageData in imageDataList){
            val collection = imageData.collection

            if(imageDataWithCollection.containsKey(collection)){
                imageDataWithCollection[collection]!!.add(imageData)
            }else{
                imageDataWithCollection[collection] = mutableListOf(imageData)
                lastAppendedIndexWithCollection[collection] = 0
            }
        }

        Log.d("결과", "이미지 추가됨 imageDataWithCollection[SELECT_ALL].size = ${imageDataWithCollection[SELECT_ALL]!!.size}")
    }

    fun resetImageDataList(){
        this.imageDataWithCollection.clear()
        this.lastAppendedIndexWithCollection.clear()

        imageDataWithCollection[SELECT_ALL] = mutableListOf()
        lastAppendedIndexWithCollection[SELECT_ALL] = 0
    }

    fun getCollectionList(): List<String> {
        return imageDataWithCollection.keys.toList()
    }

    fun selectAll(selectedCollection: String): List<ImageData>{
        indexUpdate(selectedCollection)

        return imageDataWithCollection[selectedCollection]!!
    }

    fun selectLastAppended(selectedCollection: String): List<ImageData>{
        val selectedList = imageDataWithCollection[selectedCollection]!!
        val startIndex = lastAppendedIndexWithCollection[selectedCollection]!!
        indexUpdate(selectedCollection)

        return List(selectedList.size - startIndex){
            selectedList[startIndex + it]
        }
    }

    private fun indexUpdate(selectedCollection: String){
        lastAppendedIndexWithCollection[selectedCollection] = imageDataWithCollection[selectedCollection]!!.size
    }
}