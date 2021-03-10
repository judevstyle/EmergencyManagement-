package com.idon.emergencmanagement.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.view.adapter.WorningRequestAdapter
import com.panuphong.smssender.helper.HandleClickListener
import com.tt.workfinders.BaseClass.BaseActivity
import kotlinx.android.synthetic.main.activity_worning.*

class WorningRequestActivity : BaseActivity(),HandleClickListener{


    private lateinit var adapters: WorningRequestAdapter

    override fun getContentView(): Int {
        return R.layout.activity_worning
    }


    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)

        adapters = WorningRequestAdapter(this,this)

        cv_worning.apply {

            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = adapters

        }

    }

    override fun onItemClick(view: View, position: Int, action: Int) {


    }

}