package com.app.fuse.ui.movieswipe

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.mainScreen.home.FriendsList.model.OnGoingMatchStatusUpdateModel
import com.app.fuse.ui.mainScreen.notification.gameRequests.model.GameRequestDeleteModel
import com.app.fuse.ui.movieswipe.model.MatchInProgressDisconnectModel
import com.app.fuse.ui.movieswipe.model.MatchInProgressModel
import com.app.fuse.ui.movieswipe.model.MovieModel
import com.app.fuse.ui.movieswipe.model.MovieVotingModel
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.SingleLiveEvent
import com.app.fuse.utils.common.hasInternetConnection
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MovieViewModel(val app: Application, val repository: BinjaRepository) :
    AndroidViewModel(app) {

    val session = SessionManager(app.applicationContext)

    val movieData = MutableLiveData<Resources<MovieModel>>()
    var movieResponse: MovieModel? = null

    val movieVoting = MutableLiveData<Resources<MovieVotingModel>>()
    var movieVotingResponse: MovieVotingModel? = null

    val matchInProgress = MutableLiveData<Resources<MatchInProgressModel>>()
    var matchInProgressResponse: MatchInProgressModel? = null

    val matchInProgressDisconnect = MutableLiveData<Resources<MatchInProgressDisconnectModel>>()
    var matchInProgressDisconnectResponse: MatchInProgressDisconnectModel? = null

    val onGoingMatchStatus = SingleLiveEvent<Resources<OnGoingMatchStatusUpdateModel>>()
    var onGoingMatchStatusResponse: OnGoingMatchStatusUpdateModel? = null

    val gameRequestDelete = SingleLiveEvent<Resources<GameRequestDeleteModel>>()
    val gameRequestDeleteResponse: GameRequestDeleteModel? = null


    /***************************************************Movie Listing Api**********************************************************************/

    fun MovieList(activity: Activity, jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeMovieListCall(activity, session.token.toString(), jsonObject)
        }


    private suspend fun SafeMovieListCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        movieData.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.getMovies(accessToken, jsonObject)
                movieData.postValue(handleMovieResponse(response))
            } else {
                movieData.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> movieData.postValue(Resources.Error("Network Failure", null))
                else -> movieData.postValue(Resources.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleMovieResponse(response: Response<MovieModel>): Resources<MovieModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(movieResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }

    /***************************************************Movie Voting Api******************************************************************************************************/

    fun MovieVoting(activity: Activity,  jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeMovieVotingCall(activity, session.token.toString(), jsonObject)
        }


    private suspend fun SafeMovieVotingCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        movieVoting.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.movieVoting(accessToken, jsonObject)
                movieVoting.postValue(handleMovieVotingResponse(response))
            } else {
                movieVoting.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> movieVoting.postValue(Resources.Error("Network Failure", null))
                else -> movieVoting.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleMovieVotingResponse(response: Response<MovieVotingModel>): Resources<MovieVotingModel>? {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(movieVotingResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }


    /***************************************************Movie Voting Api******************************************************************************************************/

    fun MatchInProgress(activity: Activity,  jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeMatchInProgressCall(activity,session.token.toString(), jsonObject)
        }


    private suspend fun SafeMatchInProgressCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        matchInProgress.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.matchInProgress(accessToken, jsonObject)
                matchInProgress.postValue(handleMatchInProgressResponse(response))
            } else {
                matchInProgress.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> matchInProgress.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> matchInProgress.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleMatchInProgressResponse(response: Response<MatchInProgressModel>): Resources<MatchInProgressModel>? {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(matchInProgressResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }


    /***************************************************Movie Voting Api******************************************************************************************************/

    fun MatchInProgressDisconnect(activity: Activity, jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeMatchInProgressDisconnectCall(activity, session.token.toString(), jsonObject)
        }


    private suspend fun SafeMatchInProgressDisconnectCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        matchInProgressDisconnect.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.matchInProgressDisconnect(accessToken, jsonObject)
                matchInProgressDisconnect.postValue(handleMatchInProgressDisconnectResponse(response))
            } else {
                matchInProgressDisconnect.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> matchInProgressDisconnect.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> matchInProgressDisconnect.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleMatchInProgressDisconnectResponse(response: Response<MatchInProgressDisconnectModel>): Resources<MatchInProgressDisconnectModel>? {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(matchInProgressDisconnectResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }


    /***************************************************On Going Match Api******************************************************************************************************/

    fun OnGoingMatchStatusUpdate(activity: Activity, jsonObject: JsonObject) =
        viewModelScope.launch {
            SafeOnGoingMatchStatusUpdateCall(activity, session.token.toString(), jsonObject)
        }


    private suspend fun SafeOnGoingMatchStatusUpdateCall(
        activity: Activity,
        accessToken: String,
        jsonObject: JsonObject
    ) {
        onGoingMatchStatus.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.onGoingMatchStatusUpdate(accessToken, jsonObject)
                onGoingMatchStatus.postValue(handleOnGoingMatchResponse(response))
            } else {
                onGoingMatchStatus.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> onGoingMatchStatus.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> onGoingMatchStatus.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleOnGoingMatchResponse(response: Response<OnGoingMatchStatusUpdateModel>): Resources<OnGoingMatchStatusUpdateModel>? {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(onGoingMatchStatusResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)
    }


    /************************************************************Game Request Delete**************************************************************************************/
    fun DeleteGameRequests(activity: Activity, id: Int) =
        viewModelScope.launch {
            val deleteaGameRequestJson = JsonObject()
            deleteaGameRequestJson.addProperty("id", id)
            SafeDeleteGameRequestsCall(activity, deleteaGameRequestJson)
        }

    private suspend fun SafeDeleteGameRequestsCall(activity: Activity, jsonObject: JsonObject) {
        gameRequestDelete.postValue(Resources.Loading())

        try {
            if (hasInternetConnection(activity)) {
                val response = repository.deleteGameRequest(session.token!!, jsonObject)
                gameRequestDelete.postValue(handleDeleteGameRequests(response))
            } else {
                gameRequestDelete.postValue(Resources.Error("No Internet Connection", null))

            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> gameRequestDelete.postValue(
                    Resources.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> gameRequestDelete.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleDeleteGameRequests(response: Response<GameRequestDeleteModel>): Resources<GameRequestDeleteModel>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                return Resources.Success(gameRequestDeleteResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message(), null)

    }

    //Local Database
    fun getOnGoingMatchesFriends() = repository.getOnGoingFriends()


}

