package com.idon.emergencmanagement.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.model.WorningData
import com.idon.emergencmanagement.model.WorningDataItem
import com.idon.emergencmanagement.view.adapter.WorningRequestAdapter
import com.panuphong.smssender.helper.HandleClickListener
import com.tt.workfinders.BaseClass.BaseActivity
import com.zine.ketotime.network.HttpMainConnect
import kotlinx.android.synthetic.main.activity_worning.*
import kotlinx.android.synthetic.main.toolbar_title.*
import org.parceler.Parcels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorningRequestActivity : BaseActivity(),HandleClickListener {


    private lateinit var adapters: WorningRequestAdapter

    override fun getContentView(): Int {
        return R.layout.activity_worning
    }


    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)


        toolbar.title = "คำขอแจ้งเตือนภัย"
        setSupportActionBar(toolbar)
        showBackArrow()



        adapters = WorningRequestAdapter(this,this)

        cv_worning.apply {

            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = adapters

        }

    }

    override fun onResume() {
        super.onResume()

        loadData()
    }
    fun loadData(){

        HttpMainConnect()
            .getApiService()
            .getRequest()
            .enqueue(CallWorning())

    }

    override fun onItemClick(view: View, position: Int, action: Int) {

        startActivity(Intent(this,WorningRequestDescActivity::class.java)
            .putExtra("data",Parcels.wrap(adapters.getItem(position)))
        )

    }


    inner class CallWorning : Callback<WorningData> {

        init {
            showProgressDialog()
        }

        override fun onFailure(call: Call<WorningData>, t: Throwable) {
            showToast("err")
            hideDialog()
        }

        override fun onResponse(call: Call<WorningData>, response: Response<WorningData>) {
            hideDialog()

            if (response.isSuccessful) {

                response.body()?.let {


                    adapters.setItem(it)


                }

            } else {
                showToast("err")

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }




}