package com.panuphong.smssender.reciver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.SmsMessage
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.panuphong.smssender.model.PostData
import com.panuphong.smssender.model.ResponeSMS
import com.zine.ketotime.database.KetoDatabase
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL
import java.util.*
import kotlin.concurrent.schedule


class SMSReciver : BroadcastReceiver() {

    private lateinit var context: Context
    val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";


    override fun onReceive(context: Context, intent: Intent) {



        this.context = context
//        Log.e("ss","${dataList.si}")
        if (intent.action == SMS_RECEIVED) {
            val bundle = intent.extras
            if (bundle != null) {
                // get sms objects
                val pdus =
                    bundle["pdus"] as Array<Any>?
                if (pdus!!.size == 0) {
                    return
                }
                // large message might be broken into many
                val messages =
                    arrayOfNulls<SmsMessage>(pdus!!.size)
                val sb = StringBuilder()
                for (i in pdus!!.indices) {
                    messages[i] =
                        SmsMessage.createFromPdu(pdus!![i] as ByteArray)
                    sb.append(messages[i]!!.getMessageBody())
                }
                for (i in messages.indices) {
//                    Log.e("ddd",""+ messages[0].());
                }
                val tMgr =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_SMS
                    ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_PHONE_NUMBERS
                    ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    ) !== PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                val mPhoneNumber = tMgr.line1Number
                var sender = messages[0]!!.originatingAddress
                val message = sb.toString()

                sender = "KBank"


                val scope = CoroutineScope(Job() + Dispatchers.Main)

                scope.launch(Dispatchers.IO) {

                    val data = KetoDatabase.invoke(context).taskDao().getAllData(sender)

                    withContext(Dispatchers.Main) {

                        if (data.size > 0)
                            for (dat in data) {

                                if ((sender.toUpperCase()).equals(dat.sendername.toUpperCase())) {

                                    val postData = PostData(
                                        "${dat.smsdata.bank}"
                                        , "${dat.smsdata.bankcode}"
                                        , "${mPhoneNumber}"
                                        , "${message}"
                                        , "${millisToDate(Date().time)}"
                                        , "111"
                                    )

                                    val json = Gson().toJson(postData)
                                    commitAPI(dat.endpoint,json)

                                }


                            }
//                        Toast.makeText(
//                            context,
//                            "${data.size}",
//                            Toast.LENGTH_SHORT
//                        ).show()

                    }
                }


            }
        }
    }

    fun millisToDate(currentTime: Long): String? {
        val finalDate: String
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        val date = calendar.time
        finalDate = date.toString()
        return finalDate
    }


    fun commitAPI(urlL:kotlin.String, json:kotlin.String) {


        val scope = CoroutineScope(Job() + Dispatchers.IO)


        try {
            scope.launch {



                val client = OkHttpClient()
                val url = URL("${urlL}")
                var jsonString = json
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = jsonString.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()

                val responseBody = response.body!!.string()

                val data = Gson().fromJson<ResponeSMS>(responseBody, ResponeSMS::class.java)


                withContext(Dispatchers.Main) {
                    data.status?.let {

                        if (it.equals("1001")) {

                            Toast.makeText(
                                context,
                                "SS",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("ss","ss")

                        } else {
                            Timer().schedule(500) {
                                commitAPI(urlL,json)
                            }

                        }

                    } ?: kotlin.run {

                        Timer().schedule(500) {
                            commitAPI(urlL,json)
                        }
                    }


                }


            }
        }catch (ex:Exception){}



    }


}