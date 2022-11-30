package com.app.fuse.ui.friendsMoviematched

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.ui.friendsMoviematched.model.MovieMatchedFriendsModel
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class FriendsMovieMatchedViewModel(val app: Application, val repository: BinjaRepository) :
    AndroidViewModel(app) {
    val friendsMovieMatched = MutableLiveData<Resources<MovieMatchedFriendsModel>>()
    val friendsMovieMatchedResponse: MovieMatchedFriendsModel? = null


    fun FriendsMovieMatched(activity: Activity, accessToken: String, jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeFriendsMovieMatchedCall(activity, accessToken, jsonObject)
        }

    private suspend fun SafeFriendsMovieMatchedCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        friendsMovieMatched.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getFriendsMovieMatched(accessToken, jsonObject)
                friendsMovieMatched.postValue(handleFriendsMovieMatched(response))
            } else {
                friendsMovieMatched.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> friendsMovieMatched.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> friendsMovieMatched.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }


    private fun handleFriendsMovieMatched(response: Response<MovieMatchedFriendsModel>): Resources<MovieMatchedFriendsModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(friendsMovieMatchedResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }
}