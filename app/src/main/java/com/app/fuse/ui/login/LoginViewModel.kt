package com.app.fuse.ui.login

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.login.model.LoginModel
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.SingleLiveEvent
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.utils.common.showToast
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class LoginViewModel(val app: Application, val repository: BinjaRepository) : AndroidViewModel(app) {

    val login = SingleLiveEvent<Resources<LoginModel>>()

    var loginResponse: LoginModel? = null
    var  session:SessionManager = SessionManager(app.applicationContext)

    private fun Login(activity: Activity, jsonObject: JsonObject) = viewModelScope.launch {
        SafeLoginCall(activity, jsonObject)
    }

    suspend fun SafeLoginCall(activity: Activity, jsonObject: JsonObject) {
        login.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.login(jsonObject)
                login.postValue(handleLogin(response))
            } else {
                login.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> login.postValue(Resources.Error("Network Failure", null))
                else -> login.postValue(Resources.Error("Conversion Error ${t.message}", null))
            }
        }


    }

    private fun handleLogin(response: Response<LoginModel>): Resources<LoginModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                return Resources.Success(loginResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }


    fun ValidateAndLogin(activity: Activity, userName: String, password: String) {

        if (userName.isEmpty()) {
            activity.showToast("Please Enter Valid Email Or Phone No.")
        } else if (password.isEmpty() && password.length < 6) {
            activity.showToast("Please Enter Valid password.")
        } else {

            val jsonObject = JsonObject()
            jsonObject.addProperty("email", userName)
            jsonObject.addProperty("password", password)
            jsonObject.addProperty("fcm_token", session.fcmToken)
            Login(activity,jsonObject)

        }
    }
}