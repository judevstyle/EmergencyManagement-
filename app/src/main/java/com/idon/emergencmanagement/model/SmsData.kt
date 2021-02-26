package com.panuphong.smssender.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//:{
//    "phonenumber":"[PHONE_NUMBER]",
//    "smsid":"[SMS_ID]",
//    "sms":"[SMS_CONTENT]",
//    "smsdate":"[SMS_TIME]",
//    "bank":"ae4958789c1b93786d3eb283b12de4df7703c86f",
//    "bankcode":"kbank"
//}
@Parcelize
data class SmsData(
    var phonenumber:String,
    var smsid:String,
    var sms:String,
    var smsdate:String,
    var bank:String,
    var bankcode:String

):Parcelable