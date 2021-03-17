package com.idon.emergencmanagement.model

import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

data class MerkerStaff (

    var lat:Double,
    var lng:Double,
   var  maerker: Marker?,
    var  maerkerOptions: MarkerOptions

)


