package com.example.kkotest.kakao_api.search_image

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.kkotest.kakao_api.KakaoApi
import com.example.kkotest.kakao_api.search_image.detail.SearchImageRequestData
import com.example.kkotest.kakao_api.search_image.client.SearchImageRetrofitClient
import com.example.kkotest.kakao_api.search_image.detail.SortType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchImageController {
    private val API_KEY = "KakaoAK {}".replace("{}", KakaoApi.API_KEY)
    private val result = MutableLiveData<SearchImageResponseData>()
    private val requestData = SearchImageRequestData()
    private var isConnecting = false
    private var hasNextData = false

    fun getSearchImageResult(): MutableLiveData<SearchImageResponseData> {
        return result
    }

    private fun attemptConnection(func: (call: Call<SearchImageResponseData>, response: Response<SearchImageResponseData>) -> Unit){
        isConnecting = true
        val call = SearchImageRetrofitClient.searchImageService
        val parameterMap = HashMap<String, String>()

        parameterMap["query"] = requestData.query!!
        parameterMap["sort"] = requestData.sort.toString()
        parameterMap["page"] = requestData.page.toString()
        parameterMap["size"] = requestData.size.toString()

        call.getSearchResult(parameterMap, API_KEY).enqueue(object : Callback<SearchImageResponseData> {
            override fun onResponse(
                    call: Call<SearchImageResponseData>,
                    response: Response<SearchImageResponseData>
            ) {
                func(call, response)
                isConnecting = false
            }

            override fun onFailure(call: Call<SearchImageResponseData>, t: Throwable) {
                Log.d("결과:", "실패 : $t")
                isConnecting = false
            }
        })
    }

    fun setSearchImageRequest(query: String,
                              sort: SortType = SearchImageRequestData.DEFAULT_SORT,
                              page: Int = SearchImageRequestData.DEFAULT_PAGE,
                              size: Int = SearchImageRequestData.DEFAULT_SIZE) {
        requestData.apply {
            this.query = query
            this.sort = sort
            this.page = page
            this.size = size
        }
    }

    fun loadAdditionalData(): Boolean {
        if(requestData.query == null || !hasNextData || isConnecting) return false
        requestData.page = requestData.page + 1

        attemptConnection{ call: Call<SearchImageResponseData>, response: Response<SearchImageResponseData> ->
            result.value = response.body() as SearchImageResponseData
            hasNextData = !result.value!!.meta.is_end
        }

        return true
    }

    fun search(): Boolean{
        if(requestData.query == null || isConnecting) return false

        attemptConnection{ call: Call<SearchImageResponseData>, response: Response<SearchImageResponseData> ->
            result.value = response.body() as SearchImageResponseData
            hasNextData = !result.value!!.meta.is_end
        }

        return true
    }
}