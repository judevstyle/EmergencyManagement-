package com.idon.emergencmanagement.view.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.model.ResponeUserDao
import com.idon.emergencmanagement.model.UserFull
import com.tt.workfinders.BaseClass.BaseActivity
import com.zine.ketotime.network.HttpMainConnect
import com.zine.ketotime.util.Constant
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.passET
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity(){


    lateinit var spf: SharedPreferences

    override fun getContentView(): Int {
        spf = getSharedPreferences(Constant._PREFERENCES_NAME, Context.MODE_PRIVATE)

        if (!spf.getString(Constant._UDATA,"").equals("")){

            var intent:Intent
            val gson = Gson()
            val dataclass = gson.fromJson<UserFull>(spf.getString(Constant._UDATA,""),UserFull::class.java)
            if (dataclass.type_of_user!! == 1)
                intent = Intent(this@LoginActivity, MainMapsActivity::class.java)

            else if (dataclass.type_of_user!! == 3)
                intent = Intent(this@LoginActivity, MenuAdminActivity::class.java)
            else
                intent = Intent(this@LoginActivity, MenuApproveActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }

        return R.layout.activity_login

    }
    lateinit var mSocket: io.socket.client.Socket
    private val onConnect = Emitter.Listener {
        Log.e("lll", "connected...")
        Log.e("ss", "${mSocket.id()}")
//        mSocket.emit("test")



        // This doesn't run in the UI thread, so use:
        // .runOnUiThread if you want to do something in the UI
    }
    private val onConnectError =
        Emitter.Listener { Log.e("lll", "Error connecting...") }


    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)
        try {
//        This address is the way you can connect to localhost with AVD(Android Virtual Device)
            mSocket = IO.socket("http://43.229.149.79:4001")
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("loadLatLng", Emitter.Listener {
                Log.e("ll", "${it.get(it.size - 1).toString()}")


            })
//        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);



            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

            mSocket.connect();


        }catch (ex:Exception){
            ex.message
        }
    }

    fun onclickLogin(view: View) {


        if (usernameET.text.toString().length < 1) {
            showToast("กรุณาระบุ Username")
            return
        }

        if (passET.text.toString().length < 1) {
            showToast("กรุณาระบุ Password")
            return
        }


       val myString = JSONObject()
        myString.put("id", 2)
        myString.put("openRoom", true)


//        mSocket.emit("openRoom", myString)

//        mSocket.emit("updateLatLng", "${usernameET.text.toString()}")

//
        HttpMainConnect()
            .getApiService()
            .checkUser("${usernameET.text}","${passET.text}")
            .enqueue(checkUser())

    }

    fun actionRegister(view: View) {
        startActivity(Intent(this,RegisterActivity::class.java))


    }

    inner class checkUser : Callback<ResponeUserDao> {

        init {
            showProgressDialog()
        }

        override fun onFailure(call: Call<ResponeUserDao>, t: Throwable) {
            hideDialog()
            Log.e("sss","${t.message}")
            showToast("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")

        }

        override fun onResponse(call: Call<ResponeUserDao>, response: Response<ResponeUserDao>) {
            hideDialog()

            if (response.isSuccessful) {

                if (response.body()!!.status == 0) {

                    hideDialog()
                    showToast("${response.body()!!.msg}")

                } else
                    response.body()?.user?.let {
                        val gson = Gson()
                        val json = gson.toJson(it)
                        val edit = spf.edit()
                        edit.putString("${Constant._UDATA}", "${json}")
                        edit.commit()

                        var intent:Intent

                        if (it.type_of_user!! == 1)
                        intent = Intent(this@LoginActivity, MainMapsActivity::class.java)

                        if (it.type_of_user!! == 3)
                            intent = Intent(this@LoginActivity, MenuAdminActivity::class.java)

                        else
                            intent = Intent(this@LoginActivity, MenuApproveActivity::class.java)

                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    }

            } else {

                hideDialog()
                showToast("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")

            }


        }

    }



}