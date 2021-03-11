package com.zine.ketotime.network

import androidx.room.Embedded
import com.idon.emergencmanagement.model.*
import com.shin.tmsuser.model.maps.Directions
import retrofit2.Call
import retrofit2.http.*


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


    @GET("load_request_worning.php")
    fun getRequest(
    ): Call<WorningData>


    @GET("load_accept_woring.php")
    fun getAccept(
    ): Call<WorningData>

    @GET("load_comp.php")
    fun company(
        @Query("comp_id") compId:String
    ): Call<CompanyData>


    @POST("register.php")
    fun register(
        @Body user:User
    ): Call<ResponeUserDao>


    @POST("m_worning.php")
    fun woring(
        @Body data:WorningDataItem
    ): Call<ResponeDao>


    @FormUrlEncoded
    @POST("check_user.php")
    fun checkUser(
        @Field("username") user:String,
        @Field("password") password:String

    ): Call<ResponeUserDao>



    @POST("update_user.php")
    fun updateUser(
        @Body user:UserFull
    ): Call<ResponeUserDao>





//    @POST("update_user.php")
//    fun updateUser(
//        @Body user:UserFull
//    ): Call<ResponeUserDao>






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