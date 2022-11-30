package com.app.fuse.ui.mainScreen.notification.gameRequests

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.mainScreen.notification.gameRequests.model.GameRequestDeleteModel
import com.app.fuse.ui.mainScreen.notification.gameRequests.model.GameRequestModel
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.SingleLiveEvent
import com.app.fuse.utils.common.hasInternetConnection
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class RequestGameViewModel(val app: Application, val repository: BinjaRepository) :
    AndroidViewModel(app) {
    var session: SessionManager = SessionManager(app.applicationContext)

    val gameRequest = SingleLiveEvent<Resources<GameRequestModel>>()
    val gameRequestResponse: GameRequestModel? = null

    val gameRequestDelete = SingleLiveEvent<Resources<GameRequestDeleteModel>>()
    val gameRequestDeleteResponse: GameRequestDeleteModel? = null

    fun GetGameRequests(activity: Activity) =
        viewModelScope.launch {
            SafeGameRequestsCall(activity)
        }

    private suspend fun SafeGameRequestsCall(activity: Activity) {
        gameRequest.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getGameRequests(session.token!!)
                gameRequest.postValue(handleGameRequests(response))
            } else {
                gameRequest.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> gameRequest.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> gameRequest.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }


    }

    private fun handleGameRequests(response: Response<GameRequestModel>): Resources<GameRequestModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                return Resources.Success(gameRequestResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)

    }

    /************************************************************Game Request Delete**************************************************************************************/
    fun DeleteGameRequests(activity: Activity, id: Int) =
        viewModelScope.launch {
            val deleteaGameRequestJson = JsonObject()
            deleteaGameRequestJson.addProperty("id", id)
            SafeDeleteGameRequestsCall(activity, deleteaGameRequestJson)
        }

    private suspend fun SafeDeleteGameRequestsCall(activity: Activity, jsonObject: JsonObject) {
        gameRequestDelete.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.deleteGameRequest(session.token!!, jsonObject)
                gameRequestDelete.postValue(handleDeleteGameRequests(response))
            } else {
                gameRequestDelete.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> gameRequestDelete.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> gameRequestDelete.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleDeleteGameRequests(response: Response<GameRequestDeleteModel>): Resources<GameRequestDeleteModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                return Resources.Success(gameRequestDeleteResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)

    }

}