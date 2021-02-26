package com.ssoft.candeliveryrider.helper

import android.content.Context
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.idon.emergencmanagement.R
import com.shin.tmsuser.model.maps.Route
import com.shin.tmsuser.model.maps.Spot


class MapsController(context: Context, googleMap: GoogleMap) {

    private val mContext: Context = context
    private val mGoogleMap: GoogleMap = googleMap

    private val mTimeSquare = LatLng(40.758895, -73.985131)

    private var mSpotMarkerList = ArrayList<Marker>()

    private var mRouteMarkerList = ArrayList<Marker>()
    private lateinit var mRoutePolyline: Polyline

    fun setCustomMarker() {
        val blackMarkerIcon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_shop)
        val markerOptions: MarkerOptions = MarkerOptions().position(mTimeSquare).title(mContext.getString(R.string.app_name)).snippet(mContext.getString(R.string.app_name)).icon(blackMarkerIcon)
        mGoogleMap.addMarker(markerOptions)
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mTimeSquare))
    }

    fun animateZoomInCamera() {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mTimeSquare, 15f))
    }

    fun animateZoomOutCamera() {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mTimeSquare, 10f))
    }

    fun setMarkersAndZoom(spotList: List<Spot>) {
        val spotBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_shop)

        for (spot in spotList) {
            val name = spot.name
            val latitude = spot.lat
            val longitude = spot.lng
            val latLng = LatLng(latitude!!, longitude!!)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng).title(name).icon(spotBitmap)

            val marker = mGoogleMap.addMarker(markerOptions)
            mSpotMarkerList.add(marker)
        }

        mGoogleMap.animateCamera(MapsFactory.autoZoomLevel(mSpotMarkerList))
    }

    fun clearMarkers() {
        for (marker in mSpotMarkerList) {
            marker.remove()
        }
        mSpotMarkerList.clear()
    }

    fun setMarkersAndRoute(route: Route) {

//        13.981176, 100.536625

        val startLatLng = LatLng(13.981176, 100.536625)
        val startMarkerOptions: MarkerOptions = MarkerOptions().position(startLatLng).title(route.startName).icon(BitmapDescriptorFactory.fromBitmap(MapsFactory.drawMarker(mContext, "S")))
        val endLatLng = LatLng(13.979594, 100.545648)
        val endMarkerOptions: MarkerOptions = MarkerOptions().position(endLatLng).title(route.endName).icon(BitmapDescriptorFactory.fromBitmap(MapsFactory.drawMarker(mContext, "E")))
        val startMarker = mGoogleMap.addMarker(startMarkerOptions)
        val endMarker = mGoogleMap.addMarker(endMarkerOptions)
        mRouteMarkerList.add(startMarker)
        mRouteMarkerList.add(endMarker)

        val polylineOptions = MapsFactory.drawRoute(mContext)
        val pointsList = PolyUtil.decode(route.overviewPolyline)
        for (point in pointsList) {
            polylineOptions.add(point)
        }

        mRoutePolyline = mGoogleMap.addPolyline(polylineOptions)
        Toast.makeText(mContext,"ded",Toast.LENGTH_LONG).show()
        mGoogleMap.animateCamera(MapsFactory.autoZoomLevel(mRouteMarkerList))
    }

    fun clearMarkersAndRoute() {
        for (marker in mRouteMarkerList) {
            marker.remove()
        }
        mRouteMarkerList.clear()

        if (::mRoutePolyline.isInitialized) {
            mRoutePolyline.remove()
        }
    }
}