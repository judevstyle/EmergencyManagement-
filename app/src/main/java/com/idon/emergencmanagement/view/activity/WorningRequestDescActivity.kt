package com.idon.emergencmanagement.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.model.ResponeDao
import com.idon.emergencmanagement.model.UserFull
import com.idon.emergencmanagement.model.WorningData
import com.idon.emergencmanagement.model.WorningDataItem
import com.idon.emergencmanagement.view.adapter.ImageAdapter
import com.idon.emergencmanagement.view.adapter.ImageShowAdapter
import com.idon.emergencmanagement.view.adapter.WorningRequestAdapter
import com.panuphong.smssender.helper.HandleClickListener
import com.stfalcon.frescoimageviewer.ImageViewer
import com.tt.workfinders.BaseClass.BaseActivity
import com.zine.ketotime.network.HttpMainConnect
import com.zine.ketotime.util.Constant
import kotlinx.android.synthetic.main.activity_desc_req_worning.*
import kotlinx.android.synthetic.main.activity_worning.*
import kotlinx.android.synthetic.main.toolbar_title.*
import org.parceler.Parcels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorningRequestDescActivity : BaseActivity(), HandleClickListener, OnMapReadyCallback {

    private lateinit var myRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    lateinit var user: UserFull
    lateinit var spf: SharedPreferences
    val sliderItemListURL: ArrayList<String> = ArrayList()

    lateinit var dataWorning: WorningDataItem
    lateinit var mGoogleMap: GoogleMap
    private lateinit var adapters: ImageShowAdapter

    override fun getContentView(): Int {
        return R.layout.activity_desc_req_worning
    }


    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)

        spf = getSharedPreferences(Constant._PREFERENCES_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        user = gson.fromJson(spf.getString(Constant._UDATA, "{}"), UserFull::class.java)
        toolbar.title = "รายละเอียด"
        setSupportActionBar(toolbar)
        showBackArrow()

        database = Firebase.database
        myRef = database.getReference().child("worning")

        dataWorning = Parcels.unwrap<WorningDataItem>(intent!!.getParcelableExtra("data"))

        adapters = ImageShowAdapter(this, this)

        cv_img.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            isNestedScrollingEnabled = false
            adapter = adapters

        }

        val mapFragment = supportFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)

        topicTV.text = "${dataWorning.w_topic}"
        descTV.text = "${dataWorning.w_desc}"

        nameTV.text = "ชื่อผู้แจ้ง : ${dataWorning.user!!.display_name!!}"
        telTV.text = "ติดต่อ : ${dataWorning.user!!.tel!!}"
        Glide.with(this).load("${Constant.BASE_URL}${dataWorning.user!!.avatar}").into(avatarImg)

        adapters.items = dataWorning.img
        adapters.notifyDataSetChanged()
        for (img in dataWorning?.img!!) {
            sliderItemListURL.add("${Constant.BASE_URL}${img.img}")

        }

    }

    override fun onResume() {
        super.onResume()

    }


    override fun onItemClick(view: View, position: Int, action: Int) {

        ImageViewer.Builder(this, sliderItemListURL)
            .setStartPosition(position)
            .show()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap!!

        setMarker(LatLng(dataWorning.lat!!, dataWorning.lng!!))


    }

    fun setMarker(locate: LatLng) {


        mGoogleMap!!.clear()
        val fire2MarkerOptions: MarkerOptions = MarkerOptions().position(locate).title("").icon(
            BitmapDescriptorFactory.fromBitmap(
                createCustomMarkerFire(this, R.drawable.bg_gradient, "Narender")
            )
        )

        mGoogleMap!!.addMarker(fire2MarkerOptions)
        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(locate, 17.0f));


    }


    fun createCustomMarkerFire(
        context: Context,
        @DrawableRes resource: Int,
        _name: String?
    ): Bitmap? {
        val marker =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.custom_marker_fire_layout,
                null
            )

        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        marker.layoutParams = ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT)
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            marker.measuredWidth,
            marker.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        marker.draw(canvas)
        return bitmap
    }

    var status = 0

    fun actionAccept(view: View) {
        status = 1
        HttpMainConnect()
            .getApiService()
            .updateStatus("${user.uid}", "${dataWorning.wid}", 1)
            .enqueue(CallUpdate())

    }


    fun actionReject(view: View) {
        status = 2
        HttpMainConnect()
            .getApiService()
            .updateStatus("${user.uid}", "${dataWorning.wid}", 2)
            .enqueue(CallUpdate())
    }

    inner class CallUpdate : Callback<ResponeDao> {

        init {
            showProgressDialog()
        }

        override fun onFailure(call: Call<ResponeDao>, t: Throwable) {
            hideDialog()
            showToast("err")
        }

        override fun onResponse(call: Call<ResponeDao>, response: Response<ResponeDao>) {

            hideDialog()

            if (response.isSuccessful) {
                showToast("บันทึกข้อมูลเรียบร้อย")
                if (status == 1)
                    myRef.child("${dataWorning.wid}").setValue("run")
                finish()
            } else {
                showToast("err")

            }
        }


    }


}