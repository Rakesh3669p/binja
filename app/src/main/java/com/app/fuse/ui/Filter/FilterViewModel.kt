package com.app.fuse.ui.Filter

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SingleLiveEvent
import com.app.fuse.utils.common.hasInternetConnection
import com.app.fuse.ui.Filter.model.ChannelModel
import com.app.fuse.ui.Filter.model.GenreModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class FilterViewModel(val app: Application, val repository: BinjaRepository) :
    AndroidViewModel(app) {

    val channelData = SingleLiveEvent<Resources<ChannelModel>>()
    var channelResponse: ChannelModel? = null


    val genreData = SingleLiveEvent<Resources<GenreModel>>()
    var genreResponse: GenreModel? = null


    fun ChannelList(activity: Activity, accessToken: String) =
        viewModelScope.launch {
            SafeChannleListCall(activity, accessToken)
        }

    fun GenreList(activity: Activity, accessToken: String) =
        viewModelScope.launch {
            SafeGenreListCall(activity, accessToken)
        }



    private suspend fun SafeChannleListCall(activity: Activity, accessToken: String) {
        channelData.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getChannelList(accessToken)
                channelData.postValue(handleChannelResponse(response))
            } else {
                channelData.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> channelData.postValue(Resources.Error("Network Failure", null))
                else -> channelData.postValue(Resources.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private suspend fun SafeGenreListCall(activity: Activity, accessToken: String) {
        genreData.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getGenreList(accessToken)
                genreData.postValue(handleGenreResponse(response))
            } else {
                genreData.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> channelData.postValue(Resources.Error("Network Failure", null))
                else -> channelData.postValue(Resources.Error("Conversion Error ${t.message}", null))
            }
        }

    }

    private fun handleGenreResponse(response: Response<GenreModel>): Resources<GenreModel>? {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(genreResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }


    private fun handleChannelResponse(response: Response<ChannelModel>): Resources<ChannelModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(channelResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

}