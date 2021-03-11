package com.idon.emergencmanagement.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.github.angads25.toggle.interfaces.OnToggledListener
import com.github.angads25.toggle.model.ToggleableView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.helper.DisplayUtility
import com.idon.emergencmanagement.model.CompanyData
import com.idon.emergencmanagement.model.UserFull
import com.idon.emergencmanagement.model.WorningData
import com.idon.emergencmanagement.model.WorningDataItem
import com.idon.emergencmanagement.service.LocationUpdateService
import com.idon.emergencmanagement.service.MyLocationService
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.shin.tmsuser.model.maps.Directions
import com.shin.tmsuser.model.maps.Route
import com.tt.workfinders.BaseClass.BaseActivity
import com.zine.ketotime.network.HttpMainConnect
import com.zine.ketotime.network.HttpMapsConnect
import com.zine.ketotime.util.Constant
import kotlinx.android.synthetic.main.activity_main_maps.*
import kotlinx.android.synthetic.main.fragment_main.actionHide
import kotlinx.android.synthetic.main.fragment_main.actionShow
import kotlinx.android.synthetic.main.fragment_main.imgIM
import kotlinx.android.synthetic.main.fragment_main.pulsator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainMapsActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnPolylineClickListener, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnPolygonClickListener {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var spf: SharedPreferences
    private var mRouteMarkerList = ArrayList<Marker>()
    private lateinit var mRoutePolyline: Polyline
    private var mGoogleMap: GoogleMap? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    var location: LatLng? = null
    lateinit var user: UserFull
    lateinit var companyData: CompanyData
    lateinit var database: FirebaseDatabase
    lateinit var myRef: DatabaseReference
    override fun getContentView(): Int {

        return R.layout.activity_main_maps

    }


    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)
        spf = getSharedPreferences(Constant._PREFERENCES_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        user = gson.fromJson(spf.getString(Constant._UDATA, "{}"), UserFull::class.java)
        database = Firebase.database
        myRef = database.getReference().child("warning_list")
        val service = Intent(this@MainMapsActivity, LocationUpdateService::class.java)
        switchStatus.setOnToggledListener(object : OnToggledListener {


            override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {

                when (isOn) {
                    true -> {
                        startService(service)
                    }
                    else -> {
                        stopService(service)
                    }

                }

            }
        })



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Dexter.withContext(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) { /* ... */
                    getLastLocation()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */

                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) { /* ... */
                }
            }).check()

        val mapFragment = supportFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
        pulsator.start();

//        startService(Intent(this, LocationUpdateService::class.java))
    }


    override fun onResume() {
        super.onResume()


    }

    fun actionShow(view: View) {

        imgIM.visibility = View.VISIBLE
        actionHide.visibility = View.VISIBLE
        actionShow.visibility = View.GONE

    }

    fun actionHide(view: View) {
        imgIM.visibility = View.GONE
        actionHide.visibility = View.GONE
        actionShow.visibility = View.VISIBLE
    }


    fun actionWorning(view: View) {
        startActivity(Intent(this, NotifyWorningActivity::class.java))

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap
        mGoogleMap!!.setOnMarkerClickListener(this);

        HttpMainConnect().getApiService()
            .company(user.comp_id!!).enqueue(CallComp())



        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("ddd","dldpw locaa")

                HttpMainConnect()
                    .getApiService()
                    .getAccept()
                    .enqueue(CallWorning())


            }
        })


        val directionsCall = HttpMapsConnect().getApiService().getDirections(
            "13.981472713187685, 100.53894242836355",
            "13.981872776682234, 100.54104165309973",
            getString(R.string.google_maps_key)
        )
        directionsCall.enqueue(object : Callback<Directions> {
            override fun onResponse(call: Call<Directions>, response: Response<Directions>) {


                val directions = response.body()!!

                Log.e("dwld", "${directions}")
                if (directions.status.equals("OK")) {

                    val legs = directions.routes[0].legs[0]
                    val route = Route(
                        getString(R.string.app_name)
                        ,
                        getString(R.string.app_name),
                        legs.start_location.lat,
                        legs.start_location.lng,
                        legs.end_location.lat,
                        legs.end_location.lng,
                        directions.routes[0].overview_polyline.points
                    )
                    setMarkersAndRoute(route)
                } else {
                    showToast(directions.status)
                }
            }


            override fun onFailure(call: Call<Directions>, t: Throwable) {
                showToast(t.toString())
            }
        })

    }

    fun createCustomMarker(
        context: Context,
        @DrawableRes resource: Int,
        _name: String?
    ): Bitmap? {
        val marker =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.custom_marker_layout,
                null
            )
        //        markerImage.setImageDrawable(loadImageFromURL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQODPla_2AWvBB453c2bmZft9afyH0_-UWRXmj6BV9D7U0WJHu1&s","s"));

        //        Glide.with(this).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQODPla_2AWvBB453c2bmZft9afyH0_-UWRXmj6BV9D7U0WJHu1&s").into(markerImage);
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

    fun createCustomMarkerShop(
        context: Context,
        @DrawableRes resource: Int,
        _name: String?
    ): Bitmap? {
        val marker =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.custom_marker_layout_shop,
                null
            )
        //        markerImage.setImageDrawable(loadImageFromURL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQODPla_2AWvBB453c2bmZft9afyH0_-UWRXmj6BV9D7U0WJHu1&s","s"));

        //        Glide.with(this).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQODPla_2AWvBB453c2bmZft9afyH0_-UWRXmj6BV9D7U0WJHu1&s").into(markerImage);
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
        //        markerImage.setImageDrawable(loadImageFromURL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQODPla_2AWvBB453c2bmZft9afyH0_-UWRXmj6BV9D7U0WJHu1&s","s"));

        //        Glide.with(this).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQODPla_2AWvBB453c2bmZft9afyH0_-UWRXmj6BV9D7U0WJHu1&s").into(markerImage);
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

//100.539393

    fun setMarkersAndRoute(route: Route) {


        val startLatLng = LatLng(route.startLat!!, route.startLng!!)
        val startMarkerOptions: MarkerOptions =
            MarkerOptions().position(LatLng(13.981472713187685, 100.53894242836355))
                .title(route.startName).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createCustomMarker(this, R.drawable.bg_gradient, "Narender")
                    )
                )
        val endLatLng = LatLng(route.endLat!!, route.endLng!!)
        val endMarkerOptions: MarkerOptions =
            MarkerOptions().position(LatLng(13.981872776682234, 100.54104165309973))
                .title(route.endName).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createCustomMarkerShop(this, R.drawable.bg_gradient, "Narender")
                    )
                )


        val fireMarkerOptions: MarkerOptions =
            MarkerOptions().position(LatLng(13.982201553484167, 100.539768506215))
                .title(route.endName).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createCustomMarkerFire(this, R.drawable.bg_gradient, "Narender")
                    )
                )

        val fire2MarkerOptions: MarkerOptions =
            MarkerOptions().position(LatLng(13.981857160235363, 100.53938404802422))
                .title(route.endName).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createCustomMarkerFire(this, R.drawable.bg_gradient, "Narender")
                    )
                )


//        val startMarker = mGoogleMap!!.addMarker(startMarkerOptions)
//        val endMarker = mGoogleMap!!.addMarker(endMarkerOptions)


        mGoogleMap!!.addMarker(fireMarkerOptions)
        mGoogleMap!!.addMarker(fire2MarkerOptions)
//
//        mRouteMarkerList.add(startMarker)
//        mRouteMarkerList.add(endMarker)
////
//        mRouteMarkerList.add(startMarker)


//
//        val polylineOptions = drawRoute(this)
//        val pointsList = PolyUtil.decode(route.overviewPolyline)
//        for (point in pointsList) {
//            polylineOptions.add(point)
//        }
//        polylineOptions.color(resources.getColor(R.color.colorPrimary));
//        polylineOptions.width(10F);
//
//        mRoutePolyline = mGoogleMap!!.addPolyline(polylineOptions)
//        mGoogleMap!!.animateCamera(MapsFactory.autoZoomLevel(mRouteMarkerList))

    }

    fun drawRoute(context: Context): PolylineOptions {
        val polylineOptions = PolylineOptions()
        polylineOptions.width(DisplayUtility.px2dip(context, 72.toFloat()).toFloat())
        polylineOptions.geodesic(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            polylineOptions.color(context.resources.getColor(R.color.black, context.theme))
        } else {
            polylineOptions.color(context.resources.getColor(R.color.black))
        }
        return polylineOptions
    }

    override fun onPolylineClick(p0: Polyline?) {
    }

    override fun onPolygonClick(p0: Polygon?) {
    }

    private var currentLocationMarker: Marker? = null


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {

                    currentLocationMarker?.remove()

                    location = LatLng(task.result.latitude, task.result.longitude)
                    val mrkerOptions: MarkerOptions =
                        MarkerOptions().position(location!!).title("").icon(
                            BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(this, R.drawable.bg_gradient, "Narender")
                            )
                        )
                    currentLocationMarker = mGoogleMap?.addMarker(mrkerOptions)


                } else {
                    showToast("Err")
                }
            }
    }

    fun setMarker(locate: LatLng) {

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
                    setSaveMarker(it)

                }

            } else {
                showToast("err")

            }
        }
    }



    inner class CallWorning : Callback<WorningData> {

        init {
//            showProgressDialog()
        }

        override fun onFailure(call: Call<WorningData>, t: Throwable) {
            showToast("err")
            hideDialog()
        }

        override fun onResponse(call: Call<WorningData>, response: Response<WorningData>) {
            hideDialog()

            if (response.isSuccessful) {

                response.body()?.let {


                    for (mark in listMark){
                        mark.remove()
                    }
                    listMark.clear()


                    for (data in it){

                        val markerOptions: MarkerOptions = MarkerOptions().position(
                            LatLng(
                                data.lat!!,
                                data.lng!!
                            )
                        ).title("").icon(
                            BitmapDescriptorFactory.fromBitmap(
                                createCustomMarkerFire(this@MainMapsActivity, R.drawable.bg_gradient, "Narender")
                            )
                        )
                       val da = mGoogleMap?.addMarker(markerOptions)
                        da!!.tag = data
                        listMark.add(da)
                    }
//                    Log.e("ddd","${it.size}")


                }

            } else {
                showToast("err")

            }
        }

    }



    val listMark = ArrayList<Marker>()

    private var compLocationMarker: Marker? = null


    fun setSaveMarker(companyData: CompanyData) {

        compLocationMarker?.remove()
        val markerOptions: MarkerOptions = MarkerOptions().position(
            LatLng(
                companyData.location_evacuate_lat!!,
                companyData.location_evacuate_lng!!
            )
        ).title("").icon(
            BitmapDescriptorFactory.fromBitmap(
                createCustomMarkerShop(this, R.drawable.bg_gradient, "Narender")
            )
        )
        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(companyData.location_company_lat!!,companyData.location_company_lng!!), 18.0f));

        compLocationMarker = mGoogleMap?.addMarker(markerOptions)

    }
    private fun updateLocation() {

        builLocationRequest()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent())

    }

    private fun getPendingIntent(): PendingIntent? {
        val intents = Intent(this, MyLocationService::class.java)
        intents.setAction(MyLocationService.ACTION_PROCESS_UPDATE)
        return PendingIntent.getBroadcast(this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    lateinit var locationRequest: LocationRequest

    private fun builLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 100
        }

    }

    override fun onMarkerClick(marker: Marker?): Boolean {


        try {
            val data = marker?.tag as WorningDataItem
            Log.e("d;w","${data.w_topic}")

            nameTV.text = "${data.user!!.display_name}"
            topicTV.text = "${data.w_topic}"
            descTV.text = "${data.w_desc}"
            Glide.with(this).load("${Constant.BASE_URL}${data.user!!.avatar}").into(avatarImg)
            boxWorn.visibility = View.VISIBLE
        }catch (ex:TypeCastException){}

        return true

    }


}