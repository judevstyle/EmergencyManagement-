package com.idon.emergencmanagement.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class WorningData : ArrayList<WorningDataItem>()

@Parcelize
data class WorningDataItem(
    var date_create: String?,
    var img: ArrayList<ImageWorningImg>?,
    var lat: Double?,
    var lng: Double?,
    var user: UserFull?,
    var w_desc: String?,
    var w_topic: String?,
    var wid: String?,
    var status:Int? = 0

):Parcelable

data class Img(
    var img: String?,
    var seq: String?,
    var base64_img: String?,
    var uri_img: Uri?,
    var img_del: Int = 0
)

@Parcelize
data class User(
    var username:String?,
    var password:String?,
    var avatar: String?,
    var display_name: String?,
    var fname: String?,
    var lname: String?,
    var uid: String?,
    var type: Int?
):Parcelable