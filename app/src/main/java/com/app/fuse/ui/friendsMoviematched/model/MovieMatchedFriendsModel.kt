package com.app.fuse.ui.friendsMoviematched.model

data class MovieMatchedFriendsModel(
    val `data`: List<FriendsMovieMatchedData>,
    val msg: String,
    val status: Boolean
)

data class FriendsMovieMatchedData(
    val created_at: String,
    val id: Int,
    val movie_id: Int,
    val updated_at: String,
    val user: User,
    val user_id: Int,
    val vote: String
)

data class User(
    val age: Any?,
    val created_at: String,
    val id: Int,
    val is_online: Boolean,
    val profile: Any?,
    val username: String
)