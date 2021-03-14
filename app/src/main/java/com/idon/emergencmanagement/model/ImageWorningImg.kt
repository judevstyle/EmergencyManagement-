package com.idon.emergencmanagement.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageWorningImg(
    var seq: Int? = 0,
    var img: String?,
    var base64_img: String?,
    var uri_img: Uri?,
    var img_del: Int = 0

) : Parcelable

