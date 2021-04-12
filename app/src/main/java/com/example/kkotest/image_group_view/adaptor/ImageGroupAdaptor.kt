package com.example.kkotest.image_group_view.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.kkotest.R
import com.example.kkotest.image_group_view.data.ImageGroup
import com.squareup.picasso.Picasso

class ImageGroupAdaptor(private var imageGroupList: List<ImageGroup>):
    RecyclerView.Adapter<ImageGroupAdaptor.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val imageViewList = arrayListOf(
            itemView.findViewById<ImageView>(R.id.iv_num1),
            itemView.findViewById(R.id.iv_num2),
            itemView.findViewById(R.id.iv_num3),
            itemView.findViewById(R.id.iv_num4),
            itemView.findViewById(R.id.iv_num5)
        )

        fun bind(imageGroup: ImageGroup) {
            val imageListSize = imageGroup.imageList.size

            for(index in 0..4){
                val iv = imageViewList[index]

                if(index < imageListSize){
                    val image = imageGroup.imageList[index]
                    iv.visibility = View.VISIBLE
//                    iv.layoutParams.width = image.width

                    Picasso.get().load(image.thumbnail_url)
                            .resize(image.width, image.height)
                            .into(iv)
                }else{
                    iv.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_image_group, parent, false)

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(imageGroupList[position])
    }

    override fun getItemCount(): Int {
        return imageGroupList.size
    }

    fun replaceImageGroupList(newImageGroupList: List<ImageGroup>){
        imageGroupList = newImageGroupList
        notifyDataSetChanged()
    }
}