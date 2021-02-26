package com.zine.ketotime.network

import com.shin.tmsuser.model.maps.Directions
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface APIService {

    @GET("directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints:String,
        @Query("key") key: String
    ): Call<Directions>

    @GET("directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String
    ): Call<Directions>


//
//    @GET("menu")
//    suspend fun loadMenu(
//        @Query("page") page: Int
//    ): MenuData
//
//    @GET("menu/recommend")
//    fun loadMenu(
//    ): MenuData
//
//    @GET("news/recommend")
//    suspend fun loadBlog(
//    ): BlogData
//
//    //----store
//    @GET("member/uid/{uid}")
//    suspend fun member(
//        @Path("uid") uid: String
//    ): UserData

}