package com.app.fuse.ui.moviematchedlist

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.ui.moviematchedlist.model.MovieMatchedModel
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MovieMatchedViewModel(val app: Application,val repository: BinjaRepository) :
    AndroidViewModel(app) {
    val movieMatched = MutableLiveData<Resources<MovieMatchedModel>>()
    val movieMatchedResponse: MovieMatchedModel? = null


    fun MovieMatched(activity: Activity, accessToken: String,jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeMovieMatchedCall(activity, accessToken,jsonObject)
        }

    private suspend fun SafeMovieMatchedCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        movieMatched.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getMovieMatched(accessToken, jsonObject)
                movieMatched.postValue(handleMovieMatched(response))
            } else {
                movieMatched.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> movieMatched.postValue(Resources.Error("Network Failure", null))
                else -> movieMatched.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }


    private fun handleMovieMatched(response: Response<MovieMatchedModel>): Resources<MovieMatchedModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(movieMatchedResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

}