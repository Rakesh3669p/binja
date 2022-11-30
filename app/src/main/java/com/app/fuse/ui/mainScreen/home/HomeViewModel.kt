package com.app.fuse.ui.homescreen.home

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fuse.utils.SingleLiveEvent
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.mainScreen.home.FriendsList.model.FriendDetails
import com.app.fuse.ui.mainScreen.home.FriendsList.model.FriendListModel
import com.app.fuse.ui.mainScreen.home.FriendsList.model.UserLocationModel
import com.app.fuse.ui.mainScreen.home.OnGoingmatches.model.OnGoingMatchModel
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class HomeViewModel(val app: Application, val repository: BinjaRepository) : AndroidViewModel(app) {

     var session =  SessionManager(app.applicationContext)
    val homeFriendsList = MutableLiveData<Resources<FriendListModel>>()
    var homeFriendsListResponse: FriendListModel? = null


    val userLocation = MutableLiveData<Resources<UserLocationModel>>()
    var userLocationResponse: UserLocationModel? = null

    val onGoingMatch = SingleLiveEvent<Resources<OnGoingMatchModel>>()
    var onGoingMatchResponse: OnGoingMatchModel? = null


    fun FriendsList(activity: Activity, accessToken: String, jsonObject: JsonObject) =
        viewModelScope.launch { SafeFriendsListCall(activity, accessToken, jsonObject) }

    suspend fun SafeFriendsListCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        homeFriendsList.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getFriendsList(accessToken, jsonObject)
                homeFriendsList.postValue(handleFriendsList(response))
            } else {
                homeFriendsList.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> homeFriendsList.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> homeFriendsList.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }


    }

    private fun handleFriendsList(response: Response<FriendListModel>): Resources<FriendListModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(homeFriendsListResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

    /******************************************User Location********************************************************************************/
    fun UserLocation(activity: Activity, accessToken: String, jsonObject: JsonObject) =
        viewModelScope.launch { SafeUserLocationCall(activity, accessToken, jsonObject) }

    suspend fun SafeUserLocationCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        userLocation.postValue(Resources.Loading())


        try {
            if (hasInternetConnection(activity)) {
                val response = repository.setUserLocation(accessToken, jsonObject)
                userLocation.postValue(handleUserLocation(response))
            } else {
                userLocation.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> userLocation.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> userLocation.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }


    }

    private fun handleUserLocation(response: Response<UserLocationModel>): Resources<UserLocationModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(userLocationResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }


    /******************************************On Going Match********************************************************************************/
    fun GetOnGoingMatch(activity: Activity) =
        viewModelScope.launch { SafeOnGoingMatchCall(activity, session.token!!) }

    suspend fun SafeOnGoingMatchCall(
        activity: Activity,
        accessToken: String,

        ) {
        onGoingMatch.postValue(Resources.Loading())


        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getOnGoingMatches(accessToken)
                onGoingMatch.postValue(handleOnGoingMatch(response))
            } else {
                onGoingMatch.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> onGoingMatch.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> onGoingMatch.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleOnGoingMatch(response: Response<OnGoingMatchModel>): Resources<OnGoingMatchModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(onGoingMatchResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

    fun insertOnGoingMatchFriend(onGoingMatchFriend: FriendDetails) = viewModelScope.launch {
        repository.insertOnGoingFriends(onGoingMatchFriend)
    }
    fun getOnGoingMatchesFriends() = repository.getOnGoingFriends()

    fun deleteOnGoingMatchesFriends() = viewModelScope.launch {
        repository.deleteOnGoingFriends()
    }

}