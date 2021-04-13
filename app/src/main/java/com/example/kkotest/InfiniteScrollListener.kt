package com.example.kkotest

import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class InfiniteScrollListener(
    val loadMoreData: () -> Boolean,
    val layoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    private var loading = false
    private val visibleThreshold = 4
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        // 아래로 스크롤한 경우
        if(dy >= 0){
            visibleItemCount = recyclerView.childCount
            totalItemCount = layoutManager.itemCount
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
            Log.d("결과", "visibleItemCount = ${recyclerView.childCount} totalItemCount = ${layoutManager.itemCount} firstVisibleItem = ${layoutManager.findFirstVisibleItemPosition()}")

            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold) && firstVisibleItem >= 0) {
                // 끝에 도달 했을 때
                Log.d("결과", "Scroll end reached!")

                loading = true
                loadMoreData()
            }
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

        Log.d("결과", "newState")
    }

    fun refresh(){
        loading = false
    }

    fun loadingFinish(){
        loading = false
    }
}