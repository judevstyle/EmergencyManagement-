package com.idon.emergencmanagement.view.fragment

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.helper.DisplayUtility
import com.shin.tmsuser.model.maps.Directions
import com.shin.tmsuser.model.maps.Route
import com.ssoft.candeliveryrider.helper.MapsFactory
import com.zine.ketotime.BaseClass.BaseFragment
import com.zine.ketotime.network.HttpMapsConnect
import kotlinx.android.synthetic.main.fragment_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragment : BaseFragment(), OnMapReadyCallback,GoogleMap.OnPolylineClickListener,
    GoogleMap.OnPolygonClickListener  {
    private var mRouteMarkerList = ArrayList<Marker>()
    private lateinit var mRoutePolyline: Polyline

    private var mGoogleMap: GoogleMap? = null
     var views: View? = null

    override fun provideFragmentView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        if (views != null) {
//            val parents = views?.parent as ViewGroup
//            parents?.removeView(views)
//        }
        try {
            views = LayoutInflater.from(context).inflate(R.layout.fragment_main, parent, false)
        } catch (e: InflateException) {
            /* map is already there, just return view as it is */
        }
        return views!!;

//        return LayoutInflater.from(context).inflate(R.layout.fragment_main, parent, false)

    }

    override fun onViewReady(view: View, savedInstanceState: Bundle?) {
        super.onViewReady(view, savedInstanceState)

//        Log.e("sw","${fragmentManager?.findFragmentById(R.id.map)}")
        val mapFragment =  activity?.supportFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
        pulsator.start();

        actionShow.setOnClickListener {
            actionShow()
        }
        actionHide.setOnClickListener {
            actionHide()
        }
        actionWorning.setOnClickListener {
            findNavController().navigate(R.id.action_to_mwoning)

        }

    }



    fun actionShow() {

        imgIM.visibility = View.VISIBLE
        actionHide.visibility = View.VISIBLE
        actionShow.visibility = View.GONE

    }



    fun actionHide(){
        imgIM.visibility = View.GONE
        actionHide.visibility = View.GONE
        actionShow.visibility = View.VISIBLE




    }




    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap
        val directionsCall = HttpMapsConnect().getApiService().getDirections("13.981472713187685, 100.53894242836355","13.981872776682234, 100.54104165309973", getString(R.string.google_maps_key))

        directionsCall.enqueue(object : Callback<Directions> {
            override fun onResponse(call: Call<Directions>, response: Response<Directions>) {



                val directions = response.body()!!

                Log.e("dwld","${directions}")
                if (directions.status.equals("OK")) {

                    val legs = directions.routes[0].legs[0]
                    val route = Route(getString(R.string.app_name)
                        ,getString(R.string.app_name), legs.start_location.lat, legs.start_location.lng, legs.end_location.lat, legs.end_location.lng, directions.routes[0].overview_polyline.points)
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
        val startMarkerOptions: MarkerOptions = MarkerOptions().position(LatLng(13.981472713187685, 100.53894242836355)).title(route.startName).icon(
            BitmapDescriptorFactory.fromBitmap(
            createCustomMarker(requireContext(), R.drawable.bg_gradient, "Narender")
        ))
        val endLatLng = LatLng(route.endLat!!, route.endLng!!)
        val endMarkerOptions: MarkerOptions = MarkerOptions().position(LatLng(13.981872776682234, 100.54104165309973)).title(route.endName).icon(
            BitmapDescriptorFactory.fromBitmap(
            createCustomMarkerShop(requireContext(), R.drawable.bg_gradient, "Narender")
        ))



        val fireMarkerOptions: MarkerOptions = MarkerOptions().position(LatLng(13.982201553484167, 100.539768506215)).title(route.endName).icon(
            BitmapDescriptorFactory.fromBitmap(
                createCustomMarkerFire(requireContext(), R.drawable.bg_gradient, "Narender")
            ))

        val fire2MarkerOptions: MarkerOptions = MarkerOptions().position(LatLng(13.981857160235363, 100.53938404802422)).title(route.endName).icon(
            BitmapDescriptorFactory.fromBitmap(
                createCustomMarkerFire(requireContext(), R.drawable.bg_gradient, "Narender")
            ))




//        val endMarkerOptions2: MarkerOptions = MarkerOptions().position(LatLng(13.980587491738156, 100.54517307748911)).title(route.endName).icon(BitmapDescriptorFactory.fromBitmap(
//            createCustomMarkerShop(this, R.drawable.bg_gradient, "Narender")
//        ))
//
//        val endMarkerOptions3: MarkerOptions = MarkerOptions().position(LatLng(13.97728329774291, 100.54549810378258)).title(route.endName).icon(BitmapDescriptorFactory.fromBitmap(
//            createCustomMarkerShop(this, R.drawable.bg_gradient, "Narender")
//        ))
//
//        val endMarkerOptions4: MarkerOptions = MarkerOptions().position(LatLng(13.985023107688031, 100.54937806252607)).title(route.endName).icon(BitmapDescriptorFactory.fromBitmap(
//            createCustomMarkerShop(this, R.drawable.bg_gradient, "Narender")
//        ))

        val startMarker = mGoogleMap!!.addMarker(startMarkerOptions)
        val endMarker = mGoogleMap!!.addMarker(endMarkerOptions)


        mGoogleMap!!.addMarker(fireMarkerOptions)
        mGoogleMap!!.addMarker(fire2MarkerOptions)

//        val endMarker2 = mGoogleMap!!.addMarker(endMarkerOptions2)
//        val endMarker3 = mGoogleMap!!.addMarker(endMarkerOptions3)
//        val endMarker4 = mGoogleMap!!.addMarker(endMarkerOptions4)

        mRouteMarkerList.add(startMarker)
        mRouteMarkerList.add(endMarker)



        mRouteMarkerList.add(startMarker)


//        mRouteMarkerList.add(endMarker2)
//        mRouteMarkerList.add(endMarker3)
//        mRouteMarkerList.add(endMarker4)

        val polylineOptions = drawRoute(requireContext())
//        val pointsList = PolyUtil.decode(route.overviewPolyline)
        val pointsList = PolyUtil.decode(route.overviewPolyline)
        for (point in pointsList) {
            polylineOptions.add(point)
        }
        polylineOptions.color(resources.getColor(R.color.colorPrimary));
        polylineOptions.width(10F);

        mRoutePolyline = mGoogleMap!!.addPolyline(polylineOptions)
//        CameraUpdateFactory.newCameraPosition(mRouteMarkerList)
        mGoogleMap!!.animateCamera(MapsFactory.autoZoomLevel(mRouteMarkerList))
        // Set listeners for click events.
//        mGoogleMap!!.setOnPolylineClickListener(this);
//        mGoogleMap!!.setOnPolygonClickListener(this);
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

    override fun onDestroyView() {
        super.onDestroyView()
        val mapFragment =  activity?.supportFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
        if (mapFragment != null) activity?.supportFragmentManager?.beginTransaction()?.remove(mapFragment)?.commit()
    }

}