package com.idon.emergencmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class LocationViewmodel : ViewModel(){

     val dataLocation = MutableLiveData<LatLng>()


    fun updateLocation(location:LatLng?){

        dataLocation.value = location

    }


//    fun checkLocation

    fun df(){

        Log.e("dlldsss","${dataLocation.value?.longitude}")
    }


}