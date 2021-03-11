package com.idon.emergencmanagement.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.widget.Toast
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.idon.emergencmanagement.model.UserFull
import com.zine.ketotime.util.Constant

class MyLocationService : BroadcastReceiver() {


    lateinit var spf: SharedPreferences

    companion object{

        val ACTION_PROCESS_UPDATE = "com.idon.emergencmanagement.service.UPDATE_LOCATION"

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        spf = context!!.getSharedPreferences(Constant._PREFERENCES_NAME, Context.MODE_PRIVATE)

        val database = Firebase.database
        var myRef = database.getReference().child("location")

        if (intent != null){
            val intenAction = intent.action!!

            if (intenAction.equals(ACTION_PROCESS_UPDATE)){
                val result = LocationResult.extractResult(intent)

                if (result != null){


                    val gson = Gson()
                   val user = gson.fromJson(spf.getString(Constant._UDATA, "{}"), UserFull::class.java)
                    myRef = database.getReference().child("location").child("${user.uid}")
                    val locaton = result.lastLocation
                    val location_str = "${locaton.latitude},${locaton.longitude}"
                    Toast.makeText(context!!,"${location_str}",Toast.LENGTH_SHORT).show()

// Read from the database


//                    val key = myRef.child("posts").push().key

                   val data =  HashMap<String,Double>()
                    data.put("lat",locaton.latitude)
                    data.put("lng",locaton.longitude)

                    myRef.setValue(data)

                    myRef.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {


                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
//                            val value = dataSnapshot.getValue<String>()
//                            Log.d(TAG, "Value is: $value")
                        }

//                        override fun onCancelled(error: DatabaseError) {
//                            // Failed to read value
//                            Log.w(TAG, "Failed to read value.", error.toException())
//                        }
                    })
                }

            }


        }



    }


}