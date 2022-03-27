package com.test.test_karim.Repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.test.test_karim.data.local.AppDatabase
import com.test.test_karim.data.model.Guests
import com.test.test_karim.data.remote.ApiService
import com.test.test_karim.data.remote.ErrorMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserDetailRepositoryImpl(
    private val service: ApiService
): guestsRepository, KoinComponent {

    private val db: AppDatabase by inject()

    // ******************************************************* generate Code VT
    override suspend fun getGuestsToApi(): Flow<List<Guests>>? {
        var data: Flow<List<Guests>>? = null
        val response = service.getEvents().await()
        if (response.isSuccessful){
            response.body()?.let {
                it.data?.let {guests ->
                    data = flowOf(guests)
                }
            }
        } else {
            if (response.errorBody()!=null) {
                val json: String = response.errorBody()!!.string()
                val obj: JsonObject = JsonParser().parse(json).asJsonObject
                val error = Gson().fromJson(obj, ErrorMessage::class.java)
                throw Throwable(error.message)
            } else {
                throw Throwable("The error body is null")
            }
        }
        return data
    }
}

interface guestsRepository{
    suspend fun getGuestsToApi(): Flow<List<Guests>>?
}