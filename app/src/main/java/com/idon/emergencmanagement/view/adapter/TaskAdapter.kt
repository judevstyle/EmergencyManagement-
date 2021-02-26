package com.panuphong.smssender.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.angads25.toggle.interfaces.OnToggledListener
import com.github.angads25.toggle.model.ToggleableView
import com.github.angads25.toggle.widget.LabeledSwitch
import com.panuphong.smssender.helper.HandleClickListener
import com.shin.tmsuser.database.entity.ConfigTb
import com.zine.ketotime.util.Constant
//
//
//class TaskAdapter (context: Context, clickItem: HandleClickListener
//) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//
//    var items: ArrayList<ConfigTb>? = null
//    lateinit var clickItem:HandleClickListener
//    init {
//        this.clickItem = clickItem
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//
//            return ViewHolder(
//                LayoutInflater.from(parent.context).inflate(
//                    R.layout.item_task,
//                    parent,
//                    false
//                )
//            )
//
//    }
//
//
//    override fun getItemCount(): Int {
//        items?.let {
//            return items!!.size
//        }
//        return 0
//
//    }
//
//
//    fun getItem(position: Int): ConfigTb {
//        return items!!.get(position)!!
//    }
//
//
//    override fun getItemViewType(position: Int): Int {
//
//        if (items == null || position == items!!.size ) return 1
//        return 0
//    }
//
//    fun setItem(items: List<ConfigTb>) {
//        if (this.items == null)
//            this.items = ArrayList()
//        else this.items!!.clear()
//        this.items!!.addAll(items)
//        notifyDataSetChanged()
//    }
//
//    fun getData():ArrayList<ConfigTb>{
//        return items!!
//    }
//
//    fun removeItem(position: Int) {
//        items!!.removeAt(position)
//        notifyDataSetChanged()
//
//    }
//
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//
//            if (holder is ViewHolder) {
//
//                val data = items!!.get(position)
//                holder.bind(data)
//
//            }
//
//    }
//
//    inner class ViewHolder(itemsView: View) : RecyclerView.ViewHolder(itemsView) {
//
//        fun bind(data:ConfigTb) {
//            itemView.apply {
//
//
//                taskTV.text = "${data.task_name}"
//                senderNameTV.text = "Sender name : ${data.sendername}"
//                bankCodeTV.text = "Bank code : ${data.smsdata.bankcode}"
//                endpointTV.text = "Enpoint : ${data.endpoint}"
//
//
//                switchSW.isOn = data.task_status
//                switchSW.setOnToggledListener(object : OnToggledListener {
//
//                    override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {
//
//                        itemView.tag = isOn
//                        clickItem.onItemClick(itemView,adapterPosition, Constant.ACTION_CLICKITEM_STATUS)
//
//
//                    }
//                })
//
//                setOnClickListener {
//
//                    clickItem.onItemClick(itemView,adapterPosition, Constant.ACTION_CLICKITEM)
//
//                }
//
//            }
//
//        }
//    }
//}