package com.test.test_karim.util

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import retrofit2.HttpException
import java.net.URL


object NetworkUtil {

    /**
     * Returns true if the Throwable is an instance of RetrofitError with an
     * http status code equals to the given one.
     */
    val api = "https://api.github.com/"
    var useAPI = api
    var intervalSync = 15 // in minutes
    fun getBaseUrl(context: Context) : String {
        val pref_setting: SharedPreferences by lazy {
            context.getSharedPreferences("setting.conf", Context.MODE_PRIVATE)
        }
        //val url = pref_setting.getString("server_url", useAPI)!!
        val url = api
        return url
    }

    fun getPhotoURL(context: Context) : String {
        val pref_setting: SharedPreferences by lazy {
            context.getSharedPreferences("setting.conf", Context.MODE_PRIVATE)
        }

        val url = URL(pref_setting.getString("server_url", useAPI))
        val url_port = if (url.port != -1) {
            url.port.toString()
        } else {
            ""
        }
        val baseUrl = url.protocol + "://" + url.host + ":" + url_port
        val photoUrl = "$baseUrl/api/smmdk/web/"

        return photoUrl
    }

    fun isHttpStatusCode(throwable: Throwable, statusCode: Int): Boolean {
        return throwable is HttpException && throwable.code() == statusCode
    }

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}