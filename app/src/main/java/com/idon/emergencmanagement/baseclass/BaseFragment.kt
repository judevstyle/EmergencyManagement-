package com.zine.ketotime.BaseClass

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.idon.emergencmanagement.view.customview.CustomProgressDialog

abstract class BaseFragment:Fragment() {

    protected val parties = arrayOf(
        "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H"
    )
//    lateinit var tfRegular: Typeface
//    lateinit var tfLight: Typeface
     var mProgressDialog: CustomProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



//        tfRegular = Typeface.createFromAsset(assets, "Kanit-Bold.ttf")
//        tfLight = Typeface.createFromAsset(assets, "Kanit-Light.ttf")

    }

//    open fun onCreateView(
//        inflater: LayoutInflater?,
////        parent: ViewGroup?,
////        savedInstanseState: Bundle?
////    ): View? {
////        return provideFragmentView(inflater, parent, savedInstanseState)
////    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return provideFragmentView(inflater, container, savedInstanceState)
    }


    abstract fun provideFragmentView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewReady(view, savedInstanceState)
    }

    @CallSuper
    protected open fun onViewReady(
        view: View,
        savedInstanceState: Bundle?
    ) { //To be used by child activities
    }


    open fun noInternetConnectionAvailable() {
//        showToast(getString(R.string.noNetworkFound))
    }


    open fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = CustomProgressDialog(context)
        }
        if (!mProgressDialog!!.isShowing()) { //            mProgressDialog.setMessage(message);
            mProgressDialog!!.show()
        }
    }


    open fun hideDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing()) {
            mProgressDialog!!.dismiss()
        }
    }

    protected open fun showAlertDialog(msg: String?) {
//        val dialogBuilder =
//            AlertDialog.Builder(context!)
//        dialogBuilder.setTitle(null)
//        dialogBuilder.setIcon(R.mipmap.ic_launcher)
//        dialogBuilder.setMessage(msg)
//        dialogBuilder.setPositiveButton(""
////            getString(R.string.dialog_ok_btn)
//        ) { dialog, which -> dialog.cancel() }
//        dialogBuilder.setCancelable(false)
//        dialogBuilder.show()
    }


    protected open fun showToast(mToastMsg: String?) {
        Toast.makeText(context, mToastMsg, Toast.LENGTH_LONG).show()
    }
}