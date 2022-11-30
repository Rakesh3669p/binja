package com.app.fuse.ui.mainScreen.profile.editProfile

import android.app.Activity
import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fuse.R
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.*
import com.app.fuse.utils.common.encode
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.ui.mainScreen.profile.model.UpdateProfileResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.util.*


class EditProfileViewModel(val app: Application, val repository: BinjaRepository) :
    AndroidViewModel(app) {

    var filePath = ""
    var uri: Uri? = null

    var updateProfile = SingleLiveEvent<Resources<UpdateProfileResponse>>()
    var updateProfileResponse: UpdateProfileResponse? = null

    lateinit var session: SessionManager

    //validate user info
    fun updateUserProfile(
        activity: Activity,
        username: String,
        age: String,
        designation: String,

    ) {
        /*if (username.isEmpty() || username.length < 3) {
            activity.showToast(activity.getString(R.string.str_validate_email))
        } else if (age.isEmpty() || (age.toInt()) <= 0) {
            activity.showToast(activity.getString(R.string.str_validate_age))
        } else if (designation.isEmpty()) {
            activity.showToast(activity.getString(R.string.str_validate_designation))
        } else {
            updateProfile(activity, username, age, designation )
        }*/
        updateProfile(activity, username, age, designation)

    }

    //update profile Api call
    fun updateProfile(activity: Activity, username: String, age: String, designation: String) =
        viewModelScope.launch {
            session = SessionManager(activity)
            var imageData: String = ""
            if (filePath.isNotEmpty()) {
                imageData = encode(activity, filePath)
            }

            val jsonObject = JSONObject()
            jsonObject.put("username", username)
            jsonObject.put("age", age)
            jsonObject.put("profile", imageData)
            jsonObject.put("designation", designation)
            jsonObject.put("latitude", session.latitude.toString())
            jsonObject.put( "longitude", session.longitude.toString())

            val reformatedObject = jsonObject.toString().replace("\\n", "")
            val requestBody: RequestBody =
                reformatedObject.toRequestBody(Constants.Application_Type.toMediaTypeOrNull())
            SafeUpdateProfileCall(activity, session.token!!, requestBody)
        }


    //update profile response
    private suspend fun SafeUpdateProfileCall(
        activity: Activity,
        accessToken: String,
        jsonObject: RequestBody
    ) {

        updateProfile.postValue(Resources.Loading())
        try {
            if (hasInternetConnection(activity)) {
                val response = repository.updateUserProfile(accessToken, jsonObject)
                updateProfile.postValue(handleUpdateProfile(response))
            } else {
                updateProfile.postValue(
                    Resources.Error(
                        activity.getString(R.string.str_no_internet_connection),
                        null
                    )
                )

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> updateProfile.postValue(
                    Resources.Error(
                        activity.getString(R.string.str_network_failure),
                        null
                    )
                )
                else -> updateProfile.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleUpdateProfile(response: Response<UpdateProfileResponse>): Resources<UpdateProfileResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(updateProfileResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)

    }
}