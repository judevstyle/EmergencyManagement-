package com.idon.emergencmanagement.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.model.CompanyData
import com.idon.emergencmanagement.model.ImageWorningImg
import com.idon.emergencmanagement.model.ResponeDao
import com.idon.emergencmanagement.model.UserFull
import com.idon.emergencmanagement.util.FileUtil
import com.tt.workfinders.BaseClass.BaseActivity
import com.zine.ketotime.network.HttpMainConnect
import com.zine.ketotime.util.Constant
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_m_company.*
import kotlinx.android.synthetic.main.toolbar_title.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class CompanySettingActivity : BaseActivity(), OnMapReadyCallback {

    val REQUEST_CODE_LOCATION = 1003
    private var mGoogleMap: GoogleMap? = null
    var location: LatLng? = null
    lateinit var companyData: CompanyData

    lateinit var spf: SharedPreferences

    lateinit var userFull: UserFull
    override fun getContentView(): Int {
        spf = getSharedPreferences(Constant._PREFERENCES_NAME, Context.MODE_PRIVATE)
        return R.layout.activity_m_company
    }


    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)

        toolbar.title = "ข้อมูลผู้บริษัท"
        setSupportActionBar(toolbar)
        showBackArrow()

        val gson = Gson()
        userFull = gson.fromJson(spf.getString(Constant._UDATA, "{}"), UserFull::class.java)

        val mapFragment = supportFragmentManager?.findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment?.getMapAsync(this)


    }


    fun actionSelectMaps(view: View) {

        val intent = Intent(
            this,
            SelectMapsActivity::class.java
        )
        intent.putExtra("lat", location?.latitude)
        intent.putExtra("lng", location?.longitude)
        startActivityForResult(intent, REQUEST_CODE_LOCATION)


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        try {
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode === Activity.RESULT_OK) {


                if (requestCode === REQUEST_CODE_LOCATION) {
                    location =
                        LatLng(data!!.getDoubleExtra("lat", 0.0), data!!.getDoubleExtra("lng", 0.0))
                    setMarker(location!!)


                }

            }
        } catch (ex: Exception) {

        }

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap


        HttpMainConnect()
            .getApiService()
            .company("1001")
            .enqueue(CallComp())


    }

    fun createCustomMarkerSave(
        context: Context,
        @DrawableRes resource: Int,
        _name: String?
    ): Bitmap? {
        val marker =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.custom_marker_layout_shop,
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


    fun setMarker(locate: LatLng) {


        mGoogleMap!!.clear()
        val fire2MarkerOptions: MarkerOptions = MarkerOptions().position(locate).title("").icon(
            BitmapDescriptorFactory.fromBitmap(
                createCustomMarkerSave(this, R.drawable.bg_gradient, "")
            )
        )

        mGoogleMap!!.addMarker(fire2MarkerOptions)
        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(locate, 17.0f));


    }

    fun actionCommit(view: View) {


        companyData.comp_name = "${nameET.text}"

        if (location != null) {
            companyData.location_evacuate_lat = location!!.latitude
            companyData.location_evacuate_lng = location!!.longitude
        } else {

            showToast("เลือกพิกัด")
            return
        }

        if (distanceET.text.length < 1) {
            showToast("ระบุระยะของจุดรวมพล")
            return
        }
        companyData.distance = "${distanceET.text}".toInt()


        HttpMainConnect()
            .getApiService()
            .company(companyData)
            .enqueue(Callupdate())


    }


    inner class CallComp : Callback<CompanyData> {

        init {
            showProgressDialog()
        }

        override fun onFailure(call: Call<CompanyData>, t: Throwable) {
            showToast("err")
            hideDialog()
        }

        override fun onResponse(call: Call<CompanyData>, response: Response<CompanyData>) {
            hideDialog()


            if (response.isSuccessful) {

                response.body()?.let {

                    companyData = it
                    nameET.setText("${it.comp_name}")
                    location = LatLng(it.location_evacuate_lat!!, it.location_evacuate_lng!!)
                    setMarker(location!!)
                    distanceET.setText("${it.distance}")

                }

            } else {
                finish()

                showToast("err")

            }
        }
    }

    inner class Callupdate : Callback<ResponeDao> {

        init {
            showProgressDialog()
        }

        override fun onFailure(call: Call<ResponeDao>, t: Throwable) {
            hideDialog()

            Log.e("dd", "${t.message}")
        }

        override fun onResponse(call: Call<ResponeDao>, response: Response<ResponeDao>) {
            hideDialog()
            Log.e("dd", "${response.body()!!.msg}")

            if (response.isSuccessful) {
                finish()

                val database = Firebase.database

               val myRef = database.getReference().child("comp").child("1001")

                val data = HashMap<String, Any>()
                data.put("lat", location!!.latitude)
                data.put("lng", location!!.longitude)
                data.put("radiusInMeters", "${distanceET.text}".toInt())
                myRef.setValue(data)




            } else
                showToast("err")
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}