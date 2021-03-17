package com.idon.emergencmanagement.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UserFull(
    var uid: String? = null,
    var display_name: String,
    var avatar: String,
    var type:String?,
    var fname: String?,
    var lname: String?,
    var tel:String?,
    var comp_id:String?,
    var type_of_user:Int?

) : Parcelable