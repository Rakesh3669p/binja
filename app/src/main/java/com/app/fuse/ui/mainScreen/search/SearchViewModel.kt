package com.app.fuse.ui.homescreen.search

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SingleLiveEvent
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.ui.mainScreen.search.model.*
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class SearchViewModel(val app: Application, val repository: BinjaRepository) :
    AndroidViewModel(app) {

    val searchData = MutableLiveData<Resources<SearchModel>>()
    var searchResponse: SearchModel? = null

    val recentSearchedData = SingleLiveEvent<Resources<InsertedSearchedDataModel>>()
    var recentSearchedResponse: InsertedSearchedDataModel? = null

    val insertRecentSearchedData = SingleLiveEvent<Resources<InsertedSearchResponseModel>>()
    var insertRecentSearchedResponse: InsertedSearchResponseModel? = null

    val sendFriendRequestData = MutableLiveData<Resources<SendFriendRequestModel>>()
    var sendFriendRequestResponse: SendFriendRequestModel? = null


    /****************************************Top Searched Users**********************************************************/
    fun Search(activity: Activity, accessToken: String, jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeSearchCall(activity, accessToken, jsonObject)
        }

    suspend fun SafeSearchCall(activity: Activity, accessToken: String, jsonObject: JsonObject) {
        searchData.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.searchUsers(accessToken, jsonObject)
                searchData.postValue(handleSearch(response))
            } else {
                searchData.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchData.postValue(Resources.Error("Network Failure", null))
                else -> searchData.postValue(Resources.Error("Conversion Error ${t.message}", null))
            }
        }


    }

    private fun handleSearch(response: Response<SearchModel>): Resources<SearchModel>? {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(searchResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }


    /****************************************Send Friend Request**********************************************************/

    fun SendFriendRequest(activity: Activity, accessToken: String, jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeSendFriendRequestCall(activity, accessToken, jsonObject)
        }

    private suspend fun SafeSendFriendRequestCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {

        sendFriendRequestData.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.sendFriendRequest(accessToken, jsonObject)
                sendFriendRequestData.postValue(handleSendFriendRequest(response))
            } else {
                sendFriendRequestData.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchData.postValue(Resources.Error("Network Failure", null))
                else -> searchData.postValue(Resources.Error("Conversion Error ${t.message}", null))
            }
        }


    }

    private fun handleSendFriendRequest(response: Response<SendFriendRequestModel>): Resources<SendFriendRequestModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(sendFriendRequestResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }


    /****************************************Insert Searched Users**********************************************************/
    fun AddSearchedUser(
        activity: Activity,
        accessToken: String,
        searchList: SearchList,
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", searchList.id)
        InsertSearchedUser(activity, accessToken, jsonObject)
    }

    private fun InsertSearchedUser(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) = viewModelScope.launch {
        SafeInsertSearchUserCall(activity, accessToken, jsonObject)
    }

    private suspend fun SafeInsertSearchUserCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        insertRecentSearchedData.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.insertSearchedUsers(accessToken, jsonObject)
                insertRecentSearchedData.postValue(handleinsertRecentSearchedData(response))
            } else {
                insertRecentSearchedData.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> insertRecentSearchedData.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> insertRecentSearchedData.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }


    }

    private fun handleinsertRecentSearchedData(response: Response<InsertedSearchResponseModel>): Resources<InsertedSearchResponseModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(insertRecentSearchedResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }


    /****************************************Get Recent/Inserted Searched Users**********************************************************/
    fun GetRecentSearchedUser(
        activity: Activity,
        accessToken: String,
    ) = viewModelScope.launch {
        SafeGetSearchedUserCall(activity, accessToken)
    }

    private suspend fun SafeGetSearchedUserCall(activity: Activity, accessToken: String) {
        recentSearchedData.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {

                val response = repository.getInsertedSearchedUsers(accessToken)
                recentSearchedData.postValue(handleGetInsertRecentSearchedData(response))
            } else {
                recentSearchedData.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> recentSearchedData.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> recentSearchedData.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleGetInsertRecentSearchedData(response: Response<InsertedSearchedDataModel>): Resources<InsertedSearchedDataModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(recentSearchedResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

}