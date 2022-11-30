package com.app.fuse.ui.homescreen.messages

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.SingleLiveEvent
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.ui.mainScreen.notification.messages.model.ConversationModel
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MessageViewModel(val app: Application, val repository: BinjaRepository) :
    AndroidViewModel(app) {
    var session: SessionManager = SessionManager(app.applicationContext)
    val conversation = SingleLiveEvent<Resources<ConversationModel>>()
    var conversationResponse: ConversationModel? = null

    /************************************CHAT History*********************************************************/
    fun Conversation(activity: Activity,jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeConversationCall(activity,session.token!!,jsonObject)
        }


    private suspend fun SafeConversationCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        conversation.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity )) {
                val response = repository.getConversation(accessToken,jsonObject)
                conversation.postValue(handleConversationResponse(response))
            } else {
                conversation.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> conversation.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> conversation.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleConversationResponse(response: Response<ConversationModel>): Resources<ConversationModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(conversationResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

}