package com.test.test_karim.data.remote

import com.test.test_karim.data.model.Guests
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    /**
     * API FOR LOGIN
     */
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("username") username: String, @Field("password") password: String,
        @Field("device_id") dev_id: String
    ): Call<ResponseBody>

    /**
     * API FOR GET DATA
     */

    @GET("/getdata")
    fun getOrderList(
        @Query("page") page: Int,
        @Query("status") orderStatus: String?
    ): Call<ResponseBody>


    /**
     * API FOR SEARCH USER
     */
    //@Headers("Accept: application/json")
    @GET("api/users")
    fun getEvents(): Deferred<Response<GuestResponse>>

    /**
     * API FOR SEARCH USER
     */
    //@Headers("Accept: application/json")
    @GET("users/{user}/repos")
    fun getRepo(
        @Path("user") user: String
    ): Deferred<Response<List<Guests>>>
}


