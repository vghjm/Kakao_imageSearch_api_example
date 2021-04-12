package com.example.kkotest.kakao.api.search.image.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.kkotest.kakao.api.KakaoApi
import com.example.kkotest.kakao.api.search.image.client.SearchImageClient
import com.example.kkotest.kakao.api.search.image.data.SearchImageRequest
import com.example.kkotest.kakao.api.search.image.data.SearchImageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object SearchImageRepository {
    const val SORT_ACCURACY = "accuracy"
    const val SORT_RECENCY = "recency"
    private const val DEFAULT_PAGE = 1
    private const val DEFAULT_SIZE = 80
    private val API_KEY = "KakaoAK {}".replace("{}", KakaoApi.API_KEY)
    private val result = MutableLiveData<SearchImageResponse>()
    private val request = SearchImageRequest(null, SORT_ACCURACY, DEFAULT_PAGE, DEFAULT_SIZE)
    private var searchImageResponse: SearchImageResponse? = null

    fun getSearchImageResult(): MutableLiveData<SearchImageResponse> {
        if(request.query == null) return result
        val call = SearchImageClient.searchImageService
        val parameterMap = HashMap<String, String>()

        parameterMap["query"] = request.query!!
        parameterMap["sort"] = request.sort.toString()
        parameterMap["page"] = request.page.toString()
        parameterMap["size"] = request.size.toString()

        call.getSearchResult(parameterMap, API_KEY).enqueue(object : Callback<SearchImageResponse> {
            override fun onResponse(
                    call: Call<SearchImageResponse>,
                    response: Response<SearchImageResponse>
            ) {
                if (response.body() != null) {
                    Log.d("결과:", "성공적 결과 ${response.body()!!.meta}")
                    if(searchImageResponse == null) searchImageResponse = response.body() as SearchImageResponse
                    else {
                        searchImageResponse!!.meta.is_end = response.body()!!.meta.is_end

                        for(imageInfo in response.body()!!.documents){
                            searchImageResponse!!.documents.add(imageInfo)
                        }
                    }

                    if(searchImageResponse!!.meta.is_end){
                        result.value = searchImageResponse
                        searchImageResponse = null
                    }else{
                        request.page = request.page?.plus(1)
                        getSearchImageResult()
                    }
                }else{
                    Log.d("결과:", " response body == null")
                }
            }

            override fun onFailure(call: Call<SearchImageResponse>, t: Throwable) {
                Log.d("결과:", "실패 : $t")
            }
        })

        return result
    }

    fun setSearchImageRequest(query: String?, sort: String?, page: Int? = DEFAULT_PAGE, size: Int? = DEFAULT_SIZE){
        request.apply {
            this.query = query
            this.sort = sort
            this.page = page
            this.size = size
        }
    }
}