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



    @GET("load_warning_checkin.php")
    fun getCheckIn(
    ): Call<ArrayList<UserFull>>

    @GET("load_checkin_nottrack.php")
    fun getCheckInNoneTrack(
    ): Call<ArrayList<UserFull>>



    @GET("load_accept_woring.php")
    fun getAccept(
    ): Call<WorningData>


    @GET("load_contact.php")
    fun getContact(
    ): Call<ArrayList<ContactData>>


    @GET("load_comp.php")
    fun company(
        @Query("comp_id") compId:String
    ): Call<CompanyData>


    @GET("load_my_notifydata.php")
    fun getNotifyData(
        @Query("uid") uid:String
    ): Call<ArrayList<WorningDataItem>>




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

    @FormUrlEncoded
    @POST("update_status_worning.php")
    fun updateStatus(
        @Field("uid") uid:String,
        @Field("wid") wis:String,
        @Field("status") status:Int
    ): Call<ResponeDao>



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