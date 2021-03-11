package com.idon.emergencmanagement.view.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.model.ResponeUserDao
import com.tt.workfinders.BaseClass.BaseActivity
import com.zine.ketotime.network.HttpMainConnect
import com.zine.ketotime.util.Constant
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.passET
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity(){


    lateinit var spf: SharedPreferences

    override fun getContentView(): Int {
        spf = getSharedPreferences(Constant._PREFERENCES_NAME, Context.MODE_PRIVATE)

        if (!spf.getString(Constant._UDATA,"").equals("")){
            val intent = Intent(this@LoginActivity, MainMapsActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }

        return R.layout.activity_login

    }

    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)

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

                        val intent = Intent(this@LoginActivity, MainMapsActivity::class.java)
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