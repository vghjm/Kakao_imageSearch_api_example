package com.example.kkotest.image_rv.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.kkotest.R
import com.example.kkotest.image_rv.data.ImageData
import com.squareup.picasso.Picasso

class ImageViewAdapter(private val imageDataList: MutableList<ImageData>, private var layoutWidth: Int): RecyclerView.Adapter<ImageViewAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(imageData: ImageData, layoutWidth: Int) {
            itemView.findViewById<ImageView>(R.id.iv_image).apply {
                this.layoutParams.width = layoutWidth
                this.layoutParams.height = layoutWidth

                Picasso.get().load(imageData.thumbnail_url)
                        .into(this)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_image, parent, false)

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(imageDataList[position], layoutWidth)
    }

    override fun getItemCount(): Int {
        return imageDataList.size
    }

    fun replaceImageDataList(imageDataList: List<ImageData>){
        this.imageDataList.clear()
        this.imageDataList.addAll(imageDataList)
        notifyDataSetChanged()
    }

    fun appendImageDataList(imageDataList: List<ImageData>){
        val start = this.imageDataList.size
        this.imageDataList.addAll(imageDataList)
        notifyItemRangeChanged(start, imageDataList.size)
    }

    fun replaceLayoutWidth(layoutWidth: Int){
        this.layoutWidth = layoutWidth
        notifyDataSetChanged()
    }

    fun resetImageDataList(){
        this.imageDataList.clear()
        notifyDataSetChanged()
    }
}