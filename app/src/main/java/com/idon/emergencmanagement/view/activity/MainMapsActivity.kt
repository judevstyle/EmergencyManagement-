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
import android.graphics.BitmapFactory
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
import com.google.maps.android.PolyUtil
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.helper.DisplayUtility
import com.idon.emergencmanagement.model.*
import com.idon.emergencmanagement.service.LocationUpdateService
import com.idon.emergencmanagement.service.MyLocationService
import com.idon.emergencmanagement.service.UpdateWorningService
import com.idon.emergencmanagement.view.adapter.SliderAdapter
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.panuphong.smssender.helper.HandleClickListener
import com.shin.tmsuser.model.maps.Directions
import com.shin.tmsuser.model.maps.Route
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import com.ssoft.candeliveryrider.helper.MapsFactory
import com.stfalcon.frescoimageviewer.ImageViewer
import com.tt.workfinders.BaseClass.BaseActivity
import com.zine.ketotime.network.HttpMainConnect
import com.zine.ketotime.network.HttpMapsConnect
import com.zine.ketotime.util.Constant
import com.zine.ketotime.view.CustomTextView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main_maps.*
import kotlinx.android.synthetic.main.fragment_main.actionHide
import kotlinx.android.synthetic.main.fragment_main.actionShow
import kotlinx.android.synthetic.main.fragment_main.pulsator
import kotlinx.android.synthetic.main.toolbar_title.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainMapsActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnPolylineClickListener,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnPolygonClickListener, HandleClickListener {



    private var cicleMarker: Circle? = null
    var data: WorningDataItem? = null
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var adapter: SliderAdapter
    lateinit var spf: SharedPreferences
    private var mRouteMarkerList = ArrayList<Marker>()
    private  var mRoutePolyline: Polyline? = null
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

        toolbar.title = "Home"
        setSupportActionBar(toolbar)
        adapter = SliderAdapter(this, this)
        imageSlider.setSliderAdapter(adapter)
        spf = getSharedPreferences(Constant._PREFERENCES_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        user = gson.fromJson(spf.getString(Constant._UDATA, "{}"), UserFull::class.java)
        database = Firebase.database

        myRef = database.getReference().child("worning")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val edit = spf.edit()
                val size = snapshot.getChildrenCount()
                edit.putInt("count", size.toInt()).commit()

            }
        })

        myRef = database.getReference().child("warning_list")
        val service = Intent(this@MainMapsActivity, UpdateWorningService::class.java)
        startService(service)


        val serviceTracking = Intent(this@MainMapsActivity, LocationUpdateService::class.java)

        Log.e("sss Status", "${spf.getBoolean("status", false)}")
        switchStatus.isOn = spf.getBoolean("status", false)
        val edit = spf.edit()

        when (spf.getBoolean("status", false)) {
            true -> {
//                edit.putBoolean("status", true).commit()
                startService(serviceTracking)
                showToast("ppppww")
            }
            else -> {
//                edit.putBoolean("status", false)
//                edit.putFloat("lat", 0.0.toFloat())
//                edit.putFloat("lng", 0.0.toFloat())
//                edit.commit()
                stopService(serviceTracking)
            }

        }

        switchStatus.setOnToggledListener(object : OnToggledListener {


            override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {

                when (isOn) {
                    true -> {

                        HttpMainConnect()
                            .getApiService()
                            .updateCheckin(
                                "${user.uid}",0
                            ).enqueue(CallCheckIn())


                    }
                    else -> {
                        mRoutePolyline?.remove()
                        edit.putBoolean("status", false)
                        edit.putFloat("lat", 0.0.toFloat())
                        edit.putFloat("lng", 0.0.toFloat())
                        edit.commit()
                        stopService(serviceTracking)
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

    val sliderItemListURL: ArrayList<String> = ArrayList()
    val sliderItemList: ArrayList<SliderItem> = ArrayList()

    fun actionShow(view: View) {

        imageSlider.visibility = View.VISIBLE
        actionHide.visibility = View.VISIBLE
        actionShow.visibility = View.GONE


//        imageSlider.setIndicatorAnimation(IndicatorAnimations.THIN_WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT)
//        imageSlider.setIndicatorSelectedColor(Color.WHITE)
//        imageSlider.setIndicatorUnselectedColor(Color.GRAY)
        imageSlider.setScrollTimeInSec(3)

        //dummy data

        sliderItemListURL.clear()
        sliderItemList.clear()

        for (values in data!!.img!!) {
            val sliderItem = SliderItem()
            sliderItem.setDescription("Slider Item ")
            sliderItemListURL.add("${Constant.BASE_URL}${values.img}")
            sliderItem.setImageUrl("${Constant.BASE_URL}${values.img}")
            sliderItemList.add(sliderItem)


        }
        adapter.addItem(sliderItemList)
        adapter.notifyDataSetChanged()

    }

    fun actionHide(view: View) {
        imageSlider.visibility = View.GONE
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
                Log.e("ddd", "dldpw locaa")

                HttpMainConnect()
                    .getApiService()
                    .getAccept()
                    .enqueue(CallWorning())


            }
        })

        val comp = database.getReference().child("comp")
        comp.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {


//                mGoogleMap.clear()

                for (ds in snapshot.getChildren()) {
                    val radiusInMeters = ds.child("radiusInMeters").value.toString().toInt()
                    val lat = ds.child("lat").getValue().toString().toDouble()
                    val lng = ds.child("lng").getValue().toString().toDouble()

                    val companyData = CompanyData(
                        null, null, null, null, lat, lng
                        , radiusInMeters
                    )
                    setSaveMarker(companyData)
                    if (spf.getBoolean("status", false))
                        initLineMaps()

//                    Log.e("dld", "${lat}")
                }

            }
        })


    }

    fun createCustomMarker(
        context: Context,
        url: Bitmap, name: String
    ): Bitmap? {
        val marker =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.custom_marker_layout,
                null
            )

        val markerImv = marker.findViewById<CircleImageView>(R.id.markerIM)
        val nameTV = marker.findViewById<CustomTextView>(R.id.nameTV)
//        try {
//            val url = URL("${Constant.BASE_URL}${url}")
//            val image =
//                BitmapFactory.decodeStream(url.openStream())
//            markerImv.setImageBitmap(image)
//        } catch (e: IOException) {
//            println(e)
//        }


//                markerImage.setImageDrawable(loadImageFromURL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQODPla_2AWvBB453c2bmZft9afyH0_-UWRXmj6BV9D7U0WJHu1&s","s"));
        Log.e("ok", "kk")
//        Log.e("dds","${Constant.BASE_URL}${url}")
        nameTV.text = "ฉัน"

        markerImv.setImageBitmap(url)

//        Glide.with(this).load("https://www.flashexpress.co.th/img/logo.png")
//            .listener(object : RequestListener<Drawable> {
//                override fun onLoadFailed(
//                    e: GlideException?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    isFirstResource: Boolean
//                ): Boolean {
//
//                    Log.e("err","${e?.message}")
//                    return  false
//
//                }
//
//                override fun onResourceReady(
//                    resource: Drawable?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    dataSource: DataSource?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    Log.e("ok","kl")
//                    return  true
//                }
//
//            })
//            .into(markerImv);

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


    fun getBitmapFromURL(src: String): Bitmap? {
        return try {
            Log.e("src", src!!)
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            val myBitmap =
                BitmapFactory.decodeStream(input)
            Log.e("Bitmap", "returned")
            myBitmap
        } catch (e: IOException) {
            e.printStackTrace()
//            Log.e("Exception", e.message?)
            null
        }
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
//        val startMarkerOptions: MarkerOptions =
//            MarkerOptions().position(LatLng(13.981472713187685, 100.53894242836355))
//                .title(route.startName).icon(
//                    BitmapDescriptorFactory.fromBitmap(
//                        createCustomMarker(this, "", "Narender")
//                    )
//                )
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


//            mGoogleMap!!.addMarker(fireMarkerOptions)
//            mGoogleMap!!.addMarker(fire2MarkerOptions)
        Log.e("ddd", "${currentLocationMarker} , ${compLocationMarker}")

        if (currentLocationMarker != null && compLocationMarker != null) {
            mRouteMarkerList = ArrayList()
            mRouteMarkerList.add(currentLocationMarker!!)
            mRouteMarkerList.add(compLocationMarker!!)
////
//        mRouteMarkerList.add(startMarker)


//
            val polylineOptions = drawRoute(this)
            val pointsList = PolyUtil.decode(route.overviewPolyline)
            for (point in pointsList) {
                polylineOptions.add(point)
            }
            polylineOptions.color(resources.getColor(R.color.colorPrimary));
            polylineOptions.width(10F);

            mRoutePolyline = mGoogleMap!!.addPolyline(polylineOptions)
            mGoogleMap!!.animateCamera(MapsFactory.autoZoomLevel(mRouteMarkerList))
        }
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


                    Log.e("dldl", "dl;;;;")
                    //  currentLocationMarker?.remove()

                    showToast("ldl")

                    currentLocationMarker?.let {


//                        position.latitude = 0.0

                    } ?: kotlin.run {
                        location = LatLng(task.result.latitude, task.result.longitude)

                        GlobalScope.launch(Dispatchers.Main) {


//            var im:Bitmap? = null
                            withContext(Dispatchers.IO) {
                                try {
                                    val url = URL("${Constant.BASE_URL}${user.avatar}")
                                    val connection =
                                        url.openConnection() as HttpURLConnection
                                    connection.doInput = true
                                    connection.connect()
                                    val input = connection.inputStream
                                    val im = BitmapFactory.decodeStream(input)


                                    withContext(Dispatchers.Main) {
//                                            markerImv.setImageBitmap(im)
                                        showToast("ooooss")
                                        avatarImg.setImageBitmap(im)
                                        val mrkerOptions: MarkerOptions =
                                            MarkerOptions().position(location!!).title("").icon(
                                                BitmapDescriptorFactory.fromBitmap(
                                                    createCustomMarker(
                                                        this@MainMapsActivity,
                                                        im,
                                                        "${user.display_name}"
                                                    )
                                                )
                                            )
                                        currentLocationMarker = mGoogleMap?.addMarker(mrkerOptions)

                                        if (spf.getBoolean("status", false))
                                            initLineMaps()


                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    null
                                }
                            }


                        }


//                        Handler().postDelayed({
//                            //doSomethingHere()
//                            currentLocationMarker?.position = LatLng(0.0,0.0)
//
//                        }, 5000)
//                        Timer("SettingUp", false).schedule(5000) {
//
//
//
//                        }


                    }

//                    currentLocationMarker.g

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


                }

            } else {
                showToast("err")

            }
        }
    }


    fun initLineMaps() {

        if (mRoutePolyline != null)
            mRoutePolyline?.remove()

        val directionsCall = HttpMapsConnect().getApiService().getDirections(
            "${currentLocationMarker?.position?.latitude},${currentLocationMarker?.position?.longitude}",
            "${compLocationMarker?.position?.latitude},${compLocationMarker?.position?.longitude}",
            getString(R.string.google_maps_key)
        )
        directionsCall.enqueue(object : Callback<Directions> {
            override fun onResponse(call: Call<Directions>, response: Response<Directions>) {


                val directions = response.body()!!

                Log.e("dwld", "${directions}")
                if (directions.status.equals("OK")) {

                    Log.e("dsaq", "ok")
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
                    Log.e("ddd1", "${directions.status}")

//                    showToast(directions.status)
                }
            }


            override fun onFailure(call: Call<Directions>, t: Throwable) {
                Log.e("ddd", "${t.message}")
                showToast(t.toString())
            }
        })

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


                    for (mark in listMark) {
                        mark.remove()
                    }
                    listMark.clear()


                    for (data in it) {

                        val markerOptions: MarkerOptions = MarkerOptions().position(
                            LatLng(
                                data.lat!!,
                                data.lng!!
                            )
                        ).title("").icon(
                            BitmapDescriptorFactory.fromBitmap(
                                createCustomMarkerFire(
                                    this@MainMapsActivity,
                                    R.drawable.bg_gradient,
                                    "Narender"
                                )
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

        drawMarkerWithCircle(
            LatLng(
                companyData.location_evacuate_lat!!,
                companyData.location_evacuate_lng!!
            ), companyData.distance!!
        )

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
        mGoogleMap!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    companyData.location_evacuate_lat!!,
                    companyData.location_evacuate_lng!!
                ), 18.0f
            )
        );

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
            data = marker?.tag as WorningDataItem
            nameTV.text = "${data?.user!!.display_name}"
            topicTV.text = "${data?.w_topic}"
            descTV.text = "${data?.w_desc}"
            Glide.with(this).load("${Constant.BASE_URL}${data?.user!!.avatar}").into(avatarImg)
            boxWorn.visibility = View.VISIBLE
//            imageSlider.visibility = View.GONE
            actionHide(actionHide)

        } catch (ex: TypeCastException) {
        }

        return true

    }

    override fun onItemClick(view: View, position: Int, action: Int) {

        ImageViewer.Builder(this, sliderItemListURL)
            .setStartPosition(position)
            .show()


    }


    private fun drawMarkerWithCircle(position: LatLng, distance: Int) {

        cicleMarker?.remove()
        val radiusInMeters = 50.0
        val strokeColor = -0x10000 //red outline
        val shadeColor = 0x44ff0000 //opaque red fill
        val circleOptions: CircleOptions =
            CircleOptions().center(position).radius(distance.toDouble())
                .fillColor(resources.getColor(R.color.colorGreeTran))
                .strokeColor(resources.getColor(R.color.colorGreen)).strokeWidth(8.toFloat())
        cicleMarker = mGoogleMap?.addCircle(circleOptions)
//        val markerOptions = MarkerOptions().position(position)
//        mMarker = mGoogleMap.addMarker(markerOptions)
    }



    inner class CallCheckIn : Callback<ResponeDao>{


        override fun onFailure(call: Call<ResponeDao>, t: Throwable) {
            showToast("เกิดข้อผิดพลาด")


        }

        override fun onResponse(call: Call<ResponeDao>, response: Response<ResponeDao>) {


            if (response.isSuccessful){

                response.body()?.let {
                    val edit = spf.edit()

                    if (it.status == 1){
                        edit.putBoolean("status", true).commit()
                        val serviceTracking = Intent(this@MainMapsActivity, LocationUpdateService::class.java)
                        startService(serviceTracking)
                        initLineMaps()
                        showToast("บันทึกข้อมูลเรียบร้อย")

                    }else{
                        switchStatus.isOn = true
                        showToast("${it.msg}")


                    }

                }


            }else
                showToast("เกิดข้อผิดพลาด")

        }


    }

}