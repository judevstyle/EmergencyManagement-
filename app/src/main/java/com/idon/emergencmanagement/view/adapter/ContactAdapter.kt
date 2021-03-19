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
import com.idon.emergencmanagement.model.ContactData
import com.panuphong.smssender.helper.HandleClickListener
import com.zine.ketotime.util.Constant
import kotlinx.android.synthetic.main.item_contact.view.*
import kotlinx.android.synthetic.main.item_img.view.*
import kotlinx.android.synthetic.main.item_user_tracking.view.*
import kotlinx.android.synthetic.main.item_user_tracking.view.avatarImg
import kotlinx.android.synthetic.main.item_user_tracking.view.typeTV
import kotlinx.android.synthetic.main.item_worning.view.*
import kotlinx.android.synthetic.main.item_worning.view.nameTV
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ContactAdapter(
    context: Context, val listener: HandleClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var items: ArrayList<ContactData>? = null

    init {
        this.items = ArrayList()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_contact,
                parent,
                false
            )
        )

    }

    fun clear(){
        this.items?.clear()
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
//        Log.e("dd","sl ${items!!.size}")

        if (items == null) return 0
        return items!!.size

    }


    fun getItem(position: Int): ContactData {
        return items!!.get(position)!!
    }


    override fun getItemViewType(position: Int): Int {

        Log.e("position", "${position} ${items?.size}")
        if (items == null || position == 0) return 1
        return 0
    }

    fun setItem(items: ArrayList<ContactData>) {
        if (this.items == null)
            this.items = ArrayList()

        this.items?.clear()
        this.items!!.addAll(items)
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<ContactData> {
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

        fun bind(data: ContactData) {
//            val dec = DecimalFormat("#,###.00")

            itemView.apply {

                nameTV.text = " ${data!!.contact_name}"
                typeTV.text = "จุดรับผิดชอบ : ${data!!.contact_position}"
                Glide.with(this).load("${Constant.BASE_URL}${data.avatar}").into(avatarImg)

                actionCall.setOnClickListener {
                    listener.onItemClick(this,adapterPosition,3)


                }



            }
        }
    }

}