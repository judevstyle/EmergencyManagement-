package com.idon.emergencmanagement.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.idon.emergencmanagement.R
import com.idon.emergencmanagement.model.ImageWorningImg
import com.idon.emergencmanagement.util.FileUtil
import com.idon.emergencmanagement.view.adapter.ImageAdapter
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.panuphong.smssender.helper.HandleClickListener
import com.tt.workfinders.BaseClass.BaseActivity
import com.zine.ketotime.util.Constant
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_notify_worning.*
import kotlinx.android.synthetic.main.toolbar_title.*
import kotlinx.coroutines.launch
import org.parceler.Parcels
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class NotifyWorningActivity : BaseActivity(), HandleClickListener, OnMapReadyCallback {

    private var providerFile: Uri? = null
    private var mGoogleMap: GoogleMap? = null
    private lateinit var adapters: ImageAdapter
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    var location: LatLng? = null
    val REQUEST_CODE = 1001
    val REQUEST_CODE_LOCATION = 1003

    val PICK_IMAGE_REQUEST = 1002
    private var actualImage: File? = null
    private var compressedImage: File? = null

    override fun getContentView(): Int {
        return R.layout.activity_notify_worning

    }

    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)

        toolbar.title = "แจ้งเตือนภัย"
        setSupportActionBar(toolbar)
        showBackArrow()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        adapters = ImageAdapter(this, this)
        cv_evidence.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            isNestedScrollingEnabled = false
            adapter = adapters
        }

        Dexter.withContext(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) { /* ... */
                    getLastLocation()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */

                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) { /* ... */
                }
            }).check()
        val mapFragment = supportFragmentManager?.findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment?.getMapAsync(this)


    }


    override fun onStart() {
        super.onStart()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        try {
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode === Activity.RESULT_OK) {



                Log.e("ldw","${REQUEST_CODE}")
                if (requestCode == REQUEST_CODE) {


                    showToast("sl;w0")

                    try {
                        val intent = data?.data
                        showToast("sl;w1.1")


                        Log.e("ss","${intent}")
                        actualImage = FileUtil.from(this, providerFile!!)?.also { imageFile ->
                            showToast("sl;w1")

                            lifecycleScope.launch {
                                // Default compression
                                showToast("sl;w2")

                                compressedImage =
                                    Compressor.compress(this@NotifyWorningActivity, imageFile)

                                compressedImage?.let {
                                    val img_base64 =
                                        convertImage(BitmapFactory.decodeFile(it.absolutePath))
                                    val img = ImageWorningImg(0, "", img_base64, providerFile, 0)
                                    adapters.setItem(img)
                                    showToast("sl;w3")

                                }
                            }


                        }


                    } catch (e: IOException) {
                        showToast("Failed to read picture data!")
                        e.printStackTrace()
                    }

                }
                if (requestCode === REQUEST_CODE_LOCATION) {
                    location =
                        LatLng(data!!.getDoubleExtra("lat", 0.0), data!!.getDoubleExtra("lng", 0.0))
                    setMarker(location!!)


                } else if (requestCode == PICK_IMAGE_REQUEST) {

                    try {
                        val intent = data?.data

                        actualImage = FileUtil.from(this, data?.data!!)?.also { imageFile ->
                            showToast("sl;w1")

                            lifecycleScope.launch {
                                // Default compression
                                showToast("sl;w2")

                                compressedImage =
                                    Compressor.compress(this@NotifyWorningActivity, imageFile)

                                compressedImage?.let {
                                    val img_base64 =
                                        convertImage(BitmapFactory.decodeFile(it.absolutePath))
                                    val img = ImageWorningImg(0, "", img_base64, data?.data, 0)
//                                arrImg.add(img)
                                    adapters.setItem(img)
//                                avatarIM.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
                                    showToast("sl;w3")

                                }
                            }


                        }


                    } catch (e: IOException) {
                        showToast("Failed to read picture data!")
                        e.printStackTrace()
                    }

                }
            }
        } catch (ex: Exception) {

        }

    }

    override fun onItemClick(view: View, position: Int, action: Int) {
        when (action) {


            Constant.ACTION_CLICKITEM -> {
                bottomsheet()


            }

            Constant.ACTION_CLICKITEM_DEL -> {


                if (position == 0) {

                } else {

                    if (view.id == R.id.cancleAction) {

//                    val data = adapters.getItem(position+1)
//                    data.img_del = 1
//                    imgDel.add(data)
                    adapters.removeItem(position-1)

                    }
                }


            }

        }


    }


    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap


    }

    fun createCustomMarkerFire(
        context: Context,
        @DrawableRes resource: Int,
        _name: String?
    ): Bitmap? {
        val marker =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.custom_marker_fire_layout,
                null
            )

        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        marker.layoutParams = ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT)
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            marker.measuredWidth,
            marker.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        marker.draw(canvas)
        return bitmap
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {

                    location = LatLng(task.result.latitude, task.result.longitude)
                    setMarker(location!!)


                } else {
//                    Log.w(TAG, "getLastLocation:exception", task.exception)
//                    showMessage(getString(R.string.no_location_detected))
                    showToast("Err")
                }
            }
    }

    fun setMarker(locate: LatLng) {


        mGoogleMap!!.clear()
        val fire2MarkerOptions: MarkerOptions = MarkerOptions().position(locate).title("").icon(
            BitmapDescriptorFactory.fromBitmap(
                createCustomMarkerFire(this, R.drawable.bg_gradient, "Narender")
            )
        )

        mGoogleMap!!.addMarker(fire2MarkerOptions)
        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(locate, 17.0f));


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    fun actionSelectMaps(view: View) {

        val intent = Intent(
            this,
            SelectMapsActivity::class.java
        )
        intent.putExtra("lat", location?.latitude)
        intent.putExtra("lng", location?.longitude)
        startActivityForResult(intent, REQUEST_CODE_LOCATION)

    }


    fun bottomsheet() {

        val bottomSheetView: View =
            layoutInflater.inflate(R.layout.bottom_sheet_layout_photo, null)
        val bottomSheetDialog = BottomSheetDialog(this!!)
        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetDialog.show()
        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(bottomSheetView.parent as View)
        val cameraMenu =
            bottomSheetView.findViewById<View>(R.id.menu_bottom_sheet_camera) as TextView

        val imgMenu =
            bottomSheetView.findViewById<View>(R.id.menu_bottom_sheet_img) as TextView


        cameraMenu.setOnClickListener {
            bottomSheetDialog.dismiss()

            Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) { /* ... */

                        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        val filePhoto = getPhotoFile("photo.jpg")

                         providerFile = FileProvider.getUriForFile(
                            this@NotifyWorningActivity,
                            "com.idon.emergencmanagement.provider",
                            filePhoto
                        )
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
//                        startActivityForResult(intent, REQUEST_CODE);
                        if (takePhotoIntent.resolveActivity(packageManager) != null) {
                            startActivityForResult(takePhotoIntent, REQUEST_CODE)
                        } else {
                            Toast.makeText(
                                this@NotifyWorningActivity,
                                "Camera could not open",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) { /* ... */
                    }
                }).check()


        }


        imgMenu.setOnClickListener {
            bottomSheetDialog.dismiss()
            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) { /* ... */

                        openGallery()

                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) { /* ... */
                    }
                }).check()


        }

        bottomSheetDialog.setOnShowListener {
            // do something
        }

        bottomSheetDialog.setOnDismissListener {
            // do something
        }
    }


    fun openGallery() {

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }


    fun convertImage(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val encodedImage: String = android.util.Base64.encodeToString(
            byteArrayOutputStream.toByteArray(),
            android.util.Base64.DEFAULT
        )
        //  Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
        return encodedImage


    }


    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", directoryStorage)
    }


}