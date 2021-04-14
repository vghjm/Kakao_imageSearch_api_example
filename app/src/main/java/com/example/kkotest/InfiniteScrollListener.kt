package com.example.kkotest

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InfiniteScrollListener(
    val loadMoreData: () -> Boolean,
    val layoutManager: GridLayoutManager
) : RecyclerView.OnScrollListener() {

    private var loading = false
    private val visibleThreshold = 4
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        // 아래로 스크롤한 경우
        if(dy > 0){
            visibleItemCount = recyclerView.childCount
            totalItemCount = layoutManager.itemCount
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

            // 끝에 도달 했을 때
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold) && firstVisibleItem >= 0) {
                if(loadMoreData()) loading = true
            }
        }
    }

    fun loadingFinish(){
        loading = false
    }
}