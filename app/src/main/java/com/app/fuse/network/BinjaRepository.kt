package com.app.fuse.network

import com.app.fuse.db.BinjaDatabase
import com.app.fuse.ui.mainScreen.home.FriendsList.model.FriendDetails
import com.google.gson.JsonObject
import okhttp3.RequestBody

class   BinjaRepository(val db:BinjaDatabase) {
    suspend fun register(jsonObject: JsonObject) =
        RetrofitInstance.api.register(jsonObject)

    suspend fun login(jsonObject: JsonObject) =
        RetrofitInstance.api.login(jsonObject)

    suspend fun forgotPassword(jsonObject: JsonObject) =
        RetrofitInstance.api.forgotPassword(jsonObject)

    suspend fun getFriendsList(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.getFriendsList(accessToken, jsonObject)

    suspend fun setUserLocation(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.setUserLocation(accessToken, jsonObject)

    suspend fun getOnGoingMatches(accessToken: String) =
        RetrofitInstance.api.getOnGoingMatches(accessToken)

    suspend fun getNearByUsers(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.getNearByUsers(accessToken, jsonObject)

    suspend fun searchUsers(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.searchUsers(accessToken, jsonObject)

    suspend fun insertSearchedUsers(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.insertSearchedUsers(accessToken, jsonObject)

    suspend fun getInsertedSearchedUsers(accessToken: String) =
        RetrofitInstance.api.getInsertedSearchedUsers(accessToken)

    suspend fun getSingleUser(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.getSingleUser(accessToken, jsonObject)

    suspend fun getChannelList(accessToken: String) =
        RetrofitInstance.api.getChannelList(accessToken)

    suspend fun getGenreList(accessToken: String) =
        RetrofitInstance.api.getGenreList(accessToken)

    suspend fun getMovies(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.getMovies(accessToken, jsonObject)

    suspend fun sendFriendRequest(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.sendFriendRequest(accessToken, jsonObject)

    suspend fun updateUserProfile(accessToken: String, jsonObject: RequestBody) =
        RetrofitInstance.api.updateUserProfile(accessToken, jsonObject)

    suspend fun getReceivedRequest(accessToken: String) =
        RetrofitInstance.api.getReceivedRequest(accessToken)

    suspend fun getSentRequest(accessToken: String) =
        RetrofitInstance.api.getSentRequest(accessToken)

    suspend fun friendRequestAction(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.friendRequestAction(accessToken, jsonObject)

    suspend fun movieVoting(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.movieVoting(accessToken, jsonObject)


    suspend fun searchTopMovie(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.searchTopMovie(accessToken, jsonObject)

    suspend fun getTopMatchMovies(accessToken: String) =
        RetrofitInstance.api.getTopMatchMovies(accessToken)

    suspend fun getMovieMatched(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.getMovieMatched(accessToken, jsonObject)

    suspend fun getFriendsMovieMatched(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.getFriendsMovieMatched(accessToken, jsonObject)

    suspend fun getChatHistory(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.getChatHistory(accessToken, jsonObject)

    suspend fun getConversation(accessToken: String, jsonObject: JsonObject) =
        RetrofitInstance.api.getConversation(accessToken, jsonObject)

    suspend fun getChatCount(accessToken: String) =
        RetrofitInstance.api.getChatCount(accessToken)

    suspend fun getGameRequests(accessToken: String) =
        RetrofitInstance.api.getGameRequests(accessToken)

    suspend fun deleteGameRequest(accessToken: String,jsonObject: JsonObject) =
        RetrofitInstance.api.deleteGameRequests(accessToken,jsonObject)

     suspend fun sendMessage(accessToken: String,jsonObject: JsonObject) =
        RetrofitInstance.api.sendMessage(accessToken,jsonObject)


    suspend fun matchInProgress(accessToken: String,jsonObject: JsonObject) =
        RetrofitInstance.api.matchInProgress(accessToken,jsonObject)

 suspend fun matchInProgressDisconnect(accessToken: String,jsonObject: JsonObject) =
        RetrofitInstance.api.matchInProgressDisconnect(accessToken,jsonObject)

 suspend fun onGoingMatchStatusUpdate(accessToken: String,jsonObject: JsonObject) =
        RetrofitInstance.api.onGoingMatchStatusUpdate(accessToken,jsonObject)


    suspend fun logOut(accessToken: String) =
        RetrofitInstance.api.logOut(accessToken)


    /***************** For Local Database ******************************/
    suspend fun insertOnGoingFriends(onGoingMatchFriends: FriendDetails) =
        db.getBinjaDao().insertOnGoingMatchFriends(onGoingMatchFriends)

    fun getOnGoingFriends() = db.getBinjaDao().getOnGoingMatchFriends()

    fun deleteOnGoingFriends() = db.getBinjaDao().getOnGoingMatchFriends()
}