package com.idon.emergencmanagement.view.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.model.ImageWorningImg
import com.panuphong.smssender.helper.HandleClickListener
import com.zine.ketotime.util.Constant
import kotlinx.android.synthetic.main.item_img.view.*

class ImageShowAdapter(
    context: Context, val listener: HandleClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var items: ArrayList<ImageWorningImg>? = null

    init {
        this.items = ArrayList()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_img,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
//        Log.e("dd","sl ${items!!.size}")

        if (items == null) return 0
        return items!!.size
    }


    fun getItem(position: Int): ImageWorningImg {
        return items!!.get(position)!!
    }


    override fun getItemViewType(position: Int): Int {

        if (items == null || position == 0) return 1
        return 0
    }

    fun setItem(items: ImageWorningImg) {
        if (this.items == null)
            this.items = ArrayList()
        this.items!!.add(items)
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<ImageWorningImg> {
        return items!!
    }

    fun removeItem(position: Int) {
        items!!.removeAt(position)
        notifyDataSetChanged()

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        if (holder is ViewHolder) {

            holder.bind(items!![position])
            holder.itemView.setOnClickListener {
                listener.onItemClick(
                    holder.itemView,
                    position,
                    1
                )
            }

        }


    }


    inner class ViewHolder(itemsView: View) : RecyclerView.ViewHolder(itemsView) {

        fun bind(data: ImageWorningImg?) {
//            val dec = DecimalFormat("#,###.00")

            itemView.apply {

//


                cancleAction.visibility = View.GONE

                Log.e("ddd","${Constant.BASE_URL}${data!!.img}")
                Glide.with(context).load("${Constant.BASE_URL}${data!!.img}").into(imgIM)


            }
        }
    }


}