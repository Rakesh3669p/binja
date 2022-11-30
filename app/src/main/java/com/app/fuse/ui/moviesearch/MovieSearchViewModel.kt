package com.app.fuse.ui.moviesearch

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SingleLiveEvent
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.ui.moviesearch.model.SearchMovieModel
import com.app.fuse.ui.moviesearch.model.TopMovieMatchesModel
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MovieSearchViewModel(val app: Application, val repository: BinjaRepository) :
    AndroidViewModel(app) {

    val searchMovie = SingleLiveEvent<Resources<SearchMovieModel>>()
    val searchMovieResponse: SearchMovieModel? = null

    val topMatchMovie = MutableLiveData<Resources<TopMovieMatchesModel>>()
    val topMatchMovieResponse: TopMovieMatchesModel? = null

    fun SearchMovies(activity: Activity, accessToken: String, jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeSearchMovieCall(activity, accessToken, jsonObject)
        }

    fun TopMatcheMovies(activity: Activity, accessToken: String) =
        viewModelScope.launch {
            SafeTopMatcheMoviesCall(activity, accessToken)
        }

    private suspend fun SafeSearchMovieCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        searchMovie.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.searchTopMovie(accessToken, jsonObject)
                searchMovie.postValue(handleTopMovieSearch(response))
            } else {
                searchMovie.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchMovie.postValue(Resources.Error("Network Failure", null))
                else -> searchMovie.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private suspend fun SafeTopMatcheMoviesCall(
        activity: Activity,
        accessToken: String,
    ) {
        topMatchMovie.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getTopMatchMovies(accessToken)
                topMatchMovie.postValue(handleTopMatchMovies(response))
            } else {
                topMatchMovie.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> topMatchMovie.postValue(Resources.Error("Network Failure", null))
                else -> topMatchMovie.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleTopMatchMovies(response: Response<TopMovieMatchesModel>): Resources<TopMovieMatchesModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(topMatchMovieResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

    private fun handleTopMovieSearch(response: Response<SearchMovieModel>): Resources<SearchMovieModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(searchMovieResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

}