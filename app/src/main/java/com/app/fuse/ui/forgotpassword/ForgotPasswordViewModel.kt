package com.app.fuse.ui.forgotpassword

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.forgotpassword.model.ForgotPasswordModel
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SingleLiveEvent
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.utils.common.isEmail
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class ForgotPasswordViewModel(val app: Application, val repository: BinjaRepository) :
    AndroidViewModel(app) {

    val forgotPassword = SingleLiveEvent<Resources<ForgotPasswordModel>>()
    var forgotPasswordResponse: ForgotPasswordModel? = null

   private fun ForgotPassword(activity: Activity, jsonObject: JsonObject) = viewModelScope.launch {
        SafeForgotPasswordCall(activity, jsonObject)
    }

    suspend fun SafeForgotPasswordCall(activity: Activity, jsonObject: JsonObject) {

        forgotPassword.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.forgotPassword(jsonObject)
                forgotPassword.postValue(handleForgotPassword(response))
            } else {
                forgotPassword.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> forgotPassword.postValue(Resources.Error("Network Failure", null))
                else -> forgotPassword.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleForgotPassword(response: Response<ForgotPasswordModel>): Resources<ForgotPasswordModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                return Resources.Success(forgotPasswordResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

    fun validateDataAndForgotPassword(activity: Activity, userEmail: String): Boolean {
        return if (userEmail.isEmail()) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("email", userEmail)
            ForgotPassword(activity,jsonObject)
            true
        } else {
            false
        }
    }

}