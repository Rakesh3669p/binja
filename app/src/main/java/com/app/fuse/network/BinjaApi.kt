package com.app.fuse.network

import com.app.fuse.ui.Filter.model.ChannelModel
import com.app.fuse.ui.Filter.model.GenreModel
import com.app.fuse.ui.mainScreen.notification.gameRequests.model.GameRequestDeleteModel
import com.app.fuse.ui.mainScreen.notification.gameRequests.model.GameRequestModel
import com.app.fuse.ui.mainScreen.home.FriendsList.model.FriendListModel
import com.app.fuse.ui.mainScreen.home.FriendsList.model.OnGoingMatchStatusUpdateModel
import com.app.fuse.ui.mainScreen.home.FriendsList.model.UserLocationModel
import com.app.fuse.ui.mainScreen.home.OnGoingmatches.model.OnGoingMatchModel
import com.app.fuse.ui.mainScreen.notification.messages.model.ConversationModel
import com.app.fuse.ui.mainScreen.nearbyusers.model.NearByUsersModel
import com.app.fuse.ui.mainScreen.profile.model.UpdateProfileResponse
import com.app.fuse.ui.mainScreen.search.model.*
import com.app.fuse.ui.SingleUserView.Model.SingleUserModel
import com.app.fuse.ui.chatmodule.model.ChatCountModel
import com.app.fuse.ui.chatmodule.model.ChatHistoryModel
import com.app.fuse.ui.chatmodule.model.CreateMessageModel
import com.app.fuse.ui.forgotpassword.model.ForgotPasswordModel
import com.app.fuse.ui.friendsMoviematched.model.MovieMatchedFriendsModel
import com.app.fuse.ui.homescreen.profile.model.RequestReceivedModel
import com.app.fuse.ui.homescreen.profile.model.RequestSentModel
import com.app.fuse.ui.login.model.LoginModel
import com.app.fuse.ui.mainScreen.profile.model.LogOutModel
import com.app.fuse.ui.moviematchedlist.model.MovieMatchedModel
import com.app.fuse.ui.moviesearch.model.SearchMovieModel
import com.app.fuse.ui.moviesearch.model.TopMovieMatchesModel
import com.app.fuse.ui.movieswipe.model.MatchInProgressDisconnectModel
import com.app.fuse.ui.movieswipe.model.MatchInProgressModel
import com.app.fuse.ui.movieswipe.model.MovieModel
import com.app.fuse.ui.movieswipe.model.MovieVotingModel
import com.app.fuse.ui.signUp.Model.SignUpModel
import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface BinjaApi {

    @POST("auth/login")
    suspend fun login(
        @Body jsonObject: JsonObject
    ): Response<LoginModel>

    @POST("auth/register")
    suspend fun register(
        @Body jsonObject: JsonObject
    ): Response<SignUpModel>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body jsonObject: JsonObject
    ): Response<ForgotPasswordModel>

    @POST("/users/get-user-friends-list")
    suspend fun getFriendsList(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<FriendListModel>

    @POST("/users/update-user-location")
    suspend fun setUserLocation(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<UserLocationModel>

    @GET("/game/all-game-users-list")
    suspend fun getOnGoingMatches(
        @Header("authorization") accessToken: String,
    ): Response<OnGoingMatchModel>

    @POST("/users/get-nearest-users")
    suspend fun getNearByUsers(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<NearByUsersModel>

    @POST("/users/get-users")
    suspend fun searchUsers(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<SearchModel>

    @POST("/users/add-user-recent-searches")
    suspend fun insertSearchedUsers(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<InsertedSearchResponseModel>

    @GET("/users/get-user-recent-searches")
    suspend fun getInsertedSearchedUsers(
        @Header("authorization") accessToken: String,
    ): Response<InsertedSearchedDataModel>

    @POST("/users/update-user-profile")
    suspend fun updateUserProfile(
        @Header("authorization") accessToken: String,
        @Body jsonObject: RequestBody
    ): Response<UpdateProfileResponse>

    @POST("/users/get-user-detail")
    suspend fun getSingleUser(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<SingleUserModel>

    @GET("/movies/get-movies-channels")
    suspend fun getChannelList(
        @Header("authorization") accessToken: String
    ): Response<ChannelModel>


    @GET("/movies/get-movies-types")
    suspend fun getGenreList(
        @Header("authorization") accessToken: String
    ): Response<GenreModel>

    @POST("/movies/get-movies")
    suspend fun getMovies(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<MovieModel>

    @POST("/users/send-friend-request")
    suspend fun sendFriendRequest(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<SendFriendRequestModel>

    @GET("/users/get-receiver-users-list")
    suspend fun getReceivedRequest(
        @Header("authorization") accessToken: String,
    ): Response<RequestReceivedModel>


    @GET("/users/get-request-users-list")
    suspend fun getSentRequest(
        @Header("authorization") accessToken: String,
    ): Response<RequestSentModel>

    @POST("/users/friend-request-action")
    suspend fun friendRequestAction(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<FriendRequestActionModel>

    @POST("/movies/movie-voting")
    suspend fun movieVoting(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<MovieVotingModel>

    @POST("/movies/get-movies")
    suspend fun searchTopMovie(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<SearchMovieModel>

    @GET("/movies/movie-matches")
    suspend fun getTopMatchMovies(
        @Header("authorization") accessToken: String,
    ): Response<TopMovieMatchesModel>

    @POST("/movies/movie-matched-list")
    suspend fun getMovieMatched(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<MovieMatchedModel>

    @POST("/movies/user-like-movie-matched-list")
    suspend fun getFriendsMovieMatched(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<MovieMatchedFriendsModel>

    @POST("/chat/send-message")
    suspend fun sendMessage(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<CreateMessageModel>

    @POST("/chat/chat-history")
    suspend fun getChatHistory(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<ChatHistoryModel>

    @GET("/chat/all-unread-message-count")
    suspend fun getChatCount(
        @Header("authorization") accessToken: String,
    ): Response<ChatCountModel>

    @POST("/chat/get-conversation")
    suspend fun getConversation(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<ConversationModel>

    @GET("/game/all-game-users")
    suspend fun getGameRequests(
        @Header("authorization") accessToken: String,
    ): Response<GameRequestModel>

    @POST("/game/delete")
    suspend fun deleteGameRequests(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<GameRequestDeleteModel>

  @POST("/game/match-in-progress")
    suspend fun matchInProgress(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<MatchInProgressModel>


  @POST("/game/match-in-progress-disconnected")
    suspend fun matchInProgressDisconnect(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<MatchInProgressDisconnectModel>

 @POST("/users/user-ongoing-status-update")
    suspend fun onGoingMatchStatusUpdate(
        @Header("authorization") accessToken: String,
        @Body jsonObject: JsonObject
    ): Response<OnGoingMatchStatusUpdateModel>

    @GET("users/logout")
    suspend fun logOut(
        @Header("authorization") accessToken: String,
    ): Response<LogOutModel>

}