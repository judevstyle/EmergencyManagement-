package com.idon.emergencmanagement.service

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.idon.emergencmanagement.model.UserFull
import com.idon.emergencmanagement.view.activity.AlermActivity
import com.zine.ketotime.util.Constant


class LocationUpdateService : Service() {

    private lateinit var myRefLocation: DatabaseReference
    lateinit var user: UserFull
    lateinit var spf: SharedPreferences
    var wakelock: PowerManager.WakeLock? = null

    //region data
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 3000
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private val locationSettingsRequest: LocationSettingsRequest? = null
    private val TAG = "MyLocationService"
    private var mLocationManager: LocationManager? = null
    private val LOCATION_INTERVAL = 1000
    private val LOCATION_DISTANCE = 10f
    var context: Context? = null


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //onCreate
    override fun onCreate() {
        super.onCreate()


        spf = getSharedPreferences(Constant._PREFERENCES_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        user = gson.fromJson(spf.getString(Constant._UDATA, "{}"), UserFull::class.java)
//        while(true){
////            Timer("SettingUp").schedule(5000) {
////                Log.e("dldl","ssss")
////            }
//            Log.e("dldl","ssss")
//            initData()
//            Thread.sleep(1_000)
//
//        }
        initData()
    }


    private fun initData() {


//        initializeLocationManager()

//        try {
//            mLocationManager?.requestLocationUpdates(
//                LocationManager.PASSIVE_PROVIDER,
//                LOCATION_INTERVAL.toLong(),
//                LOCATION_DISTANCE,
//                mLocationListeners[0]
//            )
//        } catch (ex: SecurityException) {
//            Log.i(TAG, "fail to request location update, ignore", ex)
//        } catch (ex: IllegalArgumentException) {
//            Log.d(TAG, "network provider does not exist, " + ex.message)
//        }

        val database = Firebase.database
        val myRef = database.getReference().child("worning")
        myRefLocation = database.getReference().child("location")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val size = snapshot.getChildrenCount()

                val edit = spf.edit()



                Log.e("num", "${spf.getInt("count", 0)} ,${size.toInt()} ")

                if (spf.getInt("count", 0) != size.toInt() && size.toInt() != 0) {

                    val dialogIntent =
                        Intent(this@LocationUpdateService, AlermActivity::class.java)
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(dialogIntent)

                    edit.putInt("count", size.toInt()).commit()

                }
//                Log.e("dlpwld","dlwp ${size}")

            }
        })


        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.canonicalName)
        wakelock?.acquire()
        locationRequest = LocationRequest.create()
        locationRequest!!.apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            priority = PRIORITY_HIGH_ACCURACY
        }
//        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(baseContext)
    }


    private fun initializeLocationManager() {

        if (mLocationManager == null) {
            mLocationManager =
                applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        prepareForegroundNotification()

        if (spf.getBoolean("status", false))
            startLocationUpdates()
        else {
            mFusedLocationClient?.removeLocationUpdates(
                locationCallback
            )

        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(context,"disldwo",Toast.LENGTH_SHORT).show()
        Log.e("dlp","distroy")
        if (wakelock != null) {
            wakelock?.release();
            wakelock = null
        }

    }

    private fun startLocationUpdates() {
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
        mFusedLocationClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback, Looper.myLooper()
        )


    }


    //Location Callback
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            if (spf.getBoolean("status", false)) {
                val currentLocation: Location = locationResult.lastLocation



               val metter = distanceBetween(currentLocation.latitude,currentLocation.longitude,spf.getFloat("lat",0.0.toFloat()).toDouble(),spf.getFloat("lng",0.0.toFloat()).toDouble())


                if (metter > 5){

                    val edit = spf.edit()
                    edit.putFloat("lat",currentLocation.latitude.toFloat())
                    edit.putFloat("lng",currentLocation.longitude.toFloat())
                    edit.commit()

                    val data =  HashMap<String,Any>()
                    data.put("lat",currentLocation.latitude)
                    data.put("lng",currentLocation.longitude)
                    data.put("data",spf!!.getString(Constant._UDATA,"{}")!!)
                    myRefLocation.child(user.uid!!).setValue(data)

                }




            }else {
                mFusedLocationClient?.removeLocationUpdates(
                    this
                )

                myRefLocation.child(user.uid!!).removeValue()
            }






//
//            Log.e(
//                "Locations",
//                currentLocation.getLatitude().toString() + "," + currentLocation.getLongitude()
//            )
//
//
//            Toast.makeText(baseContext,
//                "${currentLocation.getLatitude()
//                    .toString() + "," + currentLocation.getLongitude()}",
//                Toast.LENGTH_SHORT
//            ).show()
            //Share/Publish Location
        }
    }

    var mLocationListeners = arrayOf(
        LocationListener(LocationManager.PASSIVE_PROVIDER)
    )


    class LocationListener(provider: String) :
        android.location.LocationListener {
        val TAG = "test"

        var mLastLocation: Location
        override fun onLocationChanged(location: Location) {
            Log.e(TAG, "onLocationChanged: $location")
            mLastLocation.set(location)
        }

        override fun onProviderDisabled(provider: String) {
            Log.e(TAG, "onProviderDisabled: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.e(TAG, "onProviderEnabled: $provider")
        }

        override fun onStatusChanged(
            provider: String,
            status: Int,
            extras: Bundle
        ) {
            Log.e(TAG, "onStatusChanged: $provider")
        }

        init {
            Log.e(TAG, "LocationListener $provider")
            mLastLocation = Location(provider)
        }
    }

    private fun distanceBetween(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = dist * 180.0 / Math.PI
        dist = dist * 60 * 1.1515 * 1000
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }



}