package com.app.fuse.ui.chatmodule

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.SingleLiveEvent
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.ui.chatmodule.model.ChatCountModel
import com.app.fuse.ui.chatmodule.model.ChatHistoryModel
import com.app.fuse.ui.chatmodule.model.CreateMessageModel
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException


class ChatHistoryViewModel(val app: Application, val repository: BinjaRepository) :
    AndroidViewModel(app) {


    var session: SessionManager = SessionManager(app)

    val chatHistoryData = SingleLiveEvent<Resources<ChatHistoryModel>>()
    var chatHistoryResponse: ChatHistoryModel? = null

    val createMessage = SingleLiveEvent<Resources<CreateMessageModel>>()
    var createMessageResponse: CreateMessageModel? = null

    val chatCount = SingleLiveEvent<Resources<ChatCountModel>>()
    var chatCountResponse: ChatCountModel? = null

    /************************************Create Message*********************************************************/
    fun CreateMessage(activity: Activity, accessToken: String, jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeCreateMessageCall(activity, accessToken, jsonObject)
        }


    private suspend fun SafeCreateMessageCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        createMessage.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.sendMessage(accessToken, jsonObject)
                createMessage.postValue(handleCreateMessageResponse(response))
            } else {
                createMessage.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> createMessage.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> createMessage.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleCreateMessageResponse(response: Response<CreateMessageModel>): Resources<CreateMessageModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(createMessageResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }


/************************************CHAT History*********************************************************/
    fun ChatHistory(activity: Activity, accessToken: String, jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeChatHistoryCall(activity, accessToken, jsonObject)
        }


    private suspend fun SafeChatHistoryCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        chatHistoryData.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getChatHistory(accessToken, jsonObject)
                chatHistoryData.postValue(handleChatHistoryResponse(response))
            } else {
                chatHistoryData.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> chatHistoryData.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> chatHistoryData.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleChatHistoryResponse(response: Response<ChatHistoryModel>): Resources<ChatHistoryModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(chatHistoryResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

    /************************************CHAT COUNT *********************************************************/
  fun ChatCount(activity: Activity) =
        viewModelScope.launch {
            SafeChatCountCall(activity,session.token!!)
        }


    private suspend fun SafeChatCountCall(
        activity: Activity,
        accessToken: String
    ) {
        chatCount.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getChatCount(accessToken)
                chatCount.postValue(handleChatCountResponse(response))
            } else {
                chatCount.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> chatCount.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> chatCount.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleChatCountResponse(response: Response<ChatCountModel>): Resources<ChatCountModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(chatCountResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

}