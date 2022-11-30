package com.app.fuse.ui.SingleUserView

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.ui.SingleUserView.Model.SingleUserModel
import com.app.fuse.ui.mainScreen.search.model.SendFriendRequestModel
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException


class SingleUserViewModel(val app: Application, val repository: BinjaRepository) :
    AndroidViewModel(app) {

    val singleUser = MutableLiveData<Resources<SingleUserModel>>()
    val singleUserResponse: SingleUserModel? = null

    val sendFriendRequest = MutableLiveData<Resources<SendFriendRequestModel>>()
    val sendFriendRequestResponse: SendFriendRequestModel? = null

    fun SingleUserModel(activity: Activity, accessToken: String, jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeSingleUserCall(activity, accessToken, jsonObject)
        }

    fun SendFriendRequest(activity: Activity, accessToken: String, jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeSendFriendRequestCall(activity, accessToken, jsonObject)
        }

    suspend fun SafeSingleUserCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        singleUser.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                jsonObject.get("pageNo")
                val response = repository.getSingleUser(accessToken, jsonObject)
                singleUser.postValue(handleSingleUser(response))
            } else {
                singleUser.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> singleUser.postValue(Resources.Error("Network Failure", null))
                else -> singleUser.postValue(Resources.Error("Conversion Error ${t.message}", null))
            }
        }


    }

    suspend fun SafeSendFriendRequestCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        sendFriendRequest.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                jsonObject.get("pageNo")
                val response = repository.sendFriendRequest(accessToken, jsonObject)
                sendFriendRequest.postValue(handleSendFriendRequest(response))
            } else {
                sendFriendRequest.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> sendFriendRequest.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> sendFriendRequest.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }


    }


    private fun handleSingleUser(response: Response<SingleUserModel>): Resources<SingleUserModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(singleUserResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

    private fun handleSendFriendRequest(response: Response<SendFriendRequestModel>): Resources<SendFriendRequestModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(sendFriendRequestResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)

    }
}