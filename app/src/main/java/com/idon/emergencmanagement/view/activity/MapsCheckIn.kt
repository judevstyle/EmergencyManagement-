package com.idon.emergencmanagement.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.model.CompanyData
import com.idon.emergencmanagement.model.MerkerStaff
import com.idon.emergencmanagement.model.User
import com.idon.emergencmanagement.model.UserFull
import com.tt.workfinders.BaseClass.BaseActivity
import com.zine.ketotime.network.HttpMainConnect
import com.zine.ketotime.util.Constant
import com.zine.ketotime.view.CustomTextView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main_maps.*
import kotlinx.android.synthetic.main.toolbar_title.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class MapsCheckIn : BaseActivity(), OnMapReadyCallback {

    private lateinit var myRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    lateinit var mGoogleMap: GoogleMap

    override fun getContentView(): Int {
        return R.layout.activity_checkin
    }

    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)


        toolbar.title = "ติดตามการอพยบ"
        setSupportActionBar(toolbar)

        showBackArrow()
        database = Firebase.database

        myRef = database.getReference().child("location")
        val mapFragment = supportFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)


    }


    var hashMap = HashMap<String, MerkerStaff>()

    override fun onMapReady(googlemap: GoogleMap?) {
        mGoogleMap = googlemap!!


        showToast("dkowl")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("ddd", "dldpw locaa")
//
//                HttpMainConnect()
//                    .getApiService()
//                    .getAccept()
//                    .enqueue(CallWorning())


            }
        })

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {


                Log.e("dlw", "dlw")
                mGoogleMap.clear()

                for (ds in snapshot.getChildren()) {
                    val dataUser = ds.child("data").value.toString()
                    val lat = ds.child("lat").getValue().toString().toDouble()
                    val lng = ds.child("lng").getValue().toString().toDouble()
                    val gson = Gson()

                    val user = gson.fromJson<UserFull>(dataUser, UserFull::class.java)

                    setSaveMarker(user, LatLng(lat, lng))

                    Log.e("dld", "${lat}")
                }

            }
        })


    }


    fun setSaveMarker(userFull: UserFull, locate: LatLng) {

        val userData = hashMap.get(userFull.uid)

        Log.e("lat", "${locate.latitude} , ${locate.longitude}")
        Log.e("old", "${userData?.lat} , ${userData?.lng}")

        if (hashMap.contains(userFull.uid)) {
            Log.e("lat", "${locate.latitude} , ${locate.longitude}")
            if (locate.latitude != userData?.lat && locate.longitude != userData?.lng) {

//                val m =userData?.maerker

//                val tmp = hashMap.get(userFull.uid!!)?.maerker

//                tmp!!.position = LatLng(0.0, 0.0)


                userData?.maerker?.remove()


                var opt = userData!!.maerkerOptions
                opt.position(locate)
               val m2 = mGoogleMap?.addMarker(userData?.maerkerOptions)
                userData
                userData?.maerker = m2
                hashMap.set(userFull.uid!!,userData!!)
                mGoogleMap!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        locate,
                        18.0f
                    )
                );



//               val m2 = mGoogleMap?.addMarker(userData?.maerkerOptions)
//                userData
//                userData?.maerker = m2
//                hashMap.set(userFull.uid!!,userData!!)
//                val marker =
//                    mGoogleMap!!.animateCamera(
//                        CameraUpdateFactory.newLatLngZoom(
//                            userData?.maerker?.position,
//                            18.0f
//                        )
//                    );


            }


        } else {

            GlobalScope.launch(Dispatchers.Main) {


//            var im:Bitmap? = null
                withContext(Dispatchers.IO) {
                    try {
                        val url = URL("${Constant.BASE_URL}${userFull.avatar}")
                        val connection =
                            url.openConnection() as HttpURLConnection
                        connection.doInput = true
                        connection.connect()
                        val input = connection.inputStream
                        val im = BitmapFactory.decodeStream(input)


                        withContext(Dispatchers.Main) {
//                                            markerImv.setImageBitmap(im)
                            showToast("ooooss")
//                        avatarImg.setImageBitmap(im)
                            val mrkerOptions: MarkerOptions =
                                MarkerOptions().position(locate).title("").icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        createCustomMarker(
                                            this@MapsCheckIn,
                                            im,
                                            "${userFull.display_name}"
                                        )
                                    )
                                )


                            val m = mGoogleMap.addMarker(mrkerOptions)
                            val da = MerkerStaff(
                                m.position.latitude,m.position.longitude,m, mrkerOptions

                            )

                            hashMap.put(userFull.uid!!, da)


                            mGoogleMap!!.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    locate,
                                    18.0f
                                )
                            );

                            //                        currentLocationMarker = mGoogleMap?.addMarker(mrkerOptions)

                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        null
                    }
                }


            }

        }


//
//        val markerOptions: MarkerOptions = MarkerOptions().position(
//            locate
//        ).title("").icon(
//            BitmapDescriptorFactory.fromBitmap(
//                createCustomMarker(this, userFull.avatar, "${userFull.display_name}")
//            )
//        )


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


//                markerImage.setImageDrawable(loadImageFromURL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQODPla_2AWvBB453c2bmZft9afyH0_-UWRXmj6BV9D7U0WJHu1&s","s"));

        nameTV.text = "${name}"
        markerImv.setImageBitmap(url)
//                Glide.with(this).load("${Constant.BASE_URL}${url}")
//                    .into(markerImv);


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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    fun actionCheckIn(view: View) {

        startActivity(Intent(this,CheckInListActivity::class.java)
            .putExtra("type",1)
        )

    }

    fun actionCheckNone(view: View) {
        startActivity(Intent(this,CheckInListActivity::class.java)
            .putExtra("type",3)
        )
    }


}