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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kkotest.image_group_view.adaptor.ImageGroupAdaptor
import com.example.kkotest.image_group_view.data.Image
import com.example.kkotest.image_group_view.repository.ImageGroupViewRepository
import com.example.kkotest.kakao.api.search.image.repository.SearchImageRepository

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class MainActivity : AppCompatActivity() {
    private lateinit var rvImageView: RecyclerView
    private lateinit var rgCategory: RadioGroup
    private lateinit var imageViewAdaptor: ImageGroupAdaptor
    private lateinit var searchTextView: TextView
    private lateinit var imageGroupViewRepository: ImageGroupViewRepository
    private var checkedCollection: String = ImageGroupViewRepository.SELECT_ALL


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 검색결과 이미지를 보여주는 recycler view 등록 및 설정
        rvImageView = findViewById(R.id.rv_image_view)
        imageViewAdaptor = ImageGroupAdaptor(emptyList())
        rvImageView.adapter = imageViewAdaptor
        rvImageView.layoutManager = LinearLayoutManager(this)

        // collection category 선택을 위한 radio group 등록 및 설정
        rgCategory = findViewById(R.id.rg_category)
        updateCategory(emptyList())
        rgCategory.setOnCheckedChangeListener{ radioGroup: RadioGroup, checkedId: Int ->
            checkedCollection = radioGroup.findViewById<RadioButton>(checkedId).text.toString()

            imageViewAdaptor.replaceImageGroupList(imageGroupViewRepository.makeImageGroup(checkedCollection))
        }

        // 검색 입력창 등록
        searchTextView = findViewById<EditText>(R.id.et_keyword)

        // 이미지를 그룹별로 모아서 보여주는 ImageGroupView 등록 및 설정
        imageGroupViewRepository = ImageGroupViewRepository(
                applicationContext.resources.getDimension(R.dimen.imageGroupView_height).toInt(),
                getDeviceWidth(),
                applicationContext.resources.getDimension(R.dimen.imageGroupView_paddingSize).toInt()
        )

        // 검색 버튼 등록
        findViewById<Button>(R.id.btn_search).setOnClickListener{
            val query = searchTextView.text.toString()

            if(query == "") SearchImageRepository.setSearchImageRequest(null, SearchImageRepository.SORT_ACCURACY)
            else SearchImageRepository.setSearchImageRequest(query, SearchImageRepository.SORT_ACCURACY)

            SearchImageRepository.getSearchImageResult()
        }

        // 서버에서 검색결과를 받아오면 뷰어에 데이터 연결
        SearchImageRepository.getSearchImageResult().observe(this, { searchImage ->
            val documents = searchImage.documents
            Log.d("결과", "관찰결과 ->  ${searchImage.meta}")
            val imageList = List(documents.size){
                val imageInfo = documents[it]

                Image(imageInfo.collection,
                        imageInfo.datetime,
                        imageInfo.display_sitename,
                        imageInfo.doc_url,
                        imageInfo.width,
                        imageInfo.height,
                        imageInfo.image_url,
                        imageInfo.thumbnail_url
                )
            }

            imageGroupViewRepository.updateImageList(imageList)
            imageViewAdaptor.replaceImageGroupList(imageGroupViewRepository.makeImageGroup(checkedCollection))
            rvImageView.smoothScrollToPosition(0)
            updateCategory(imageGroupViewRepository.getCollectionList())
        })
    }

    @SuppressLint("SetTextI18n")
    fun updateCategory(categoryList: List<String>){
        rgCategory.removeAllViews()

        val defaultRadioButton = RadioButton(this)
        defaultRadioButton.text = ImageGroupViewRepository.SELECT_ALL
        rgCategory.addView(defaultRadioButton)

        for(category in categoryList){
            val radioButton = RadioButton(this)
            radioButton.text = category
            rgCategory.addView(radioButton)
        }

        defaultRadioButton.isChecked = true
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
            imageGroupViewRepository.updateLayoutWidth(getDeviceWidth())
        }

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            findViewById<ConstraintLayout>(R.id.constraintLayout_nav).layoutParams.height = applicationContext.resources.getDimension(R.dimen.dp_90).toInt()
            imageGroupViewRepository.updateLayoutWidth(getDeviceWidth())
        }

        imageViewAdaptor.replaceImageGroupList(imageGroupViewRepository.makeImageGroup(checkedCollection))
    }
}

