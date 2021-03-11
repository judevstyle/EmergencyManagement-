package com.idon.emergencmanagement.model

import android.net.Uri

class WorningData : ArrayList<WorningDataItem>()

data class WorningDataItem(
    var date_create: String?,
    var img: ArrayList<ImageWorningImg>?,
    var lat: Double?,
    var lng: Double?,
    var user: UserFull?,
    var w_desc: String?,
    var w_topic: String?,
    var wi: String?

)

data class Img(
    var img: String?,
    var seq: String?,
    var base64_img: String?,
    var uri_img: Uri?,
    var img_del: Int = 0


)

data class User(
    var username:String?,
    var password:String?,
    var avatar: String?,
    var display_name: String?,
    var fname: String?,
    var lname: String?,
    var uid: String?,
    var type: Int?
)