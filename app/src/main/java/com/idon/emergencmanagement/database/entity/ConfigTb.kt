package com.shin.tmsuser.database.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.panuphong.smssender.model.SmsData
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class ConfigTb(

    var task_name: String,
    var task_status: Boolean = true,
    var endpoint: String = "",
    var sendername: String,
    @Embedded
    var smsdata: SmsData

    ) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var task_id:Int = 0



}
