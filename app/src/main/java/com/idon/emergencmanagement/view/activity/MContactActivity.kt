package com.idon.emergencmanagement.view.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.model.*
import com.idon.emergencmanagement.util.FileUtil
import com.tt.workfinders.BaseClass.BaseActivity
import com.yalantis.ucrop.UCrop
import com.zine.ketotime.network.HttpMainConnect
import com.zine.ketotime.util.Constant._PREFERENCES_NAME
import com.zine.ketotime.util.Constant._UDATA
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_m_company.*
import kotlinx.android.synthetic.main.activity_m_company.nameET
import kotlinx.android.synthetic.main.activity_m_contact.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.avatarIM
import kotlinx.android.synthetic.main.activity_register.telET
import kotlinx.android.synthetic.main.toolbar_title.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

class MContactActivity : BaseActivity() {

    lateinit var spf: SharedPreferences
    var gendar: String = ""
    lateinit var user: UserFull

    // [END declare_auth]
    private val REQUEST_SELECT_PICTURE_FOR_FRAGMENT = 0x02
    private val SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage"
    private val requestMode: Int = 1001
    private var actualImage: File? = null
    private var compressedImage: File? = null

    override fun getContentView(): Int {
        return R.layout.activity_m_contact
    }

    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)

        spf = getSharedPreferences(_PREFERENCES_NAME, Context.MODE_PRIVATE)


        toolbar.title = "จัดการข้อมูลส่วนตัว"
        setSupportActionBar(toolbar)
        showBackArrow()


    }


    fun actionChooseImg(view: View) {
        pickFromGallery()

    }


    var img = ""
    fun actionCommit(view: View) {


        compressedImage?.let {
//                    compressedImageView.setImageBitmap()
            img = uploadImage(BitmapFactory.decodeFile(it.absolutePath))
        }

//        if (img.equals("")) {
//            img = user.avatar
//        }


        if (compressedImage == null) {
            showToast("เลือกรูปภาพ")
            return
        } else if (nameET.text.toString().length < 1) {
            showToast("กรุณาระบุชื่อ")
            return
        }else if (jobPositionET.text.toString().length < 1) {
                showToast("กรุณาระบุจุดที่รับผิดชอบ")
                return
        } else if (telET.text.toString().length < 1) {
            showToast("กรุณาระบุเบอร์มือถือ")
            return
        }


        val data = ContactData(
            img,null,"1001","${nameET.text}","${jobPositionET.text}",null,"${telET.text}"

        )

        HttpMainConnect()
            .getApiService()
            .m_contact(data)
            .enqueue(CallInsert())



    }


    //    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadImage(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
//        val encodedImage: String =
//            Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
        val encodedImage: String = android.util.Base64.encodeToString(
            byteArrayOutputStream.toByteArray(),
            android.util.Base64.DEFAULT
        )
        return encodedImage

    }


    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType("image/*")
            .addCategory(Intent.CATEGORY_OPENABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val mimeTypes =
                arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        startActivityForResult(
            Intent.createChooser(
                intent,
                getString(R.string.app_name)
            ), requestMode
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == requestMode) {
                val selectedUri = data!!.data
                if (selectedUri != null) {
                    startCrop(selectedUri)
                } else {
                    Toast.makeText(
                        this,
                        R.string.app_name,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data!!)
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
//            handleCropError(data)
        }

    }


    private fun handleCropResult(result: Intent) {
        val resultUri = UCrop.getOutput(result)
        avatarIM.setImageResource(0)
        avatarIM.setImageURI(resultUri!!)

        try {
            actualImage = FileUtil.from(this!!, resultUri!!)?.also {

            }
        } catch (e: IOException) {
//                    showError("Failed to read picture data!")
            e.printStackTrace()
        }

        actualImage?.let { imageFile ->
            lifecycleScope.launch {
                // Default compression
                compressedImage = Compressor.compress(this@MContactActivity, imageFile)
//                        upload()
            }
        } ?: showToast("Please choose an image!")


    }

    private fun startCrop(uri: Uri) {
        var destinationFileName: String =
            SAMPLE_CROPPED_IMAGE_NAME

        var uCrop = UCrop.of(
            uri,
            Uri.fromFile(File(cacheDir, destinationFileName))
        )
        uCrop = basisConfig(uCrop)
//        uCrop = advancedConfig(uCrop)
        if (requestMode == REQUEST_SELECT_PICTURE_FOR_FRAGMENT) {       //if build variant = fragment
//            setupFragment(uCrop)
        } else {                                                        // else start uCrop Activity
            uCrop.start(this)
        }
    }

    fun basisConfig(@NonNull uCrop: UCrop): UCrop {
        var data = uCrop
        data = uCrop.withAspectRatio(192.toFloat(), 192.toFloat())
        return data
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }


    inner class CallInsert : Callback<ResponeDao> {


        override fun onFailure(call: Call<ResponeDao>, t: Throwable) {
            Log.e("err", "${t.message}")
        }

        override fun onResponse(call: Call<ResponeDao>, response: Response<ResponeDao>) {

            if (response.isSuccessful) {
               finish()
                Log.e("ss", "ss ${response.body()?.status} , ${response.body()?.msg}")

            }

        }

    }

}