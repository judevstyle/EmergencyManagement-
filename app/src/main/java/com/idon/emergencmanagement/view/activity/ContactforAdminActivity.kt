package com.idon.emergencmanagement.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.model.ContactData
import com.idon.emergencmanagement.model.ResponeDao
import com.idon.emergencmanagement.view.adapter.ContactAdapter
import com.panuphong.smssender.helper.HandleClickListener
import com.tt.workfinders.BaseClass.BaseActivity
import com.zine.ketotime.network.HttpMainConnect
import kotlinx.android.synthetic.main.activity_checkin_list.*
import kotlinx.android.synthetic.main.toolbar_title.*
import org.parceler.Parcels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactforAdminActivity : BaseActivity(),HandleClickListener{
    private lateinit var adapters: ContactAdapter

    var type = 0

    override fun getContentView(): Int {
       return R.layout.activity_contact
    }


    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)

        toolbar.title = "ข้อมูลผู้ติดต่อ"
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

            bottomsheet(adapters.getItem(position))
        }
        

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }


    fun bottomsheet(value: ContactData) {

        val bottomSheetView: View =
            layoutInflater.inflate(R.layout.bottom_sheet_layout_action, null)
        val bottomSheetDialog = BottomSheetDialog(this!!)
        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetDialog.show()
        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(bottomSheetView.parent as View)
        val deleteMenu =
            bottomSheetView.findViewById<View>(R.id.menu_bottom_sheet_delete) as TextView





        deleteMenu.setOnClickListener {
            bottomSheetDialog.dismiss()

            val builder = AlertDialog.Builder(this!!)
            builder.setMessage("ยืนยันการลบข้อมูล")
            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->

                value.status = "1"
                HttpMainConnect()
                    .getApiService()
                    .update_contact(value)
                    .enqueue(CallUpdate())



            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->

            }


            builder.show()


        }


        bottomSheetDialog.setOnShowListener {
            // do something
        }

        bottomSheetDialog.setOnDismissListener {
            // do something
        }
    }


    inner class CallUpdate : Callback<ResponeDao> {
        override fun onFailure(call: Call<ResponeDao>, t: Throwable) {
            Log.e("err", "${t.message}")
        }

        override fun onResponse(call: Call<ResponeDao>, response: Response<ResponeDao>) {

            if (response.isSuccessful) {
                adapters.clear()
                loadData()

                Log.e("ss", "ss ${response.body()?.status} , ${response.body()?.msg}")

            }

        }

    }

    fun actionAdd(view: View) {

        startActivity(Intent(this,MContactActivity::class.java))


    }


}