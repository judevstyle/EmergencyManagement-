package com.idon.emergencmanagement.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.view.adapter.ImageAdapter
import com.idon.emergencmanagement.viewmodel.LocationViewmodel
import com.panuphong.smssender.helper.HandleClickListener
import com.zine.ketotime.BaseClass.BaseFragment
import kotlinx.android.synthetic.main.fragment_notify_worning.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class NotifyWorningFragment : BaseFragment(),HandleClickListener, OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private lateinit var adapters: ImageAdapter
    private val locationViewModel: LocationViewmodel by sharedViewModel()
    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    var ds = ""

    override fun provideFragmentView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return LayoutInflater.from(context).inflate(R.layout.fragment_notify_worning, parent, false)

    }


    override fun onViewReady(view: View, savedInstanceState: Bundle?) {
        super.onViewReady(view, savedInstanceState)


        adapters = ImageAdapter(requireContext(), this)
        cv_evidence.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            isNestedScrollingEnabled = false
            adapter = adapters
        }

        val mapFragment =  activity?.supportFragmentManager?.findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment?.getMapAsync(this)
        locationViewModel.df()

        actionSelectMaps.setOnClickListener {

            findNavController().navigate(R.id.action_selectMaps)

        }

        Log.e("ooop","ss ${ds}")

    }


    public override fun onStart() {
        super.onStart()

//        if (!checkPermissions()) {
//            requestPermissions()
//        } else {
//            getLastLocation()
//        }
    }


    fun Onserver(){
        Log.e("dl","llsllหห")

        locationViewModel.dataLocation.observe(viewLifecycleOwner, Observer {

            it?.let {

                val fire2MarkerOptions: MarkerOptions = MarkerOptions().position(it).title("").icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createCustomMarkerFire(requireContext(), R.drawable.bg_gradient, "Narender")
                    ))

                mGoogleMap!!.addMarker(fire2MarkerOptions)
                mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 17.0f));



            }

        })


    }

    override fun onItemClick(view: View, position: Int, action: Int) {


    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap
        Onserver()

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

    override fun onDestroyView() {
        super.onDestroyView()
        val mapFragment =  activity?.supportFragmentManager?.findFragmentById(R.id.maps) as SupportMapFragment
        if (mapFragment != null) activity?.supportFragmentManager?.beginTransaction()?.remove(mapFragment)?.commit()
        ds = "rrr"
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful && task.result != null) {

                } else {
//                    Log.w(TAG, "getLastLocation:exception", task.exception)
//                    showMessage(getString(R.string.no_location_detected))
                }
            }
    }

}