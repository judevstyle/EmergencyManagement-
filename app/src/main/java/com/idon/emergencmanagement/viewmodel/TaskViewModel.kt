package com.panuphong.smssender.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.panuphong.smssender.model.PostData
import com.panuphong.smssender.repository.TaskRespt
import com.panuphong.smssender.model.ResponeData
import com.panuphong.smssender.model.ResponeSMS
import com.panuphong.smssender.model.Sms
import com.shin.tmsuser.database.entity.ConfigTb
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL
import java.util.*
import kotlin.concurrent.schedule

class TaskViewModel(val taskRespt: TaskRespt) : ViewModel(){


    val dataList = taskRespt.getAllData()
    val responeAddTask:LiveData<ResponeData> = MutableLiveData<ResponeData>()

    val statusSender = MutableLiveData<Boolean>()


    fun addTask(task:ConfigTb){

        val coroutineExceptionHanlder = CoroutineExceptionHandler { _, throwable ->


//            profile_state_call.value = ResponseData(false,"${throwable.message}")
        }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHanlder) {
            taskRespt.addTask(task)
        }


    }

    fun updateTask(task:ConfigTb){
        val coroutineExceptionHanlder = CoroutineExceptionHandler { _, throwable ->
//            profile_state_call.value = ResponseData(false,"${throwable.message}")
            Log.e("dddd","${throwable.message}")
        }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHanlder) {
            Log.e("dd","${task.task_name} , ${task.endpoint}")
            taskRespt.updateTask(task)
        }

    }

    fun deleteTask(task_id:Int){

        val coroutineExceptionHanlder = CoroutineExceptionHandler { _, throwable ->
//            profile_state_call.value = ResponseData(false,"${throwable.message}")
        }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHanlder) {
            taskRespt.deleteTask(task_id)
        }

    }


    fun senderSMS(smslist:ArrayList<Sms>,configTb: ConfigTb){



        viewModelScope.launch(Dispatchers.IO ) {

            for (data in smslist.iterator()){
                val postData = PostData(
                    "${configTb.smsdata.bank}"
                    , "${configTb.smsdata.bankcode}"
                    , "${data._address}"
                    , "${data._msg}"
                    , "${data._time}"
                    , "111"
                )
                val json = Gson().toJson(postData)
                commitAPI(configTb.endpoint,json)
            }

            withContext(Dispatchers.Main){
                statusSender.value = true
            }


        }
//

    }


    fun commitAPI(urlL:kotlin.String, json:kotlin.String) {


        val scope = CoroutineScope(Job() + Dispatchers.IO)

        scope.launch {

            val client = OkHttpClient()
            val url = URL("${urlL}")
            var jsonString = json
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = jsonString.toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()

            val responseBody = response.body!!.string()

            val data = Gson().fromJson<ResponeSMS>(responseBody, ResponeSMS::class.java)


            withContext(Dispatchers.Main) {
                data.status?.let {

                    if (it.equals("1001")) {

                       
                        Log.e("ss","ss")

                    } else {
                        Timer().schedule(500) {
                            commitAPI(urlL,json)
                        }

                    }

                } ?: kotlin.run {

                    Timer().schedule(500) {
                        commitAPI(urlL,json)
                    }
                }


            }


        }


    }



}