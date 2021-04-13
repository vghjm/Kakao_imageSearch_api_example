package com.example.kkotest

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kkotest.image_rv.adapter.ImageViewAdapter
import com.example.kkotest.image_rv.data.ImageData
import com.example.kkotest.image_rv.selector.ImageSelector
import com.example.kkotest.kakao_api.search_image.SearchImageController

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class MainActivity : AppCompatActivity() {
    private lateinit var searchImageRV: RecyclerView
    private lateinit var categoryRG: RadioGroup
    private var paddingSize = 0
    private lateinit var imageViewAdaptor: ImageViewAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var infiniteScrollListener: InfiniteScrollListener
    private lateinit var searchTextView: TextView
    private val imageSelector = ImageSelector()
    private var checkedCollection: String = ImageSelector.SELECT_ALL
    private val searchImageController = SearchImageController()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        paddingSize = this.resources.getDimension(R.dimen.imageView_paddingSize).toInt()
        imageViewAdaptor = ImageViewAdapter(mutableListOf(), (getDeviceWidth() - paddingSize) / 2)

        // 검색결과 이미지를 보여주는 recycler view 등록 및 설정
        findViewById<RecyclerView>(R.id.rv_image_view).apply {
            searchImageRV = this
            gridLayoutManager = GridLayoutManager(this.context, 2)
            infiniteScrollListener = InfiniteScrollListener({ searchImageController.loadAdditionalData() }, gridLayoutManager)

            this.adapter = imageViewAdaptor
            this.layoutManager = gridLayoutManager
            this.addOnScrollListener(infiniteScrollListener)
        }

        // collection category 선택을 위한 radio group 등록 및 설정
        findViewById<RadioGroup>(R.id.rg_category).apply{
            categoryRG = this
            updateCategory(emptyList())

            this.setOnCheckedChangeListener{ radioGroup: RadioGroup, checkedId: Int ->
                radioGroup.findViewById<RadioButton>(checkedId).text.toString().also{
                    if(it != checkedCollection){
                        checkedCollection = it
                        searchImageRV.scrollToPosition(0)

                        imageSelector.selectAll(checkedCollection).apply {
                            imageViewAdaptor.replaceImageDataList(this)

                            if(this.size < 5){
                                searchImageController.loadAdditionalData()
                            }
                        }
                    }else{
                        imageSelector.selectLastAppended(checkedCollection).apply {
                            imageViewAdaptor.appendImageDataList(this)

                            if(this.size < 5){
                                searchImageController.loadAdditionalData()
                            }
                        }
                    }
                }
            }
        }

        // 검색 입력창 등록
        searchTextView = findViewById<EditText>(R.id.et_keyword)

        // 검색 버튼 등록
        findViewById<Button>(R.id.btn_search).setOnClickListener{
            val query = searchTextView.text.toString()
            if(query == ""){
                Toast.makeText(this, "검색어를 입력해주세요?", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.d("결과", " 검색어 정상 입력됨 : ${query}")

            searchImageController.setSearchImageRequest(query)
            imageSelector.resetImageDataList()
            searchImageController.search()
            imageViewAdaptor.resetImageDataList()
        }

        // 서버에서 검색결과를 받아오면 imageSelector에 데이터 연결 & 카테고리 갱신
        searchImageController.getSearchImageResult().observe(this, { result ->
            Log.d("결과", "관찰결과 ->  ${result.meta}")
            infiniteScrollListener.loadingFinish()

            val documents = result.documents
            val imageList = List(result.documents.size){
                val imageInfo = documents[it]

                ImageData(imageInfo.collection,
                        imageInfo.datetime,
                        imageInfo.display_sitename,
                        imageInfo.doc_url,
                        imageInfo.width,
                        imageInfo.height,
                        imageInfo.image_url,
                        imageInfo.thumbnail_url
                )
            }

            imageSelector.appendImageList(imageList)
            updateCategory(imageSelector.getCollectionList())
        })

    }

    @SuppressLint("SetTextI18n")
    fun updateCategory(collectionList: List<String>){
        categoryRG.removeAllViews()

        // 전체 선택 버튼 등록
        val defaultRadioButton = RadioButton(this)
        defaultRadioButton.text = ImageSelector.SELECT_ALL
        categoryRG.addView(defaultRadioButton)
        if(ImageSelector.SELECT_ALL == checkedCollection) defaultRadioButton.isChecked = true

        // 나머지 콜렉션 버튼 등록
        for(collection in collectionList){
            val radioButton = RadioButton(this)
            radioButton.text = collection
            categoryRG.addView(radioButton)
            if(collection == checkedCollection) radioButton.isChecked = true
        }
    }

    private fun getDeviceWidth(): Int {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        return metrics.widthPixels
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            findViewById<ConstraintLayout>(R.id.constraintLayout_nav).layoutParams.height = applicationContext.resources.getDimension(R.dimen.dp_120).toInt()
            gridLayoutManager.spanCount = 2
            imageViewAdaptor.replaceImageWidth((getDeviceWidth() - paddingSize) / 2)
        }

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            findViewById<ConstraintLayout>(R.id.constraintLayout_nav).layoutParams.height = applicationContext.resources.getDimension(R.dimen.dp_90).toInt()
            gridLayoutManager.spanCount = 4
            imageViewAdaptor.replaceImageWidth((getDeviceWidth() - paddingSize) / 4)
        }
    }
}

