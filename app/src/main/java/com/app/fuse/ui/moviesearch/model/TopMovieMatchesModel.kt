package com.app.fuse.ui.moviesearch.model

data class TopMovieMatchesModel(
    val data: List<TopMovieMatchData>,
    val msg: String,
    val status: Boolean
)

data class TopMovieMatchData(
    val created_at: String,
    val deleted_at: Any?,
    val id: Int,
    val movi_subscribe_channel: String,
    val movi_subscribe_channel_id: Int,
    val movi_type_id: Int,
    val movie_description: String,
    val movie_image: String,
    val movie_name: String,
    val movie_rating: String,
    val movie_subscribe_channel: Int,
    val movie_title: String?,
    val movie_type: Int,
    val movies_type_name: String,
    val updated_at: String,
    val votes: Int
)