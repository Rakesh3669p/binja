package com.app.fuse.ui.mainScreen.nearbyusers

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.ui.mainScreen.nearbyusers.model.NearByUsersModel
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NearByUsersViewModel(val app: Application, val repository: BinjaRepository) :
    AndroidViewModel(app) {

    val nearByUsers = MutableLiveData<Resources<NearByUsersModel>>()
    var nearByUsersResponse: NearByUsersModel? = null


    fun NearByUsers(activity: Activity, accessToken: String, jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeNearByUsersCall(activity, accessToken, jsonObject)
        }

    suspend fun SafeNearByUsersCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        nearByUsers.postValue(Resources.Loading())


        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getNearByUsers(accessToken, jsonObject)
                nearByUsers.postValue(handleNearByUser(response))
            } else {
                nearByUsers.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> nearByUsers.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> nearByUsers.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }


    }

    private fun handleNearByUser(response: Response<NearByUsersModel>): Resources<NearByUsersModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(nearByUsersResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }


}