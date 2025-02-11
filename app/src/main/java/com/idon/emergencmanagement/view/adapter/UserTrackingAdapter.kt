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
import com.idon.emergencmanagement.model.UserFull
import com.panuphong.smssender.helper.HandleClickListener
import com.zine.ketotime.util.Constant
import kotlinx.android.synthetic.main.item_img.view.*
import kotlinx.android.synthetic.main.item_user_tracking.view.*
import kotlinx.android.synthetic.main.item_worning.view.*
import kotlinx.android.synthetic.main.item_worning.view.nameTV
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserTrackingAdapter(
    context: Context, val listener: HandleClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var items: ArrayList<UserFull>? = null

    init {
        this.items = ArrayList()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user_tracking,
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


    fun getItem(position: Int): UserFull {
        return items!!.get(position)!!
    }


    override fun getItemViewType(position: Int): Int {

        Log.e("position", "${position} ${items?.size}")
        if (items == null || position == 0) return 1
        return 0
    }

    fun setItem(items: ArrayList<UserFull>) {
        if (this.items == null)
            this.items = ArrayList()

        this.items?.clear()
        this.items!!.addAll(items)
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<UserFull> {
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

        fun bind(data: UserFull?) {
//            val dec = DecimalFormat("#,###.00")

            itemView.apply {

                nameTV.text = " ${data!!.display_name}"
                typeTV.text = "ปรภท : ${data!!.type}"

                Glide.with(this).load("${Constant.BASE_URL}${data.avatar}").into(avatarImg)


            }
        }
    }

}