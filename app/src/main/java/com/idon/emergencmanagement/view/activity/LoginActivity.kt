package com.idon.emergencmanagement.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.idon.emergencmanagement.R
import com.tt.workfinders.BaseClass.BaseActivity

class LoginActivity : BaseActivity(){


    override fun getContentView(): Int {

        return R.layout.activity_login

    }

    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)

    }

    fun onclickLogin(view: View) {

        startActivity(Intent(this,MainMapsActivity::class.java))

    }

    fun actionRegister(view: View) {
        startActivity(Intent(this,RegisterActivity::class.java))


    }


}