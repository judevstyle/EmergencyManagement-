package com.zine.ketotime.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HttpMainConnect {

//    private val BASE_URL = "https://s-softdev.com/keto-api/api/"

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.43.185/FWorningAPI/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun getApiService(): APIService {

        return retrofit.create(APIService::class.java)

    }







}