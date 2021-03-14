package com.idon.emergencmanagement.view.activity

import android.content.Intent
import android.view.View
import com.idon.emergencmanagement.R
import com.tt.workfinders.BaseClass.BaseActivity

class MenuApproveActivity : BaseActivity(){


    override fun getContentView(): Int {
        return R.layout.activity_main_menu
    }

    fun actionWorning(view: View) {

    }
    fun actionRequest(view: View) {

        startActivity(Intent(this,WorningRequestActivity::class.java))


    }
    fun checkIn(view: View) {
        startActivity(Intent(this,MapsCheckIn::class.java))

    }

    fun actionContact(view: View) {

    }


    fun actionLogout(view: View) {

    }


}