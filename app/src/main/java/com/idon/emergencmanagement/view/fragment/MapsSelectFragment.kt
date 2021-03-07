package com.idon.emergencmanagement.view.fragment

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
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.view.adapter.ImageAdapter
import com.idon.emergencmanagement.viewmodel.LocationViewmodel
import com.zine.ketotime.BaseClass.BaseFragment
import kotlinx.android.synthetic.main.fragment_select_maps.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class MapsSelectFragment : BaseFragment(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private lateinit var adapters: ImageAdapter
    private val shearViewModel:LocationViewmodel by sharedViewModel()

    private var location:LatLng? = null

    override fun provideFragmentView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return LayoutInflater.from(context).inflate(R.layout.fragment_select_maps, parent, false)

    }



    override fun onViewReady(view: View, savedInstanceState: Bundle?) {
        super.onViewReady(view, savedInstanceState)

        val mapFragment =  activity?.supportFragmentManager?.findFragmentById(R.id.map_select) as SupportMapFragment
        mapFragment?.getMapAsync(this)



        actionSelector.setOnClickListener {
            shearViewModel.updateLocation(location)
            findNavController().popBackStack(R.id.mapsSelectFragment, true);

        }


    }


    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap

        val fire2MarkerOptions: MarkerOptions = MarkerOptions().position(LatLng(13.981857160235363, 100.53938404802422)).title("").icon(
            BitmapDescriptorFactory.fromBitmap(
                createCustomMarkerFire(requireContext(), R.drawable.bg_gradient, "Narender")
            ))


        mGoogleMap!!.setOnMapClickListener(GoogleMap.OnMapClickListener {
            mGoogleMap!!.clear()


            location = it
            val fire2MarkerOptions: MarkerOptions = MarkerOptions().position(it).title("").icon(
                BitmapDescriptorFactory.fromBitmap(
                    createCustomMarkerFire(requireContext(), R.drawable.bg_gradient, "Narender")
                ))
            mGoogleMap!!.addMarker(fire2MarkerOptions)
//        mGoogleMap!!.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );



        })

        location = LatLng(13.981857160235363, 100.53938404802422)

        mGoogleMap!!.addMarker(fire2MarkerOptions)
//        mGoogleMap!!.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(13.981857160235363, 100.53938404802422), 17.0f));




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

    override fun onDestroyView() {
        super.onDestroyView()
        val mapFragment =  activity?.supportFragmentManager?.findFragmentById(R.id.map_select) as SupportMapFragment
        if (mapFragment != null) activity?.supportFragmentManager?.beginTransaction()?.remove(mapFragment)?.commit()
    }




}