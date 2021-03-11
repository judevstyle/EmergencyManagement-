package com.idon.emergencmanagement.service

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
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
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.idon.emergencmanagement.view.activity.AlermActivity


class LocationUpdateService : Service() {

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
    var context:Context? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //onCreate
    override fun onCreate() {
        super.onCreate()

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


        initializeLocationManager()

        try {
            mLocationManager?.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE,
                mLocationListeners[0]
            )
        } catch (ex: SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }

        val database = Firebase.database
        val myRef = database.getReference()


        myRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {


                val dialogIntent =
                    Intent(this@LocationUpdateService, AlermActivity::class.java)
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(dialogIntent)
               // Toast.makeText(baseContext,"dkwodlll",Toast.LENGTH_SHORT).show()



                Log.e("dlpwld","dlwp")

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
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if ( wakelock != null) {
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
            val currentLocation: Location = locationResult.lastLocation
            Log.e(
                "Locations",
                currentLocation.getLatitude().toString() + "," + currentLocation.getLongitude()
            )


            Toast.makeText(baseContext,"${currentLocation.getLatitude().toString() + "," + currentLocation.getLongitude()}",Toast.LENGTH_SHORT).show()
            //Share/Publish Location
        }
    }

    var mLocationListeners = arrayOf(
        LocationListener(LocationManager.PASSIVE_PROVIDER)
    )


     class LocationListener(provider: String) :
        android.location.LocationListener {
        val TAG ="test"

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


}