package com.idon.emergencmanagement

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.idon.emergencmanagement.service.LocationService
import com.idon.emergencmanagement.service.LocationUpdateService
import com.idon.emergencmanagement.service.MyLocationService
import com.idon.emergencmanagement.view.activity.TestActivity
import com.idon.emergencmanagement.viewmodel.LocationViewmodel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.tt.workfinders.BaseClass.BaseActivity
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity() {

    private val locationViewModel: LocationViewmodel by viewModel()

    var wakelock: PowerManager.WakeLock? = null
    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //    var receiver: LocationBroadcastReceiver? = null
    private lateinit var navController: NavController

    override fun getContentView(): Int {
        return R.layout.activity_main
    }

    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)

        navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        Dexter.withContext(this)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener, MultiplePermissionsListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    showToast("ok")

                    updateLocation()

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {

                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                    showToast("you mute accept location")

                }

                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                }


            }).check()
        startService(Intent(this, LocationUpdateService::class.java))
//        startLocService()

    }


    fun startLocService() {
//        val filter = IntentFilter("ACT_LOC")
//        registerReceiver(receiver, filter)
        val intent = Intent(this@MainActivity, LocationService::class.java)
        startService(intent)
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


    private fun builLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 100
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }





}