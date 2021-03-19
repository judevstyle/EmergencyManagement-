package com.idon.emergencmanagement.view.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.model.UserFull
import com.tt.workfinders.BaseClass.BaseActivity
import com.zine.ketotime.util.Constant
import kotlinx.android.synthetic.main.activity_main_menu.*

class MenuAdminActivity : BaseActivity(){





    lateinit var spf: SharedPreferences

    lateinit var userFull :UserFull
    override fun getContentView(): Int {
        spf = getSharedPreferences(Constant._PREFERENCES_NAME, Context.MODE_PRIVATE)
        return R.layout.activity_main_menu_admin
    }


    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)



            val gson = Gson()
            userFull = gson.fromJson(spf.getString(Constant._UDATA,"{}"), UserFull::class.java)


            Glide.with(this).load("${Constant.BASE_URL}${userFull.avatar}").into(avatarIM)
            nameTV.text = "${userFull.display_name}"




    }

    fun actionLogout(view: View) {

        val edit = spf.edit()
        edit.clear().commit()
       val intent = Intent(this, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)


    }
    fun actionSetting(view: View) {

        startActivity(Intent(this,CompanySettingActivity::class.java))

    }
    fun actionContact(view: View) {
        startActivity(Intent(this,ContactforAdminActivity::class.java))

    }

    fun actionAdd(view: View) {
        startActivity(Intent(this,AddmemberActivity::class.java))

    }
}