package com.example.kkotest.image_rv.selector

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.kkotest.image_rv.data.ImageData

class ImageCollectionSelector {
    companion object {
        val SELECT_ALL = "all"
    }

    val imageCollectionHashMap = hashMapOf(SELECT_ALL to mutableListOf<ImageData>())
    val lastAppendedIndexHashMap = hashMapOf(SELECT_ALL to 0)
    val collectionSet = MutableLiveData<MutableSet<String>>()
    var dataSetChanged = false

    init {
        collectionSet.value = imageCollectionHashMap.keys
    }
    fun appendImageList(imageDataList: List<ImageData>){
        imageCollectionHashMap[SELECT_ALL]!!.addAll(imageDataList)

        for(imageData in imageDataList){
            val collection = imageData.collection

            if(imageCollectionHashMap.containsKey(collection)){
                imageCollectionHashMap[collection]!!.add(imageData)
            }else{
                imageCollectionHashMap[collection] = mutableListOf(imageData)
                lastAppendedIndexHashMap[collection] = 0
                dataSetChanged = true
            }
        }

        this.updateCollecionSet()
    }

    fun resetImageDataList(){
        this.imageCollectionHashMap.clear()
        this.lastAppendedIndexHashMap.clear()

        imageCollectionHashMap[SELECT_ALL] = mutableListOf()
        lastAppendedIndexHashMap[SELECT_ALL] = 0
        dataSetChanged = true
        this.updateCollecionSet()
    }

    fun getCollectionLiveData(): MutableLiveData<MutableSet<String>> {
        return collectionSet
    }

    fun selectAll(selectedCollection: String): List<ImageData>{
        indexUpdate(selectedCollection)

        return imageCollectionHashMap[selectedCollection]!!
    }

    fun selectLastAppended(selectedCollection: String): List<ImageData>{
        val selectedList = imageCollectionHashMap[selectedCollection]!!
        val startIndex = lastAppendedIndexHashMap[selectedCollection]!!
        indexUpdate(selectedCollection)

        return List(selectedList.size - startIndex){
            selectedList[startIndex + it]
        }
    }

    private fun indexUpdate(selectedCollection: String){
        lastAppendedIndexHashMap[selectedCollection] = imageCollectionHashMap[selectedCollection]!!.size
    }

    private fun updateCollecionSet(){
        if(dataSetChanged) {
            collectionSet.value = imageCollectionHashMap.keys
            dataSetChanged = false
        }
    }
}