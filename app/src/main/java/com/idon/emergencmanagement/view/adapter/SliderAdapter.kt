package com.idon.emergencmanagement.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.model.SliderItem
import com.panuphong.smssender.helper.HandleClickListener
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(context: Context,handleClickListener: HandleClickListener) : SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {


    private var click:HandleClickListener? = null
    private val context: Context? = null
    private val mSliderItems: ArrayList<SliderItem> = ArrayList()


    init {
        click = handleClickListener
    }


    inner class SliderAdapterVH(itemView: View) :
        SliderViewAdapter.ViewHolder(itemView) {
        var itemViews: View
        var imageViewBackground: ImageView
        var imageGifContainer: ImageView
        var textViewDescription: TextView


        init {
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider)
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container)
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider)
            this.itemViews = itemView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent!!.context)
            .inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)

    }


    fun addItem(sliderItem: ArrayList<SliderItem>) {
        mSliderItems.clear()
        mSliderItems.addAll(sliderItem)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mSliderItems.size

    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH?, position: Int) {

        val sliderItem = mSliderItems[position]

        viewHolder!!.textViewDescription.text = sliderItem.getDescription()
        viewHolder.textViewDescription.setTextSize(16F)
        viewHolder.textViewDescription.setTextColor(Color.WHITE)

//        Log.e("ddd",sliderItem.getImageUrl())
        Glide.with(viewHolder.itemViews)
            .load(sliderItem.getImageUrl())
            .fitCenter()
            .into(viewHolder.imageViewBackground)

        viewHolder.itemViews.setOnClickListener {
            click!!.onItemClick(it,position,0)
//            Toast.makeText(context, "This is item in position $position", Toast.LENGTH_SHORT)
//                .show()
        }


    }
}