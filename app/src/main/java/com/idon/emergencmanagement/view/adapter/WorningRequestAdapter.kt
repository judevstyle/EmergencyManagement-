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
import com.idon.emergencmanagement.model.WorningDataItem
import com.panuphong.smssender.helper.HandleClickListener
import com.zine.ketotime.util.Constant
import kotlinx.android.synthetic.main.item_img.view.*
import kotlinx.android.synthetic.main.item_worning.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WorningRequestAdapter(
    context: Context, val listener: HandleClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var items: ArrayList<WorningDataItem>? = null

    init {
        this.items = ArrayList()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_worning,
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


    fun getItem(position: Int): WorningDataItem {
        return items!!.get(position)!!
    }


    override fun getItemViewType(position: Int): Int {

        Log.e("position","${position} ${items?.size}")
        if (items == null || position == 0) return 1
        return 0
    }

    fun setItem(items: ArrayList<WorningDataItem>) {
        if (this.items == null)
            this.items = ArrayList()

        this.items?.clear()
        this.items!!.addAll(items)
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<WorningDataItem> {
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
        val NEW_FORMAT = "dd/MM/yyyy HH:mm"
        val OLD_FORMAT  = "yyyy-MM-dd HH:mm:ss"
        fun bind(data: WorningDataItem?) {
//            val dec = DecimalFormat("#,###.00")

            itemView.apply {

                val oldDateString = "${data?.date_create}"
                val newDateString: String

                val sdf = SimpleDateFormat(OLD_FORMAT)
                val d: Date = sdf.parse(oldDateString)
                sdf.applyPattern(NEW_FORMAT)
                newDateString = sdf.format(d)
                dateTV.text = "ลงประกาศวันที่ ${newDateString}"
                nameTV.text = "ผู้รายงาน : ${data!!.user?.display_name}"
                topicTV.text = "${data.w_topic}"
                descTV.text = "${data.w_desc}"
                Glide.with(this).load("${Constant.BASE_URL}${data.user!!.avatar}").into(avatarIM)


            }
        }
    }

}