package com.app.fuse.ui.signUp

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.signUp.Model.SignUpModel
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.SingleLiveEvent
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.utils.common.isEmail
import com.app.fuse.utils.common.showToast
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class SignUpViewModel(val app: Application, val repository: BinjaRepository) :
    AndroidViewModel(app) {

    var session: SessionManager = SessionManager(app.applicationContext)
    val signUp = SingleLiveEvent<Resources<SignUpModel>>()
    var registerResponse: SignUpModel? = null


    private fun SignUp(activity: Activity, jsonObject: JsonObject) = viewModelScope.launch {
        SafeRegisterCall(activity, jsonObject)
    }

    private suspend fun SafeRegisterCall(activity: Activity, jsonObject: JsonObject) {
        signUp.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.register(jsonObject)
                signUp.postValue(handleRegister(response))
            } else {
                signUp.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> signUp.postValue(Resources.Error("Network Failure", null))
                else -> signUp.postValue(Resources.Error("Conversion Error ${t.message}", null))
            }
        }


    }

    private fun handleRegister(response: Response<SignUpModel>): Resources<SignUpModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                return Resources.Success(registerResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)

    }


    fun validDataAndSignUp(
        activity: Activity,
        userName: String,
        userEmail: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (userName.isEmpty() || userName.length < 3) {
            activity.showToast("Please Enter a Valid User Name")
            return false
        } else if (!userEmail.isEmail()) {
            activity.showToast("Please Enter a Valid Email Address")
            return false
        } else if (password.isEmpty() || password.length < 6) {
            activity.showToast("Password must be more than 6 characters")
            return false
        } else if (confirmPassword != password) {
            activity.showToast("Password and Confirm Password not matched")
            return false
        } else {
            val jsonObject = JsonObject()
            jsonObject.addProperty("username", userName)
            jsonObject.addProperty("email", userEmail)
            jsonObject.addProperty("password", password)
            jsonObject.addProperty("fcm_token", session.fcmToken)
            SignUp(activity, jsonObject)
            return true
        }
    }
}