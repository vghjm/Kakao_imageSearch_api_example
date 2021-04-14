package com.example.kkotest

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kkotest.image_rv.adapter.ImageViewAdapter
import com.example.kkotest.image_rv.data.ImageData
import com.example.kkotest.image_rv.selector.ImageCollectionSelector
import com.example.kkotest.kakao_api.search_image.SearchImageController

class MainActivity : AppCompatActivity() {
    private lateinit var searchImageRV: RecyclerView // 검색결과 보여주는 리사이클러 뷰
    private lateinit var collectionRG: RadioGroup // 콜렉션 라디어버튼 그룹
    private lateinit var defaultButton: RadioButton // 전체선택 라디오 버튼
    private lateinit var imageViewAdaptor: ImageViewAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var infiniteScrollListener: InfiniteScrollListener
    private val imageSelector = ImageCollectionSelector()
    private val searchImageController = SearchImageController()
    private var checkedCollection: String = ImageCollectionSelector.SELECT_ALL
    private val NEED_MODE_IMAGE_COUNT = 8


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewAdaptor = ImageViewAdapter(mutableListOf(), getDeviceWidth() / 2)

        // 검색결과 이미지를 보여주는 searchImageRV 등록 및 설정
        searchImageRV = findViewById<RecyclerView>(R.id.rv_image_view).apply {
            gridLayoutManager = GridLayoutManager(this.context, 2)
            infiniteScrollListener = InfiniteScrollListener({ searchImageController.loadAdditionalData() }, gridLayoutManager)

            this.adapter = imageViewAdaptor
            this.layoutManager = gridLayoutManager
            this.addOnScrollListener(infiniteScrollListener)
        }

        // collection category 선택을 위한 collectionRG 등록 및 설정
        collectionRG = findViewById<RadioGroup>(R.id.rg_category).apply{
            this.setOnCheckedChangeListener{ radioGroup: RadioGroup, checkedId: Int ->
                radioGroup.findViewById<RadioButton>(checkedId).text.toString().also{
                    // 기존과 다른 컬렉션 버튼 누르면 이미지뷰의 데이터를 교체한다.
                    if(it != checkedCollection){
                        checkedCollection = it
                        searchImageRV.smoothScrollToPosition(0)

                        imageSelector.selectAll(checkedCollection).apply {
                            imageViewAdaptor.replaceImageDataList(this)

                            if(this.size < NEED_MODE_IMAGE_COUNT){
                                searchImageController.loadAdditionalData()
                            }
                        }
                    }
                }
            }
        }

        // 기본적인 전체선택 버튼
        defaultButton = RadioButton(this).apply {
            this.text = ImageCollectionSelector.SELECT_ALL
            collectionRG.addView(this)
            this.isChecked = true
        }

        // collection 변경시 화면에 반영함
        imageSelector.getCollectionLiveData().observe(this, {
            updateRadioGroupMember(collectionRG, it)
        })

        // 검색 버튼 등록
        findViewById<Button>(R.id.btn_search).setOnClickListener{
            val query = findViewById<EditText>(R.id.et_keyword).text.toString()
            if(query == ""){
                Toast.makeText(this, "검색어를 입력해주세요?", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            imageSelector.resetImageDataList()
            imageViewAdaptor.resetImageDataList()
            searchImageController.setSearchImageRequest(query)
            searchImageController.search()
        }

        // 서버에서 검색결과를 받아오면 imageSelector에 데이터 연결
        searchImageController.getSearchImageResult().observe(this, { result ->
            if(result.meta.is_end) Toast.makeText(this, "마지막 결과입니다.", Toast.LENGTH_SHORT).show()

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

            // 추가된 데이터를 화면에 연결
            imageSelector.appendImageList(imageList)
            imageSelector.selectLastAppended(checkedCollection).apply {
                imageViewAdaptor.appendImageDataList(this)

                if(this.size < NEED_MODE_IMAGE_COUNT){
                    searchImageController.loadAdditionalData()
                }
            }

            infiniteScrollListener.loadingFinish()
        })
    }

    private fun updateRadioGroupMember(radioGroup: RadioGroup, nextTextSet: MutableSet<String>) {
        val currentTextSet = mutableSetOf<String>()
        for(childView in radioGroup.children){
            val radioButton = childView as RadioButton
            currentTextSet.add(radioButton.text.toString())
        }
        val removedTextSet = currentTextSet.subtract(nextTextSet)
        val addedTextSet = nextTextSet.subtract(currentTextSet)

        // 없어질부분 제거
        for(removedText in removedTextSet){
            for(collectionView in radioGroup.children){
                val radioButton = collectionView as RadioButton
                if(radioButton.text == removedText){
                    // 체크된 버튼이 사라지면 기본값으로 등록
                    if(radioButton.text == checkedCollection) checkedCollection = ImageCollectionSelector.SELECT_ALL
                    radioGroup.removeView(collectionView)
                    break
                }
            }
        }

        // 새로운부분 추가
        for(addedText in addedTextSet){
            val radioButton = RadioButton(this)
            radioButton.text = addedText
            radioGroup.addView(radioButton)
        }

        // 체크되어있는지 확인
        for(childView in radioGroup.children){
            val radioButton = childView as RadioButton
            if(radioButton.text == checkedCollection){
                if(!radioButton.isChecked) radioButton.isChecked = true
                break
            }
        }
    }

    private fun getDeviceWidth(): Int {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        return metrics.widthPixels
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // 세로모드
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            findViewById<ConstraintLayout>(R.id.constraintLayout_nav).layoutParams.height = applicationContext.resources.getDimension(R.dimen.dp_120).toInt()
            gridLayoutManager.spanCount = 2
            imageViewAdaptor.replaceLayoutWidth(getDeviceWidth() / 2)
        }

        // 가로모드
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            findViewById<ConstraintLayout>(R.id.constraintLayout_nav).layoutParams.height = applicationContext.resources.getDimension(R.dimen.dp_90).toInt()
            gridLayoutManager.spanCount = 4
            imageViewAdaptor.replaceLayoutWidth(getDeviceWidth() / 4)
        }
    }
}

