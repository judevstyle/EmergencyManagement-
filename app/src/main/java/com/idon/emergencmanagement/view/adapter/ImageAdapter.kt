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
import com.idon.emergencmanagement.model.ImgData
import com.panuphong.smssender.helper.HandleClickListener
import kotlinx.android.synthetic.main.item_img.view.*

class ImageAdapter(
    context: Context, val listener: HandleClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_LOADING: Int = 1
    private val VIEW_TYPE_ITEM: Int = 2
    private var STATE_ALLDEL: Boolean = false

    var items: ArrayList<ImgData>? = null

    init {
        this.items = ArrayList()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == 1)
            return ViewHolderAdd(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_img_add,
                    parent,
                    false
                )
            )
        else
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

        if (items == null) return 1
        return items!!.size +1
    }


    fun getItem(position: Int): ImgData {
        return items!!.get(position)!!
    }



    override fun getItemViewType(position: Int): Int {

        if (items == null || position == items!!.size ) return 1
        return 0
    }

    fun setItem(items: ImgData) {
        if (this.items == null)
            this.items = ArrayList()
        Log.e("dd","sl")
        this.items!!.add(items)
    }

    fun getData():ArrayList<ImgData>{
        return items!!
    }

    fun removeItem(position: Int) {
        items!!.removeAt(position)
        notifyDataSetChanged()

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        if (holder.itemViewType == 0) {
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
        } else {

            if (holder is ViewHolderAdd) {
                holder.itemView.setOnClickListener {
                    listener.onItemClick(it,position,2)

                }
            }


        }

    }


    inner class ViewHolder(itemsView: View) : RecyclerView.ViewHolder(itemsView) {

        fun bind(data: ImgData?) {
//            val dec = DecimalFormat("#,###.00")

            itemView.apply {

                data?.pid?.let {

                    if (!it.equals("")) {
//                        Glide.with(context).load("${KeySK.instance.getBaseURL()}${data!!.img}")
//                            .into(imgIM)
                    }else{

                        val decodedString: ByteArray =
                            Base64.decode(data?.img, Base64.DEFAULT)
                        val decodedByte: Bitmap =
                            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                        imgIM.setImageBitmap(decodedByte)

                    }

                }?: kotlin.run {


                    val decodedString: ByteArray =
                        Base64.decode(data?.img, Base64.DEFAULT)
                    val decodedByte: Bitmap =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                    imgIM.setImageBitmap(decodedByte)


                }
            }

        }
    }

    inner class ViewHolderAdd(itemsView: View) : RecyclerView.ViewHolder(itemsView)
}