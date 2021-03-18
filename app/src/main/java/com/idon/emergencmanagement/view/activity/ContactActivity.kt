package com.idon.emergencmanagement.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.model.ContactData
import com.idon.emergencmanagement.view.adapter.ContactAdapter
import com.panuphong.smssender.helper.HandleClickListener
import com.tt.workfinders.BaseClass.BaseActivity
import com.zine.ketotime.network.HttpMainConnect
import kotlinx.android.synthetic.main.activity_checkin_list.*
import kotlinx.android.synthetic.main.toolbar_title.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactActivity : BaseActivity(),HandleClickListener{
    private lateinit var adapters: ContactAdapter

    var type = 0

    override fun getContentView(): Int {
       return R.layout.activity_checkin_list
    }


    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)



        toolbar.title = "ติดต่อ"
        setSupportActionBar(toolbar)
        showBackArrow()



        adapters = ContactAdapter(this,this)

        cv_list.apply {

            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = adapters

        }


    }

    override fun onResume() {
        super.onResume()

        loadData()
    }

    fun loadData(){
            HttpMainConnect()
                .getApiService()
                .getContact()
                .enqueue(CallWorning())



    }

    inner class CallWorning : Callback<ArrayList<ContactData>> {

        init {
            showProgressDialog()
        }

        override fun onFailure(call: Call<ArrayList<ContactData>>, t: Throwable) {
            showToast("err")
            hideDialog()
        }

        override fun onResponse(call: Call<ArrayList<ContactData>>, response: Response<ArrayList<ContactData>>) {
            hideDialog()

            if (response.isSuccessful) {

                response.body()?.let {


                    adapters.setItem(it)


                }

            } else {
                showToast("err")

            }
        }

    }

    override fun onItemClick(view: View, position: Int, action: Int) {

        if (action == 3){

            val data = adapters.getItem(position)
            val intent =
                Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${data.tel}")
            startActivity(intent)

        }else{
            
        }
        

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }





}