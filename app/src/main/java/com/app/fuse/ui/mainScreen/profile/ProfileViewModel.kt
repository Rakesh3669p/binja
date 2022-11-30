package com.app.fuse.ui.mainScreen.profile

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.homescreen.profile.model.RequestReceivedModel
import com.app.fuse.ui.homescreen.profile.model.RequestSentModel
import com.app.fuse.ui.mainScreen.profile.model.LogOutModel
import com.app.fuse.utils.Resources
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.ui.mainScreen.search.model.FriendRequestActionModel
import com.app.fuse.utils.SingleLiveEvent
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class ProfileViewModel(val app: Application, val repository: BinjaRepository) : AndroidViewModel(app) {

    val receivedRequestedUser = MutableLiveData<Resources<RequestReceivedModel>>()
    private val receivedRequestedUserResponse: RequestReceivedModel? = null

    val sentRequestedUser = MutableLiveData<Resources<RequestSentModel>>()
    private val sentRequestedUserResponse: RequestSentModel? = null

    val friendRequestAction = MutableLiveData<Resources<FriendRequestActionModel>>()
    private val friendRequestActionResponse: FriendRequestActionModel? = null

    val logOut = SingleLiveEvent<Resources<LogOutModel>>()
    private val logOutResponse: LogOutModel? = null


    fun receivedRequest(activity: Activity, accessToken: String) =
        viewModelScope.launch {
            safeReceivedRequestCall(activity, accessToken)
        }

    fun sentRequest(activity: Activity, accessToken: String) =
        viewModelScope.launch {
            safeSentRequestCall(activity, accessToken)
        }

    fun friendRequestAction(activity: Activity, accessToken: String,jsonObject: JsonObject) =
        viewModelScope.launch {
            safeFriendRequestActionCall(activity, accessToken,jsonObject)
        }

    private suspend fun safeFriendRequestActionCall(activity: Activity, accessToken: String, jsonObject: JsonObject) {
        friendRequestAction.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.friendRequestAction(accessToken,jsonObject)
                friendRequestAction.postValue(handleFriendRequestAction(response))
            } else {
                friendRequestAction.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> friendRequestAction.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> friendRequestAction.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }

    }



    private suspend fun safeReceivedRequestCall(
        activity: Activity,
        accessToken: String,
    ) {
        receivedRequestedUser.postValue(Resources.Loading())
        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getReceivedRequest(accessToken)
                receivedRequestedUser.postValue(handleReceivedRequest(response))
            } else {
                receivedRequestedUser.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> receivedRequestedUser.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> receivedRequestedUser.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }


    }



    suspend fun safeSentRequestCall(
        activity: Activity,
        accessToken: String,
    ) {
        sentRequestedUser.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getSentRequest(accessToken)
                sentRequestedUser.postValue(handleSentRequest(response))
            } else {
                sentRequestedUser.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> sentRequestedUser.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> sentRequestedUser.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }


    }

    private fun handleReceivedRequest(response: Response<RequestReceivedModel>): Resources<RequestReceivedModel> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                return Resources.Success(receivedRequestedUserResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

    private fun handleSentRequest(response: Response<RequestSentModel>): Resources<RequestSentModel> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                return Resources.Success(sentRequestedUserResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

    private fun handleFriendRequestAction(response: Response<FriendRequestActionModel>): Resources<FriendRequestActionModel> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                return Resources.Success(friendRequestActionResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }



    fun logOut(activity: Activity, accessToken: String) =
        viewModelScope.launch {
            safeLogOutCall(activity, accessToken)
        }

    private suspend fun safeLogOutCall(
        activity: Activity,
        accessToken: String,
    ) {
        logOut.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.logOut(accessToken)
                logOut.postValue(handleLogoutRequest(response))
            } else {
                logOut.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> logOut.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> logOut.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }


    }

    private fun handleLogoutRequest(response: Response<LogOutModel>): Resources<LogOutModel> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                return Resources.Success(logOutResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }
}