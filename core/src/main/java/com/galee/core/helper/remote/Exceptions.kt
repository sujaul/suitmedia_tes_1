package com.galee.core.helper.remote

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class NoConnectivityException(message :String = "No internet connection, please turn on your internet connection first"): IOException(message)
class LocationPermissionNotGrantedException: Exception()
class DateNotFoundException: Exception()
class ApiResponseException(message: String) : Throwable(message)
class NoDataFoundException: Throwable(){
    override val message: String?
        get() = "Data unavailable"
}
fun StringBuilder.getErrorFromNetwork(errorInputStream: InputStream): StringBuilder? {
    try {
        val reader = BufferedReader(InputStreamReader(errorInputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            this.append(line)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
    return this
}