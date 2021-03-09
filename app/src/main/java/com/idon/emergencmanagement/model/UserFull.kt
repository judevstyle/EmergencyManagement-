package com.idon.emergencmanagement.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UserFull(
    var uid: String? = null,
    var display_name: String,
    var avatar: String,
    var gender:String?,
    var tel:String?
) : Parcelable